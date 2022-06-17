package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.TermsOfServiceRemoteDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TermsOfServiceRepository @Inject constructor(
    private val termsOfServiceRemoteDataSource: TermsOfServiceRemoteDataSource
) {

    suspend fun getTermsOfService(): String? {
        val response = termsOfServiceRemoteDataSource.getTermsOfService()
        return response.resourcePayload.content
    }
}
