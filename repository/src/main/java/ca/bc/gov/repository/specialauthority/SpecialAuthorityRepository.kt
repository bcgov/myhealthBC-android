package ca.bc.gov.repository.specialauthority

import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.data.datasource.remote.SpecialAuthorityRemoteDataSource
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 24,June,2022
 */
class SpecialAuthorityRepository @Inject constructor(
    private val specialAuthorityRemoteDataSource: SpecialAuthorityRemoteDataSource
) {

    suspend fun getSpecialAuthority(token: String, hdid: String): List<SpecialAuthorityDto> {
        return specialAuthorityRemoteDataSource.getSpecialAuthority(token, hdid)
    }
}
