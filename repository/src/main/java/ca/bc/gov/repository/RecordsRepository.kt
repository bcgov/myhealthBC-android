package ca.bc.gov.repository

import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.model.immunization.ImmunizationDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.repository.hospitalvisit.HospitalVisitRepository
import ca.bc.gov.repository.immunization.ImmunizationForecastRepository
import ca.bc.gov.repository.immunization.ImmunizationRecommendationRepository
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.labtest.LabTestRepository
import ca.bc.gov.repository.model.PatientVaccineRecordsState
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.testrecord.CovidTestRepository
import javax.inject.Inject

class RecordsRepository @Inject constructor(
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val covidOrderRepository: CovidOrderRepository,
    private val covidTestRepository: CovidTestRepository,
    private val labOrderRepository: LabOrderRepository,
    private val labTestRepository: LabTestRepository,
    private val immunizationRecordRepository: ImmunizationRecordRepository,
    private val immunizationForecastRepository: ImmunizationForecastRepository,
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
    private val hospitalVisitRepository: HospitalVisitRepository,
) {

    suspend fun storeVaccineRecords(
        vaccineRecords: List<PatientVaccineRecordsState?>,
    ) {
        vaccineRecords.forEach { response ->
            response?.patientVaccineRecord?.let {
                try {
                    patientWithVaccineRecordRepository.insertAuthenticatedPatientsVaccineRecord(
                        response.patientId, it
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun storeCovidOrders(
        patientId: Long,
        covidOrderResponse: List<CovidOrderWithCovidTestDto>?,
    ) {
        patientWithTestResultRepository.deletePatientTestRecords(patientId)
        covidOrderRepository.deleteByPatientId(patientId)
        covidOrderResponse?.forEach {
            it.covidOrder.patientId = patientId
            covidOrderRepository.insert(it.covidOrder)
            covidTestRepository.insert(it.covidTests)
        }
    }

    suspend fun storeLabOrders(
        patientId: Long,
        labOrdersResponse: List<LabOrderWithLabTestDto>?,
    ) {
        labOrderRepository.delete(patientId)
        labOrdersResponse?.forEach {
            it.labOrder.patientId = patientId
            val id = labOrderRepository.insert(it.labOrder)
            it.labTests.forEach { test ->
                test.labOrderId = id
            }
            labTestRepository.insert(it.labTests)
        }
    }

    suspend fun storeImmunizationRecords(
        patientId: Long,
        immunizationDto: ImmunizationDto?
    ) {
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
    }
    
    suspend fun storeHospitalVisits(patientId: Long, hospitalVisits: List<HospitalVisitDto>?) {
        hospitalVisitRepository.deleteHospitalVisits(patientId)
        hospitalVisits?.let { list ->
            list.forEach { it.patientId = patientId }
            hospitalVisitRepository.insertHospitalVisits(list)
        }
    }
}
