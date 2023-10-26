package ca.bc.gov.common.model.settings

import ca.bc.gov.common.model.AppFeatureName

data class AppFeatureDto(
    val id: Long = 0,
    val name: AppFeatureName,
    val hasManageableQuickAccessLinks: Boolean = false,
    val showAsQuickAccess: Boolean = false
)
