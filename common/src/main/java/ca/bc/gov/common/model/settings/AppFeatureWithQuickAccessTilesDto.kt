package ca.bc.gov.common.model.settings

import ca.bc.gov.common.model.quicklink.QuickLinkDto

data class AppFeatureWithQuickAccessTilesDto(
    val appFeatureDto: AppFeatureDto,
    val quickAccessTiles: List<QuickLinkDto> = emptyList()
)
