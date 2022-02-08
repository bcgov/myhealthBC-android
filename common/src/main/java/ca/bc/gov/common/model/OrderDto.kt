package ca.bc.gov.common.model

data class OrderDto(
    val additionalData: String?,
    val id: String?,
    val labResult: List<LabResultDto>?,
    val location: String?,
    val messageDateTime: String?,
    val messageId: String?,
    val orderingProviderIds: String?,
    val orderingProviders: String?,
    val ormOrOru: String?,
    val phn: String?,
    val reportAvailable: Boolean?,
    val reportingLab: String?
)