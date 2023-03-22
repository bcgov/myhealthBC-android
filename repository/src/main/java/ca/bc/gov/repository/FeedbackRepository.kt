package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.FeedbackRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

class FeedbackRepository @Inject constructor(
    private val feedbackRemoteDataSource: FeedbackRemoteDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
) {
    suspend fun addFeedback(message: String) {
        val authParameters = bcscAuthRepo.getAuthParametersDto()
        feedbackRemoteDataSource.addFeedback(
            message = message,
            hdid = authParameters.hdid,
            accessToken = authParameters.token
        )
    }
}
