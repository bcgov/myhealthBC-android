package ca.bc.gov.repository.analytics

import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.common.model.settings.AnalyticsFeature
import ca.bc.gov.preference.EncryptedPreferenceStorage
import com.snowplowanalytics.snowplow.Snowplow
import com.snowplowanalytics.snowplow.event.SelfDescribing
import com.snowplowanalytics.snowplow.payload.SelfDescribingJson
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class AnalyticsRepository @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    companion object {
        private const val schema = "iglu:ca.bc.gov.gateway/action/jsonschema/2-0-0"
    }

    val analyticsFeature = preferenceStorage.analyticsFeature

    suspend fun toggleAnalyticsFeature(feature: AnalyticsFeature) =
        preferenceStorage.setAnalyticsFeature(feature)

    suspend fun track(action: AnalyticsAction, data: AnalyticsActionData) {
        Snowplow.getDefaultTracker()?.track(getEvent(action = action.value, text = data.value))
    }

    suspend fun track(action: AnalyticsAction, data: String) {
        Snowplow.getDefaultTracker()?.track(getEvent(action = action.value, text = data))
    }

    suspend fun track(action: AnalyticsAction, text: AnalyticsActionData, data: Map<String, String>) {
        Snowplow.getDefaultTracker()?.track(getEvent(action, text, data))
    }

    private fun getEvent(action: String, text: String?): SelfDescribing {

        val properties: MutableMap<String, String?> = HashMap()
        properties["action"] = action
        properties["text"] = text

        val sdj = SelfDescribingJson(
            schema,
            properties
        )

        return SelfDescribing(sdj)
    }

    private fun getEvent(action: AnalyticsAction, text: AnalyticsActionData, data: Map<String, String?>): SelfDescribing {
        val properties: MutableMap<String, String?> = HashMap()
        properties["action"] = action.value
        properties["text"] = text.value

        properties.putAll(data)

        val sdj = SelfDescribingJson(
            schema,
            properties
        )
        return SelfDescribing(sdj)
    }
}
