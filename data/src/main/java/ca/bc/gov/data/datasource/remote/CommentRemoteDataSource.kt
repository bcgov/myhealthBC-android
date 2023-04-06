package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.request.CommentRequest
import ca.bc.gov.data.datasource.remote.model.request.CommentUpdateRequest
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
        hdid: String,
        accessToken: String
    ): List<CommentDto> {
        val response =
            safeCall { healthGatewayPrivateApi.getAllComments(hdid, accessToken) }
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
            entryTypeCode = entryTypeCode
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

    suspend fun updateComment(
        commentDto: CommentDto,
        hdid: String,
        accessToken: String
    ): CommentDto {
        val commentUpdateRequest = CommentUpdateRequest(
            id = commentDto.id,
            text = commentDto.text.orEmpty(),
            parentEntryId = commentDto.parentEntryId,
            userProfileId = hdid,
            entryTypeCode = commentDto.entryTypeCode,
            version = commentDto.version,
            createdDateTime = commentDto.createdDateTime.toString(),
            createdBy = commentDto.createdBy.orEmpty()
        )
        val response =
            safeCall {
                healthGatewayPrivateApi.updateComment(
                    hdid,
                    accessToken,
                    commentUpdateRequest
                )
            }
                ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        return response.toDto()
    }

    suspend fun deleteComment(
        commentDto: CommentDto,
        hdid: String,
        accessToken: String
    ) {
        val commentUpdateRequest = CommentUpdateRequest(
            id = commentDto.id,
            text = commentDto.text.orEmpty(),
            parentEntryId = commentDto.parentEntryId,
            userProfileId = hdid,
            entryTypeCode = commentDto.entryTypeCode,
            version = commentDto.version,
            createdDateTime = commentDto.createdDateTime.toString(),
            createdBy = commentDto.createdBy.orEmpty()
        )
        safeCall {
            healthGatewayPrivateApi.deleteComment(
                hdid,
                accessToken,
                commentUpdateRequest
            )
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
    }
}
