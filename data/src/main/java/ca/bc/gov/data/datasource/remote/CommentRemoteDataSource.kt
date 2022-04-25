package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.request.CommentRequest
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CommentRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    /**
     * @return list of comment
     */
    suspend fun fetchComments(
        parentEntryId: String?,
        hdid: String,
        accessToken: String
    ): List<CommentDto> {
        val response =
            safeCall { healthGatewayPrivateApi.getComments(hdid, accessToken, parentEntryId) }
                ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }
        return response.toDto()
    }

    suspend fun addComment(
        parentEntryId: String?,
        comment: String,
        entryTypeCode: String?,
        hdid: String,
        accessToken: String
    ): CommentDto {
        val commentRequest = CommentRequest(
            text = comment,
            parentEntryId = parentEntryId,
            userProfileId = hdid,
            entryTypeCode = entryTypeCode,
            // createdDateTime = Date().toString()
        )
        val response =
            safeCall {
                healthGatewayPrivateApi.addComment(
                    hdid,
                    accessToken,
                    commentRequest
                )
            }
                ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        return response.toDto()
    }
}
