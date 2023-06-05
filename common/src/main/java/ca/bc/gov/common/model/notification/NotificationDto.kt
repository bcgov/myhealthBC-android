package ca.bc.gov.common.model.notification

data class NotificationDto(
    val id: String,
    var hdid: String = "",
    val category: String,
    val displayText: String,
    val actionUrl: String,
    val actionType: String,
    val date: String,
)
