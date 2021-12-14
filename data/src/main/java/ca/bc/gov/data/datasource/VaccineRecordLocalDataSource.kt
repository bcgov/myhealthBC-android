package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.CreateVaccineRecordDto
import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.data.local.dao.VaccineRecordDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class VaccineRecordLocalDataSource @Inject constructor(
    private val vaccineRecordDao: VaccineRecordDao
) {

    /**
     * Insert [vaccineRecordDto] in to database
     * @param vaccineRecordDto
     * @return vaccineId of inserted record or -1L in case of error.
     */
    suspend fun insertVaccineRecord(vaccineRecordDto: CreateVaccineRecordDto): Long {
        var vaccineRecordId = vaccineRecordDao.insertVaccineRecord(vaccineRecordDto.toEntity())
        if (vaccineRecordId == -1L) {
            return vaccineRecordDao.getVaccineRecordId(vaccineRecordDto.patientId) ?: -1L
        }
        return vaccineRecordId
    }

    /**
     * Update [vaccineRecordDto] in to database
     * @param vaccineRecordDto
     * @return updated rowNumber, 0 if no row is updated.
     */
    suspend fun updateVaccineRecord(vaccineRecordDto: VaccineRecord): Int {
        return vaccineRecordDao.updateVaccineRecord(vaccineRecordDto.toEntity())
    }

    suspend fun getVaccineRecordId(patientId: Long): Long? =
        vaccineRecordDao.getVaccineRecordId(patientId)

    suspend fun getVaccineRecords(patientId: Long) = vaccineRecordDao.getVaccineRecords(patientId)

    suspend fun delete(vaccineRecordId: Long): Int = vaccineRecordDao.delete(vaccineRecordId)
}