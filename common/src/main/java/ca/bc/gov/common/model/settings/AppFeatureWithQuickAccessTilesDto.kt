package ca.bc.gov.common.model.settings

data class AppFeatureWithQuickAccessTilesDto(
    val appFeatureDto: AppFeatureDto,
    val quickAccessTiles: List<QuickAccessTileDto> = emptyList()
)
