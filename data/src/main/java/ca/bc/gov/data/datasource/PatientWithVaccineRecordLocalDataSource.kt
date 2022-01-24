package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientWithVaccineRecordDto
import ca.bc.gov.data.local.dao.PatientWithVaccineRecordDao
import ca.bc.gov.data.local.entity.PatientOrderUpdate
import ca.bc.gov.data.local.entity.relations.PatientWithVaccineRecord
import ca.bc.gov.data.model.mapper.toDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientWithVaccineRecordLocalDataSource @Inject constructor(
    private val dao: PatientWithVaccineRecordDao
) {

    val patientsVaccineRecord: Flow<List<PatientWithVaccineRecord>> =
        dao.getPatientsWithVaccineFlow()

    suspend fun getPatientWithVaccineRecord(patientDto: PatientDto) =
        dao.getPatientsWithVaccine(
            patientDto.firstName,
            patientDto.lastName,
            patientDto.dateOfBirth
        )

    suspend fun getPatientWithVaccineRecord(patientId: Long): PatientWithVaccineRecordDto? =
        dao.getPatientWithVaccine(patientId)?.toDto()

    suspend fun getVaccineDoses(vaccineRecordId: Long): List<VaccineDoseDto> =
        dao.getVaccineDoses(vaccineRecordId).map { it.toDto() }

    suspend fun updatePatientOrder(patientOrderUpdates: List<PatientOrderUpdate>) =
        dao.updatePatientOrder(patientOrderUpdates)
}
