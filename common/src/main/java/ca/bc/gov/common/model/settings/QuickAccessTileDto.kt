package ca.bc.gov.common.model.settings

data class QuickAccessTileDto(
    val id: Long = 0,
    val featureId: Long = 0,
    val titleNameId: Int,
    val titleIconId: Int,
    val isEnabled: Boolean = false
)
