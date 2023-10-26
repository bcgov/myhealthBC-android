package ca.bc.gov.common.model.settings

import ca.bc.gov.common.model.QuickAccessLinkName

data class QuickAccessTileDto(
    val id: Long = 0,
    val featureId: Long = 0,
    val tileName: QuickAccessLinkName,
    val tilePayload: String? = null,
    val showAsQuickAccess: Boolean = false
)
