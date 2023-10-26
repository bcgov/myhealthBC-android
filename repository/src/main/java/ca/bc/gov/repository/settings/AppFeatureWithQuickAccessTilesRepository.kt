package ca.bc.gov.repository.settings

import ca.bc.gov.preference.EncryptedPreferenceStorage
import javax.inject.Inject

class AppFeatureWithQuickAccessTilesRepository @Inject constructor(
    private val appFeatureRepository: AppFeatureRepository,
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    var isQuickAccessTileTutorialRequired: Boolean
        get() = preferenceStorage.isQuickAccessTileTutorialRequired
        set(value) {
            preferenceStorage.isQuickAccessTileTutorialRequired = value
        }

    suspend fun getAppFeaturesWithQuickAccessTiles() = appFeatureRepository.getAppFeaturesWithQuickAccessTiles()
}
