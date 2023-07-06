package ca.bc.gov.common.model.settings

data class AppFeatureDto(
    val id: Long = 0,
    val featureNameId: Int,
    val featureIconId: Int,
    val destinationId: Int,
    val isEnabled: Boolean = false,
    val isQuickAccessEnabled: Boolean = false
)
