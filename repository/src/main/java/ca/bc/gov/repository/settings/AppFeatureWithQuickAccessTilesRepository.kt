package ca.bc.gov.repository.settings

import javax.inject.Inject

class AppFeatureWithQuickAccessTilesRepository @Inject constructor(
    private val appFeatureRepository: AppFeatureRepository
) {

    suspend fun getAppFeaturesWithQuickAccessTiles() = appFeatureRepository.getAppFeaturesWithQuickAccessTiles()
}
