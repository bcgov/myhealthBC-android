package ca.bc.gov.repository.immunization

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastAndPatientDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto
import ca.bc.gov.data.datasource.local.ImmunizationRecordLocalDataSource
import ca.bc.gov.data.datasource.remote.ImmunizationRemoteDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ImmunizationRecordRepository @Inject constructor(
    private val immunizationRemoteDataSource: ImmunizationRemoteDataSource,
    private val immunizationRecordLocalDataSource: ImmunizationRecordLocalDataSource
) {

    suspend fun insert(immunizationRecord: ImmunizationRecordDto): Long =
        immunizationRecordLocalDataSource.insert(immunizationRecord)

    suspend fun insert(immunizationRecords: List<ImmunizationRecordDto>): List<Long> =
        immunizationRecordLocalDataSource.insert(immunizationRecords)

    suspend fun findByImmunizationRecordId(id: Long): ImmunizationRecordWithForecastAndPatientDto =
        immunizationRecordLocalDataSource.findByImmunizationRecordId(id)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for  Immunization id=  $id"
            )

    suspend fun delete(patientId: Long): Int = immunizationRecordLocalDataSource.delete(patientId)

    suspend fun fetchImmunization(token: String, hdid: String): List<ImmunizationRecordWithForecastDto> {
        return immunizationRemoteDataSource.getImmunization(token, hdid)
    }
}
