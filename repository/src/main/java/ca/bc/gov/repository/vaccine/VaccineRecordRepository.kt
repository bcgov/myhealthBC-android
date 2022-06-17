package ca.bc.gov.repository.vaccine

import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.data.datasource.local.VaccineRecordLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class VaccineRecordRepository @Inject constructor(
    private val vaccineRecordLocalDataSource: VaccineRecordLocalDataSource
) {

    suspend fun insertVaccineRecord(vaccineRecordDto: VaccineRecordDto): Long =
        vaccineRecordLocalDataSource.insert(vaccineRecordDto)

    suspend fun updateVaccineRecord(vaccineRecordDtoDto: VaccineRecordDto): Int =
        vaccineRecordLocalDataSource.update(vaccineRecordDtoDto)

    suspend fun getVaccineRecordId(patientId: Long): Long? =
        vaccineRecordLocalDataSource.getVaccineRecordId(patientId)

    suspend fun delete(vaccineRecordId: Long): Int =
        vaccineRecordLocalDataSource.delete(vaccineRecordId)

    suspend fun insertAuthenticatedVaccineRecord(vaccineRecordDto: VaccineRecordDto): Long =
        vaccineRecordLocalDataSource.insertAuthenticatedVaccineRecord(vaccineRecordDto)

    suspend fun insertAllAuthenticatedVaccineDose(doses: List<VaccineDoseDto>): List<Long> =
        vaccineRecordLocalDataSource.insertAllAuthenticatedVaccineDose(doses)

    suspend fun insertAllVaccineDose(doses: List<VaccineDoseDto>): List<Long> =
        vaccineRecordLocalDataSource.insert(doses)

    suspend fun insertAllVaccineDose(id: Long, doses: List<VaccineDoseDto>): List<Long> =
        vaccineRecordLocalDataSource.insertAllVaccineDoses(id, doses)

    suspend fun insertVaccineDose(dose: VaccineDoseDto): Long =
        vaccineRecordLocalDataSource.insert(dose)

    suspend fun deleteVaccineDose(vaccineRecordId: Long): Int =
        vaccineRecordLocalDataSource.deleteVaccineDose(vaccineRecordId)

    suspend fun deletePatientVaccineRecords(patientId: Long) =
        vaccineRecordLocalDataSource.deletePatientVaccineRecords(patientId)
}
