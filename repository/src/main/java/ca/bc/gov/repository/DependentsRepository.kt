package ca.bc.gov.repository

import android.util.Log
import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.immunization.ImmunizationDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.patient.PatientWithCovidOrderAndTestDto
import ca.bc.gov.common.model.patient.PatientWithImmunizationRecordAndForecastDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.data.datasource.local.DependentsLocalDataSource
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.DependentsRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import ca.bc.gov.data.model.mapper.toPatientEntity
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.extensions.mapFlowContent
import ca.bc.gov.repository.immunization.ImmunizationForecastRepository
import ca.bc.gov.repository.immunization.ImmunizationRecommendationRepository
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.labtest.LabTestRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.testrecord.CovidTestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DependentsRepository @Inject constructor(
    private val remoteDataSource: DependentsRemoteDataSource,
    private val localDataSource: DependentsLocalDataSource,
    private val patientLocalDataSource: PatientLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
    private val preferenceStorage: EncryptedPreferenceStorage,
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val covidOrderRepository: CovidOrderRepository,
    private val covidTestRepository: CovidTestRepository,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val labOrderRepository: LabOrderRepository,
    private val labTestRepository: LabTestRepository,
    private val immunizationRecordRepository: ImmunizationRecordRepository,
    private val immunizationForecastRepository: ImmunizationForecastRepository,
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
) {

    fun getAllDependents(): Flow<List<DependentDto>> =
        localDataSource.getAllDependents().mapFlowContent {
            it.toDto()
        }

    suspend fun fetchAllDependents(token: String, hdid: String): List<DependentDto> {
        return remoteDataSource.fetchAllDependents(hdid, token).map {
            it.dependentInformation.toDto()
        }
    }

    suspend fun storeDependents(dependents: List<DependentDto>, guardianId: Long) {
        localDataSource.clearTables()

        dependents.forEach { dependentDto ->
            val patientId = localDataSource.insertPatient(dependentDto.toPatientEntity())
            localDataSource.insertDependent(dependentDto.toEntity(patientId, guardianId))
        }
    }

    suspend fun registerDependent(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phn: String,
    ) {
        val authParameters = bcscAuthRepo.getAuthParametersDto()

        val guardianId = patientLocalDataSource.getAuthenticatedPatientId()

        val dependentDto = remoteDataSource.addDependent(
            authParameters.hdid,
            firstName,
            lastName,
            dateOfBirth,
            phn,
            authParameters.token,
        ).toDto()

        val patientId = localDataSource.insertPatient(dependentDto.toPatientEntity())
        localDataSource.insertDependent(dependentDto.toEntity(patientId, guardianId))
    }

    suspend fun checkDuplicateRecord(phn: String): Boolean {
        val count = localDataSource.findDependent(phn).size
        return count > 0
    }

    suspend fun requestRecordsIfNeeded(patientId: Long, hdid: String) {
        if (localDataSource.isDependentCacheValid(patientId).not()) {
            var vaccineRecords: Pair<VaccineRecordState, PatientVaccineRecord?>? = null
            var covidOrders: List<CovidOrderWithCovidTestDto>? = null
            var labOrders: List<LabOrderWithLabTestDto>? = null
            var immunizationDto: ImmunizationDto? = null

            val token = bcscAuthRepo.getAuthParametersDto().token

            try {
                vaccineRecords = fetchVaccineRecordRepository.fetchVaccineRecord(token, hdid)
            } catch (e: Exception) {
                handleException(e)
            }

            try {
                covidOrders = covidOrderRepository.fetchCovidOrders(token, hdid)
            } catch (e: Exception) {
                handleException(e)
            }

            try {
                labOrders = labOrderRepository.fetchLabOrders(token, hdid)
            } catch (e: Exception) {
                handleException(e)
            }

            try {
                immunizationDto = immunizationRecordRepository.fetchImmunization(token, hdid)
            } catch (e: Exception) {
                handleException(e)
            }

            storeRecords(patientId, vaccineRecords, covidOrders, labOrders, immunizationDto)
        }
    }

    private fun handleException(exception: Exception) {
        Log.e("DependentsRepository", "Handling Exception:")
        exception.printStackTrace()
    }

    private suspend fun storeRecords(
        patientId: Long,
        vaccineRecordsResponse: Pair<VaccineRecordState, PatientVaccineRecord?>?,
        covidOrderResponse: List<CovidOrderWithCovidTestDto>?,
        labOrdersResponse: List<LabOrderWithLabTestDto>?,
        immunizationDto: ImmunizationDto?
    ) {

        // Insert vaccine records
        vaccineRecordsResponse?.second?.let {
            patientWithVaccineRecordRepository.insertAuthenticatedPatientsVaccineRecord(
                patientId, it
            )
        }
        // Insert covid orders
        patientWithTestResultRepository.deletePatientTestRecords(patientId)
        covidOrderRepository.deleteByPatientId(patientId)
        covidOrderResponse?.forEach {
            it.covidOrder.patientId = patientId
            covidOrderRepository.insert(it.covidOrder)
            covidTestRepository.insert(it.covidTests)
        }

        // Insert lab orders
        labOrderRepository.delete(patientId)
        labOrdersResponse?.forEach {
            it.labOrder.patientId = patientId
            val id = labOrderRepository.insert(it.labOrder)
            it.labTests.forEach { test ->
                test.labOrderId = id
            }
            labTestRepository.insert(it.labTests)
        }
        // Insert immunization records
        immunizationRecordRepository.delete(patientId)

        immunizationDto?.records?.forEach {
            it.immunizationRecord.patientId = patientId
            val id = immunizationRecordRepository.insert(it.immunizationRecord)

            it.immunizationForecast?.immunizationRecordId = id
            it.immunizationForecast?.let { forecast ->
                immunizationForecastRepository.insert(
                    forecast
                )
            }
        }

        immunizationDto?.recommendations?.forEach {
            it.patientId = patientId
            immunizationRecommendationRepository.insert(it)
        }

        localDataSource.enableDependentCacheFlag(patientId)
    }

    suspend fun getPatientWithTestResultsAndRecords(patientId: Long): PatientWithTestResultsAndRecordsDto =
        patientLocalDataSource.getPatientWithTestResultsAndRecords(patientId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient id=  $patientId"
            )

    suspend fun getPatientWithCovidOrdersAndCovidTests(patientId: Long): PatientWithCovidOrderAndTestDto =
        patientLocalDataSource.getPatientWithCovidOrderAndCovidTests(patientId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient id=  $patientId"
            )

    suspend fun getPatientWithImmunizationRecordAndForecast(patientId: Long): PatientWithImmunizationRecordAndForecastDto =
        patientLocalDataSource.getPatientWithImmunizationRecordAndForecast(patientId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient id=  $patientId"
            )
}
