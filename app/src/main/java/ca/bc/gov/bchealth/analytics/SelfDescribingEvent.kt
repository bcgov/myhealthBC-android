package ca.bc.gov.bchealth.analytics

import com.snowplowanalytics.snowplow.event.SelfDescribing
import com.snowplowanalytics.snowplow.payload.SelfDescribingJson

/*
* Created by amit_metri on 25,October,2021
*/
object SelfDescribingEvent {

    const val schema  = "iglu:ca.bc.gov.gateway/action/jsonschema/1-0-0"

    fun get(key: AnalyticsAction, value: AnalyticsText): SelfDescribing {

        val properties: MutableMap<String, String> = HashMap()
        properties[key.toString()] = value.toString()

        val sdj = SelfDescribingJson(
            schema,
            properties
        )

        return SelfDescribing(sdj)
    }

    fun get(key: AnalyticsAction, value: String): SelfDescribing {
        val properties: MutableMap<String, String> = HashMap()
        properties[key.toString()] = value

        val sdj = SelfDescribingJson(
            schema,
            properties
        )

        return SelfDescribing(sdj)
    }
}