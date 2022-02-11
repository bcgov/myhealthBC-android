package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.VaccineRecordDto
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
    suspend fun insertVaccineRecord(vaccineRecordDto: VaccineRecordDto): Long {
        val vaccineRecordId = vaccineRecordDao.getVaccineRecordId(vaccineRecordDto.patientId) ?: -1L
        return if (vaccineRecordId != -1L) {
            vaccineRecordId
        } else {
            vaccineRecordDao.insertVaccineRecord(vaccineRecordDto.toEntity())
        }
    }

    suspend fun insertAuthenticatedVaccineRecord(vaccineRecordDto: VaccineRecordDto): Long {
        val vaccineRecordId = vaccineRecordDao.getVaccineRecordId(vaccineRecordDto.patientId) ?: -1L
        if (vaccineRecordId != -1L) {
            vaccineRecordDao.delete(vaccineRecordId)
        }
        return vaccineRecordDao.insertVaccineRecord(vaccineRecordDto.toEntity())
    }

    /**
     * Update [vaccineRecordDtoDto] in to database
     * @param vaccineRecordDtoDto
     * @return updated rowNumber, 0 if no row is updated.
     */
    suspend fun updateVaccineRecord(vaccineRecordDtoDto: VaccineRecordDto): Int {
        return vaccineRecordDao.updateVaccineRecord(vaccineRecordDtoDto.toEntity())
    }

    suspend fun getVaccineRecordId(patientId: Long): Long? =
        vaccineRecordDao.getVaccineRecordId(patientId)

    suspend fun getVaccineRecords(patientId: Long) = vaccineRecordDao.getVaccineRecords(patientId)

    suspend fun delete(vaccineRecordId: Long): Int = vaccineRecordDao.delete(vaccineRecordId)
}
