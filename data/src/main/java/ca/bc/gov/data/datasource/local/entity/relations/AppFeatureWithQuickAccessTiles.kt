package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.settings.AppFeatureEntity
import ca.bc.gov.data.datasource.local.entity.settings.QuickAccessTileEntity

data class AppFeatureWithQuickAccessTiles(
    @Embedded
    val appFeature: AppFeatureEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "feature_id"
    )
    val quickAccessTiles: List<QuickAccessTileEntity> = emptyList()
)
