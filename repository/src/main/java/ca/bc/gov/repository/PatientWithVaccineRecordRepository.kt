package ca.bc.gov.repository

import ca.bc.gov.common.model.DataSource
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.vaccine.VaccineDoseRepository
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientWithVaccineRecordRepository @Inject constructor(
    private val patientRepository: PatientRepository,
    private val vaccineRecordRepository: VaccineRecordRepository,
    private val vaccineDoseRepository: VaccineDoseRepository
) {

    /**
     * Insert Vaccine Record [PatientVaccineRecord]
     * @param patientVaccineRecord
     * @return inserted recordId or -1L
     */
    suspend fun insertPatientsVaccineRecord(patientVaccineRecord: PatientVaccineRecord): Long {
        val patientId =
            patientRepository.insert(patientVaccineRecord.patientDto)
        patientVaccineRecord.vaccineRecordDto.patientId = patientId
        val vaccineRecordId = vaccineRecordRepository.insertVaccineRecord(
            patientVaccineRecord.vaccineRecordDto
        )
        patientVaccineRecord.vaccineRecordDto.doseDtos.forEach { vaccineDose ->
            vaccineDose.vaccineRecordId = vaccineRecordId
        }
        vaccineDoseRepository.insertAllVaccineDose(
            vaccineRecordId,
            patientVaccineRecord.vaccineRecordDto.doseDtos
        )
        return patientId
    }

    suspend fun insertAuthenticatedPatientsVaccineRecord(
        patientId: Long,
        patientVaccineRecord: PatientVaccineRecord
    ): Long {
        patientVaccineRecord.vaccineRecordDto.patientId = patientId
        patientVaccineRecord.vaccineRecordDto.mode = DataSource.BCSC
        val vaccineRecordId = vaccineRecordRepository.insertAuthenticatedVaccineRecord(
            patientVaccineRecord.vaccineRecordDto
        )
        patientVaccineRecord.vaccineRecordDto.doseDtos.forEach { vaccineDose ->
            vaccineDose.vaccineRecordId = vaccineRecordId
        }
        vaccineDoseRepository.insertAllAuthenticatedVaccineDose(patientVaccineRecord.vaccineRecordDto.doseDtos)
        return patientId
    }

    suspend fun updatePatientVaccineRecord(patientVaccineRecord: PatientVaccineRecord): Long {
        val patientId =
            patientRepository.update(patientVaccineRecord.patientDto)
        val vaccineRecordId = vaccineRecordRepository.getVaccineRecordId(patientId) ?: return -1L
        vaccineDoseRepository.deleteVaccineDose(vaccineRecordId)
        val vaccineRecord = patientVaccineRecord.vaccineRecordDto
        vaccineRecord.patientId = patientId
        vaccineRecord.id = vaccineRecordId
        val id = vaccineRecordRepository.updateVaccineRecord(
            patientVaccineRecord.vaccineRecordDto
        )
        println("id = $id")
        patientVaccineRecord.vaccineRecordDto.doseDtos.forEach { vaccineDose ->
            vaccineDose.vaccineRecordId = vaccineRecordId
        }

        vaccineDoseRepository.insertAllVaccineDose(
            vaccineRecordId,
            patientVaccineRecord.vaccineRecordDto.doseDtos
        )
        return patientId
    }
}
