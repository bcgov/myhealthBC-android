package ca.bc.gov.bchealth.analytics

import com.snowplowanalytics.snowplow.event.SelfDescribing
import com.snowplowanalytics.snowplow.payload.SelfDescribingJson

/*
* Created by amit_metri on 25,October,2021
*/
object SelfDescribingEvent {

    private const val schema = "iglu:ca.bc.gov.gateway/action/jsonschema/1-0-0"

    fun get(action: String, text: String?): SelfDescribing {

        val properties: MutableMap<String, String?> = HashMap()
        properties["action"] = action
        properties["text"] = text

        val sdj = SelfDescribingJson(
            schema,
            properties
        )

        return SelfDescribing(sdj)
    }
}
