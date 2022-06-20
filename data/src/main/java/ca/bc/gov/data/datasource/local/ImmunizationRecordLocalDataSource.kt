package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastAndPatientDto
import ca.bc.gov.data.datasource.local.dao.ImmunizationRecordDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin kansara
 */
class ImmunizationRecordLocalDataSource @Inject constructor(
    private val immunizationRecordDao: ImmunizationRecordDao
) {

    suspend fun insert(immunizationRecord: ImmunizationRecordDto): Long {
        return immunizationRecordDao.insert(immunizationRecord.toEntity())
    }

    suspend fun insert(immunizationRecords: List<ImmunizationRecordDto>): List<Long> {
        return immunizationRecordDao.insert(immunizationRecords.map { it.toEntity() })
    }

    suspend fun findByImmunizationRecordId(id: Long): ImmunizationRecordWithForecastAndPatientDto? =
        immunizationRecordDao.findByImmunizationId(id)?.toDto()

    suspend fun delete(patientId: Long): Int = immunizationRecordDao.delete(patientId)
}
