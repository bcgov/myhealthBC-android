package ca.bc.gov.repository

import ca.bc.gov.common.model.banner.BannerDto
import ca.bc.gov.data.datasource.remote.BannerRemoteDataSource
import javax.inject.Inject

class BannerRepository @Inject constructor(
    private val remoteDataSource: BannerRemoteDataSource
) {

    suspend fun getBanner(): BannerDto? =
        remoteDataSource.fetchCommunicationBanner()
}
