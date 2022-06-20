package ca.bc.gov.repository

import ca.bc.gov.common.model.TermsOfServiceDto
import ca.bc.gov.data.datasource.remote.TermsOfServiceRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TermsOfServiceRepository @Inject constructor(
    private val termsOfServiceRemoteDataSource: TermsOfServiceRemoteDataSource
) {

    suspend fun getTermsOfService(): TermsOfServiceDto {
        val response = termsOfServiceRemoteDataSource.getTermsOfService()
        return response.resourcePayload.toDto()
    }
}
