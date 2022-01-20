package ca.bc.gov.repository.analytics

import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.common.model.settings.AnalyticsFeature
import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import com.snowplowanalytics.snowplow.Snowplow
import com.snowplowanalytics.snowplow.event.SelfDescribing
import com.snowplowanalytics.snowplow.payload.SelfDescribingJson
import com.snowplowanalytics.snowplow.payload.TrackerPayload
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class AnalyticsRepository @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    companion object {
        private const val schema = "iglu:ca.bc.gov.gateway/action/jsonschema/1-0-0"
    }

    val analyticsFeature = preferenceStorage.analyticsFeature

    suspend fun toggleAnalyticsFeature(feature: AnalyticsFeature) =
        preferenceStorage.setAnalyticsFeature(feature)

    suspend fun track(action: AnalyticsAction, data: AnalyticsActionData) {
        val payload = TrackerPayload().add(action.value, data.value)
        val json = SelfDescribingJson(schema, payload)
        Snowplow.getDefaultTracker()?.track(SelfDescribing(json))
    }

    suspend fun track(action: AnalyticsAction, data: String) {
        val payload = TrackerPayload().add(action.value, data)
        val json = SelfDescribingJson(schema, payload)
        Snowplow.getDefaultTracker()?.track(SelfDescribing(json))
    }
}
