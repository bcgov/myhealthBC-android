package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.common.model.relation.PatientAndVaccineRecord
import ca.bc.gov.data.local.dao.PatientWithVaccineRecordDao
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

    suspend fun getPatientWithVaccineRecord(patient: Patient) =
        dao.getPatientsWithVaccine(patient.firstName.uppercase(), patient.lastName.uppercase(), patient.dateOfBirth)

    suspend fun getPatientWithVaccineRecord(patientId: Long): PatientAndVaccineRecord? =
        dao.getPatientWithVaccine(patientId)?.toDto()
}