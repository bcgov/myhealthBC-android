package ca.bc.gov.repository.services

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.services.BcCancerScreeningDataDto
import ca.bc.gov.data.datasource.local.BcCancerScreeningLocalDataSource
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2024-01-18 at 10:59 a.m.
 */
class BcCancerScreeningRepository @Inject constructor(
    private val localDataSource: BcCancerScreeningLocalDataSource
) {

    suspend fun insert(bcCancerScreeningDataDtoList: List<BcCancerScreeningDataDto>) =
        localDataSource.insert(bcCancerScreeningDataDtoList)

    suspend fun getBcCancerScreeningDataDetails(id: Long): BcCancerScreeningDataDto =
        localDataSource.getBcCancerScreeningDataDetails(id) ?: throw MyHealthException(
            DATABASE_ERROR, "No record found for diagnostic imaging id=  $id"
        )
}
