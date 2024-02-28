package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.services.BcCancerScreeningDataDto
import ca.bc.gov.data.datasource.local.dao.BcCancerScreeningDataDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2024-01-18 at 12:40 p.m.
 */
class BcCancerScreeningLocalDataSource @Inject constructor(
    private val bcCancerScreeningDataDao: BcCancerScreeningDataDao
) {

    suspend fun insert(bcCancerScreeningDataDtoList: List<BcCancerScreeningDataDto>) =
        bcCancerScreeningDataDao.insert(bcCancerScreeningDataDtoList.map { it.toEntity() })

    suspend fun getBcCancerScreeningDataDetails(id: Long) = bcCancerScreeningDataDao.getBcCancerScreeningDataDetails(id)?.toDto()
}
