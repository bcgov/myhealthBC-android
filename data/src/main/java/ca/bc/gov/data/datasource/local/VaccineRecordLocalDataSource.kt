package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.data.local.dao.VaccineRecordDao
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class VaccineRecordLocalDataSource @Inject constructor(
    private val vaccineRecordDao: VaccineRecordDao
) {

    /**
     * Insert [vaccineRecord] in to database
     * @param vaccineRecord
     * @return vaccineId of inserted record or -1L in case of error.
     */
    suspend fun insert(vaccineRecord: VaccineRecordDto): Long {
        val vaccineRecordId = vaccineRecordDao.getVaccineRecordId(vaccineRecord.patientId) ?: -1L
        return if (vaccineRecordId != -1L) {
            vaccineRecordId
        } else {
            vaccineRecordDao.insert(vaccineRecord.toEntity())
        }
    }

    suspend fun insertAuthenticatedVaccineRecord(vaccineRecordDto: VaccineRecordDto): Long {
        return vaccineRecordDao.insert(vaccineRecordDto.toEntity())
    }

    suspend fun insertAllAuthenticatedVaccineDose(doses: List<VaccineDoseDto>): List<Long> {
        return vaccineRecordDao.insert(dose = doses.map { it.toEntity() })
    }

    /**
     * Insert [VaccineDoseEntity] in to database
     * @param vaccineDose
     * @return vaccineRecordId else return -1L
     */
    suspend fun insert(vaccineDose: VaccineDoseDto): Long {
        val vaccineDoseId = vaccineRecordDao.insert(vaccineDose.toEntity())
        if (vaccineDoseId == -1L) {
            return vaccineRecordDao.getVaccineDoseId(vaccineDose.vaccineRecordId) ?: -1L
        }
        return vaccineDoseId
    }

    suspend fun insert(doses: List<VaccineDoseDto>): List<Long> {
        return vaccineRecordDao.insert(dose = doses.map { it.toEntity() })
    }

    suspend fun insertAllVaccineDoses(id: Long, doses: List<VaccineDoseDto>): List<Long> {
        val vaccineDoses = vaccineRecordDao.getVaccineDoses(id)
        if (vaccineDoses.isEmpty()) {
            return vaccineRecordDao.insert(dose = doses.map { it.toEntity() })
        }
        return vaccineDoses.map { id }
    }

    /**
     * Update [vaccineRecord] in to database
     * @param vaccineRecord
     * @return updated rowNumber, 0 if no row is updated.
     */
    suspend fun update(vaccineRecord: VaccineRecordDto): Int {
        return vaccineRecordDao.update(vaccineRecord.toEntity())
    }

    suspend fun delete(vaccineRecordId: Long): Int = vaccineRecordDao.delete(vaccineRecordId)

    /**
     * Delete [VaccineDoseEntity] from database
     * @param vaccineRecordId
     * @return number of row deleted else 0
     */
    suspend fun deleteVaccineDose(vaccineRecordId: Long): Int {
        return vaccineRecordDao.deleteVaccineDosesByRecordId(vaccineRecordId)
    }

    suspend fun getVaccineRecordId(patientId: Long): Long? =
        vaccineRecordDao.getVaccineRecordId(patientId)

    suspend fun deleteAuthenticatedVaccineRecords(patientId: Long): Int {
        return vaccineRecordDao.deleteAllAuthenticatedVaccineRecord(patientId)
    }
}
