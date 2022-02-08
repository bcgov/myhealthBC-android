package ca.bc.gov.common.model

data class AuthenticatedCovidTestDto(
    val loaded: Boolean,
    val order: List<OrderDto>?,
    val retryInMilli: Long
)