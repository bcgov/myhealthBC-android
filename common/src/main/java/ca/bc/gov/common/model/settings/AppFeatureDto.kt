package ca.bc.gov.common.model.settings

data class AppFeatureDto(
    val id: Long = 0,
    val featureName: String? = null,
    val featureNameId: Int? = null,
    val categoryId: Int,
    val featureIconId: Int,
    val destinationId: Int,
    val isManagementEnabled: Boolean = false,
    val isQuickAccessEnabled: Boolean = false
)
