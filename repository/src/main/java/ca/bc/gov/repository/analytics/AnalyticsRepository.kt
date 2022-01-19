package ca.bc.gov.repository.analytics

import ca.bc.gov.common.model.settings.AnalyticsFeature
import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class AnalyticsRepository @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    val analyticsFeature = preferenceStorage.analyticsFeature

    suspend fun toggleAnalyticsFeature(feature: AnalyticsFeature) =
        preferenceStorage.setAnalyticsFeature(feature)
}
