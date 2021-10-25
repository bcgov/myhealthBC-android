package ca.bc.gov.bchealth

import android.app.Application
import android.util.Log
import com.snowplowanalytics.snowplow.Snowplow
import com.snowplowanalytics.snowplow.configuration.NetworkConfiguration
import com.snowplowanalytics.snowplow.configuration.SessionConfiguration
import com.snowplowanalytics.snowplow.configuration.TrackerConfiguration
import com.snowplowanalytics.snowplow.network.HttpMethod
import com.snowplowanalytics.snowplow.tracker.LogLevel
import com.snowplowanalytics.snowplow.tracker.LoggerDelegate
import com.snowplowanalytics.snowplow.util.TimeMeasure
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit


/**
 * [BcVaccineCardApp].
 *
 * @author Pinakin Kansara
 */
@HiltAndroidApp
class BcVaccineCardApp : Application() {

    companion object {
        const val COLLECTOR_URL_NON_PROD = "spm.apps.gov.bc.ca"
        const val COLLECTOR_URL_PROD = "spt.apps.gov.bc.ca"
        const val namespace = "android"
    }

    private lateinit var appId: String
    private lateinit var networkConfig: NetworkConfiguration
    private lateinit var trackerConfig: TrackerConfiguration

    override fun onCreate() {
        super.onCreate()

        initSnowplow()

    }

    private fun initSnowplow() {

        if (BuildConfig.DEBUG) {

            appId = "Snowplow_standalone_HApp_dev"

            networkConfig = NetworkConfiguration(COLLECTOR_URL_NON_PROD, HttpMethod.POST)

            trackerConfig = TrackerConfiguration(appId)
                .logLevel(LogLevel.VERBOSE)
                .loggerDelegate(object : LoggerDelegate {
                    override fun error(tag: String?, msg: String?) {
                        Log.e(tag, msg.toString())
                    }

                    override fun debug(tag: String?, msg: String?) {
                        Log.d(tag, msg.toString())
                    }

                    override fun verbose(tag: String?, msg: String?) {
                        Log.d(tag, msg.toString())
                    }
                })
                .base64encoding(true)
                .sessionContext(true)
                .platformContext(true)
                .lifecycleAutotracking(true)
                .screenViewAutotracking(true)
                .screenContext(true)
                .applicationContext(true)
                .exceptionAutotracking(true)
                .installAutotracking(true)
        } else {

            appId = "Snowplow_standalone_HApp" // TODO: 22/10/21 TBD for release version of the app

            networkConfig = NetworkConfiguration(COLLECTOR_URL_PROD, HttpMethod.POST)

            trackerConfig = TrackerConfiguration(appId)
                .base64encoding(true)
                .sessionContext(true)
                .platformContext(true)
                .lifecycleAutotracking(true)
                .screenViewAutotracking(true)
                .screenContext(true)
                .applicationContext(true)
                .exceptionAutotracking(true)
                .installAutotracking(true)
        }

        val sessionConfig = SessionConfiguration(
            TimeMeasure(30, TimeUnit.SECONDS),
            TimeMeasure(30, TimeUnit.SECONDS)
        )

        Snowplow.createTracker(
            applicationContext,
            namespace,
            networkConfig,
            trackerConfig,
            sessionConfig
        )
    }

}
