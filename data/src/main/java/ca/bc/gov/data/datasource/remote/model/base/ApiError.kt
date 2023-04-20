package ca.bc.gov.data.datasource.remote.model.base

/**
 * @author Pinakin Kansara
 * [ApiError] is used to handle v2 api error response.
 * @see [Extensions] is app module for the error handling part.
 */
data class ApiError(
    val type: String,
    val title: String,
    val status: Int,
    val traceId: String
)
