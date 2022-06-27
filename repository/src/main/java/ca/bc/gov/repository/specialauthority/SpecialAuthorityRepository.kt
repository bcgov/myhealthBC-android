package ca.bc.gov.repository.specialauthority

import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.data.datasource.local.SpecialAuthorityLocalDataSource
import ca.bc.gov.data.datasource.remote.SpecialAuthorityRemoteDataSource
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 24,June,2022
 */
class SpecialAuthorityRepository @Inject constructor(
    private val specialAuthorityRemoteDataSource: SpecialAuthorityRemoteDataSource,
    private val specialAuthorityLocalDataSource: SpecialAuthorityLocalDataSource
) {

    suspend fun getSpecialAuthority(token: String, hdid: String): List<SpecialAuthorityDto> {
        return specialAuthorityRemoteDataSource.getSpecialAuthority(token, hdid)
    }

    suspend fun insert(specialAuthorities: List<SpecialAuthorityDto>): List<Long> {
        return specialAuthorityLocalDataSource.insert(specialAuthorities)
    }

    suspend fun deleteSpecialAuthorities(patientId: Long): Int =
        specialAuthorityLocalDataSource.deleteSpecialAuthorities(patientId)

    suspend fun getSpecialAuthorityDetails(id: Long): SpecialAuthorityDto? {
        return specialAuthorityLocalDataSource.getSpecialAuthorityDetails(id)
    }
}
