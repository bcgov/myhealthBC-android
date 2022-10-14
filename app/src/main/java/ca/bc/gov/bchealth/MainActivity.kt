package ca.bc.gov.bchealth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import ca.bc.gov.bchealth.databinding.ActivityMainBinding
import ca.bc.gov.bchealth.ui.inappupdate.InAppUpdateActivity
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.common.model.settings.AnalyticsFeature
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import ca.bc.gov.repository.worker.FetchAuthenticatedHealthRecordsWorker.Companion.APP_UPDATE_REQUIRED
import ca.bc.gov.repository.worker.FetchAuthenticatedHealthRecordsWorker.Companion.IS_HG_SERVICES_UP
import ca.bc.gov.repository.worker.FetchAuthenticatedHealthRecordsWorker.Companion.QUEUE_IT_URL
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * [MainActivity]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBindings(ActivityMainBinding::bind)
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()
    private val viewModel: MainViewModel by viewModels()

    // isWorkerStarted is required to avoid capturing "FAILED" state of worker at app launch
    private var isWorkerStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        toggleAnalyticsFeature()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.healthPassFragment,
                R.id.healthPassesFragment,
                R.id.addCardOptionFragment,
                R.id.healthRecordPlaceholderFragment,
                R.id.dependentsFragment,
                R.id.resourcesFragment,
                R.id.individualHealthRecordFragment,
                R.id.vaccineRecordDetailFragment,
                R.id.testResultDetailFragment,
                R.id.addHealthRecordsFragment,
                R.id.homeFragment,
                R.id.bannerDetailFragment,
                R.id.newsfeedFragment -> {
                    showBottomNav()
                }
                else -> hideBottomNav()
            }
        }

        observeExceptionFromWorker()
    }

    private fun toggleAnalyticsFeature() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                analyticsFeatureViewModel.analyticsFeature.collect { analyticsFeature ->
                    if (analyticsFeature == AnalyticsFeature.ENABLED) {
                        Snowplow.getDefaultTracker()?.resume()
                    } else if (analyticsFeature == AnalyticsFeature.DISABLED) {
                        Snowplow.getDefaultTracker()?.pause()
                    }
                }
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
    }

    private fun observeExceptionFromWorker() {
        val workRequest = WorkManager.getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        if (!workRequest.hasObservers()) {
            workRequest.observe(
                this
            ) {
                val workInfo = it.firstOrNull() ?: return@observe

                if (workInfo.state.name == "RUNNING") {
                    isWorkerStarted = true
                }

                if (workInfo.state.name == "FAILED") {
                    val workData = workInfo.outputData

                    if (isWorkerStarted) {
                        val appUpdateRequired = workData.getBoolean(APP_UPDATE_REQUIRED, false)
                        if (appUpdateRequired) {
                            openInAppUpdateActivity()
                            return@observe
                        }

                        val isHgServicesUp = workData.getBoolean(IS_HG_SERVICES_UP, true)
                        if (!isHgServicesUp) {
                            binding.navHostFragment.showServiceDownMessage(this)
                            return@observe
                        }
                    }
                    val queueItUrl = workData.getString(QUEUE_IT_URL)
                    if (queueItUrl?.isNotBlank() == true) {
                        queUser(queueItUrl.toString())
                    }
                }
            }
        }
    }

    private fun queUser(value: String) {
        try {
            val uri = Uri.parse(URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
            val customerId = uri.getQueryParameter("c")
            val waitingRoomId = uri.getQueryParameter("e")
            QueueService.IsTest = false
            val queueITEngine =
                QueueITEngine(this, customerId, waitingRoomId, "", "", queueListener)
            queueITEngine.run(this)
        } catch (e: Exception) {
            Log.i(this::class.java.name, "Exception in queUser: ${e.message}")
        }
    }

    private fun openInAppUpdateActivity() {
        startActivity(Intent(this, InAppUpdateActivity::class.java))
        finish()
    }

    private val queueListener = object : QueueListener() {
        override fun onQueuePassed(queuePassedInfo: QueuePassedInfo?) {
            Log.d("TAG", "setQueItToken: token = $queuePassedInfo?.queueItToken")
            viewModel.setQueItToken(queuePassedInfo?.queueItToken)
        }

        override fun onQueueDisabled() {
            // Do nothing
        }

        override fun onQueueViewWillOpen() {
            // Do nothing
        }

        override fun onError(error: Error?, errorMessage: String?) {
            // Do nothing
        }

        override fun onQueueItUnavailable() {
            // Do nothing
        }
    }
}
