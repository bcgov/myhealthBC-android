package ca.bc.gov.repository.worker

import ca.bc.gov.data.datasource.remote.MobileConfigRemoteDataSource
import ca.bc.gov.data.datasource.remote.model.response.MobileConfigurationResponse
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 16,May,2022
 */
class MobileConfigRepository @Inject constructor(private val mobileConfigRemoteDataSource: MobileConfigRemoteDataSource) {

    suspend fun getBaseUrl(): MobileConfigurationResponse {
        return mobileConfigRemoteDataSource.getBaseUrl()
    }
}
