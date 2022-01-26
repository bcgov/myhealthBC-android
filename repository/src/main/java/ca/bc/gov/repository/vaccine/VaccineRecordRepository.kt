package ca.bc.gov.repository.vaccine

import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.data.datasource.VaccineRecordLocalDataSource
import ca.bc.gov.repository.model.mapper.toVaccineRecord
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class VaccineRecordRepository @Inject constructor(
    private val vaccineRecordLocalDataSource: VaccineRecordLocalDataSource
) {

    suspend fun insertVaccineRecord(vaccineRecordDto: VaccineRecordDto): Long =
        vaccineRecordLocalDataSource.insertVaccineRecord(vaccineRecordDto)

    suspend fun updateVaccineRecord(vaccineRecordDtoDto: VaccineRecordDto): Int =
        vaccineRecordLocalDataSource.updateVaccineRecord(vaccineRecordDtoDto)

    suspend fun getVaccineRecordId(patientId: Long): Long? =
        vaccineRecordLocalDataSource.getVaccineRecordId(patientId)

    suspend fun getVaccineRecords(patientId: Long) =
        vaccineRecordLocalDataSource.getVaccineRecords(patientId)
            .map { vaccineRecordEntity -> vaccineRecordEntity.toVaccineRecord() }

    suspend fun delete(vaccineRecordId: Long): Int =
        vaccineRecordLocalDataSource.delete(vaccineRecordId)
}
