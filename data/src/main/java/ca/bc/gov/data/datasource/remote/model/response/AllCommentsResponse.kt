package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.Status
import ca.bc.gov.data.datasource.remote.model.base.comment.CommentPayload
import com.google.gson.annotations.SerializedName

/**
 * @author: Created by Rashmi Bambhania on 29,April,2022
 */
data class AllCommentsResponse(
    @SerializedName("resourcePayload")
    val payload: HashMap<String, List<CommentPayload>> = hashMapOf(),
    val totalResultCount: Int,
    val pageIndex: Int,
    val pageSize: Int,
    @SerializedName("resultStatus")
    val status: Status,
    @SerializedName("resultError")
    val error: Error?
)
