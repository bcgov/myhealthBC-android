package ca.bc.gov.repository

import android.util.Log
import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.const.SERVICE_NOT_AVAILABLE
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.immunization.ImmunizationDto
import ca.bc.gov.common.model.patient.PatientWithCovidOrderAndTestDto
import ca.bc.gov.common.model.patient.PatientWithImmunizationRecordAndForecastDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.data.datasource.local.DependentsLocalDataSource
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.remote.DependentsRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import ca.bc.gov.data.model.mapper.toPatientEntity
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.extensions.mapFlowContent
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.model.PatientVaccineRecordsState
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DependentsRepository @Inject constructor(
    private val remoteDataSource: DependentsRemoteDataSource,
    private val localDataSource: DependentsLocalDataSource,
    private val patientLocalDataSource: PatientLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
    private val covidOrderRepository: CovidOrderRepository,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val immunizationRecordRepository: ImmunizationRecordRepository,
    private val recordsRepository: RecordsRepository,
    private val mobileConfigRepository: MobileConfigRepository,
) {

    fun getAllDependents(): Flow<List<DependentDto>> =
        localDataSource.getAllDependents().mapFlowContent {
            it.toDto()
        }

    suspend fun fetchAllDependents(token: String, hdid: String): List<DependentDto> =
        remoteDataSource.fetchAllDependents(hdid, token).map { it.toDto() }

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
        return localDataSource.findDependentByPhn(phn) != null
    }

    suspend fun requestRecordsIfNeeded(patientId: Long, hdid: String) {
        if (localDataSource.isDependentCacheValid(patientId).not()) {

            val serviceAvailable = mobileConfigRepository.refreshMobileConfiguration()
            if (serviceAvailable.not()) {
                throw MyHealthException(SERVICE_NOT_AVAILABLE)
            }

            var vaccineRecords: Pair<VaccineRecordState, PatientVaccineRecord?>? = null
            var covidOrders: List<CovidOrderWithCovidTestDto>? = null
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
                immunizationDto = immunizationRecordRepository.fetchImmunization(token, hdid)
            } catch (e: Exception) {
                handleException(e)
            }

            storeRecords(patientId, vaccineRecords, covidOrders, immunizationDto)
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
        immunizationDto: ImmunizationDto?
    ) {

        // Insert vaccine records
        recordsRepository.storeVaccineRecords(
            vaccineRecordsResponse?.let {
                listOf(
                    PatientVaccineRecordsState(
                        patientId = patientId,
                        vaccineRecordState = it.first,
                        patientVaccineRecord = it.second,
                    )
                )
            } ?: listOf()
        )

        // Insert covid orders
        recordsRepository.storeCovidOrders(patientId, covidOrderResponse)

        // Insert immunization records
        recordsRepository.storeImmunizationRecords(patientId, immunizationDto)

        localDataSource.enableDependentCacheFlag(patientId)
    }

    suspend fun getPatientWithTestResultsAndRecords(patientId: Long): PatientWithTestResultsAndRecordsDto =
        patientLocalDataSource.getPatientWithTestResultsAndRecords(patientId)
            ?: throw getDatabaseException(patientId)

    suspend fun getPatientWithCovidOrdersAndCovidTests(patientId: Long): PatientWithCovidOrderAndTestDto =
        patientLocalDataSource.getPatientWithCovidOrderAndCovidTests(patientId)
            ?: throw getDatabaseException(patientId)

    suspend fun getPatientWithImmunizationRecordAndForecast(patientId: Long): PatientWithImmunizationRecordAndForecastDto =
        patientLocalDataSource.getPatientWithImmunizationRecordAndForecast(patientId)
            ?: throw getDatabaseException(patientId)

    suspend fun updateDependentListOrder(list: List<DependentDto>) {
        localDataSource.deleteAllDependentListOrders()

        list.forEachIndexed { index, dependentDto ->
            localDataSource.insertDependentListOrder(dependentDto.hdid, index)
        }
    }

    suspend fun getDependent(patientId: Long): DependentDto =
        localDataSource.findDependent(patientId)?.toDto()
            ?: throw getDatabaseException(patientId)

    suspend fun getDependentByPhn(phn: String): DependentDto =
        localDataSource.findDependentByPhn(phn)?.toDto()
            ?: throw getDatabaseException(phn)

    suspend fun deleteDependent(patientId: Long) {
        val dependentDto = getDependent(patientId)
        deleteDependent(dependentDto)
    }

    suspend fun deleteDependent(dependentDto: DependentDto) {
        val authParams = bcscAuthRepo.getAuthParametersDto()

        remoteDataSource.deleteDependent(
            guardianHdid = authParams.hdid,
            dependentHdid = dependentDto.hdid,
            accessToken = authParams.token,
            dependentDto = dependentDto
        )
        localDataSource.deleteDependent(dependentDto.patientId)
    }

    private fun getDatabaseException(patientId: Long) =
        MyHealthException(DATABASE_ERROR, "No record found for patient id=  $patientId")

    private fun getDatabaseException(phn: String) =
        MyHealthException(DATABASE_ERROR, "No record found for phn=  $phn")
}
