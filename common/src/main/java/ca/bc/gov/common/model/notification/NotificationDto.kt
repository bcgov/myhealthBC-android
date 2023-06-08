package ca.bc.gov.common.model.notification

import java.time.Instant

data class NotificationDto(
    val id: String,
    var hdid: String = "",
    val category: String,
    val displayText: String,
    val actionUrl: String,
    val actionType: NotificationActionTypeDto,
    val date: Instant,
)

enum class NotificationActionTypeDto(val value: String) {
    EXTERNAL("ExternalLink"),
    INTERNAL("InternalLink"),
    NONE("None");

    companion object {
        fun getByValue(value: String) =
            values().firstOrNull {
                it.value == value
            } ?: NONE
    }
}
