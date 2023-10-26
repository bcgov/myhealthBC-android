package ca.bc.gov.bchealth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import androidx.navigation.ui.setupWithNavController
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.databinding.ActivityMainBinding
import ca.bc.gov.bchealth.ui.inappupdate.InAppUpdateActivity
import ca.bc.gov.bchealth.utils.InAppUpdateHelper
import ca.bc.gov.bchealth.utils.showErrorSnackbar
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.bchealth.workers.FetchAuthenticatedHealthRecordsWorker
import ca.bc.gov.common.BuildConfig.FLAG_SERVICE_TAB
import ca.bc.gov.common.model.settings.AnalyticsFeature
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.AppUpdateType
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [MainActivity]
 *
 * @author Pinakin Kansara
 */
@OptIn(NavigationUiSaveStateControl::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBindings(ActivityMainBinding::bind)
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()

    // isWorkerStarted is required to avoid capturing "FAILED" state of worker at app launch
    private var isWorkerStarted: Boolean = false

    private lateinit var inAppUpdate: InAppUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        inAppUpdate = InAppUpdateHelper(this, lifecycle) {
            showUpdateDownloaded()
        }

        toggleAnalyticsFeature()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.inflateMenu(
            if (FLAG_SERVICE_TAB) {
                R.menu.bottom_nav_services_menu
            } else {
                R.menu.bottom_nav_menu
            }
        )
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController, false)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.healthPassFragment,
                R.id.healthPassesFragment,
                R.id.addCardOptionFragment,
                R.id.dependentsFragment,
                R.id.dependentRecordsFragment,
                R.id.resourcesFragment,
                R.id.healthRecordFragment,
                R.id.vaccineRecordDetailFragment,
                R.id.addHealthRecordsFragment,
                R.id.homeFragment,
                R.id.bannerDetailFragment,
                R.id.newsfeedFragment,
                R.id.servicesFragment,
                R.id.bcServiceCardSessionFragment,
                R.id.bcServicesCardLoginFragment -> {
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

                when (workInfo.state) {
                    WorkInfo.State.RUNNING -> {
                        isWorkerStarted = true
                        val started = workInfo.progress.getBoolean(
                            FetchAuthenticatedHealthRecordsWorker.RECORD_FETCH_STARTED,
                            false
                        )
                        if (started) {
                            binding.navHostFragment.showErrorSnackbar(getString(R.string.notification_title_while_fetching_data), binding.bottomNav)
                        }
                    }

                    WorkInfo.State.FAILED -> {
                        handleError(workInfo.outputData)
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        if (isWorkerStarted) {
                            inAppUpdate.checkForUpdate(AppUpdateType.FLEXIBLE)
                            binding.navHostFragment.showErrorSnackbar(getString(R.string.notification_title_on_success), binding.bottomNav)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun handleError(workData: Data) {
        if (isWorkerStarted) {
            val appUpdateRequired = workData.getBoolean(
                FetchAuthenticatedHealthRecordsWorker.FailureReason.APP_UPDATE_REQUIRED.value,
                false
            )
            if (appUpdateRequired) {
                openInAppUpdateActivity()
                return
            }

            val isHgServicesUp =
                workData.getBoolean(
                    FetchAuthenticatedHealthRecordsWorker.FailureReason.IS_HG_SERVICES_UP.value,
                    true
                )
            if (!isHgServicesUp) {
                binding.navHostFragment.showServiceDownMessage(this)
                return
            }
            val isRecordFetchFailed =
                workData.getBoolean(
                    FetchAuthenticatedHealthRecordsWorker.FailureReason.IS_RECORD_FETCH_FAILED.value,
                    false
                )
            if (isRecordFetchFailed) {
                binding.navHostFragment.showErrorSnackbar(getString(R.string.notification_title_on_failed), binding.bottomNav)
            }
        }
    }

    private fun openInAppUpdateActivity() {
        startActivity(Intent(this, InAppUpdateActivity::class.java))
        finish()
    }

    private fun showUpdateDownloaded() {
        Snackbar.make(
            findViewById(R.id.bottom_nav),
            R.string.update_downloaded,
            5000
        ).apply {
            setAction(R.string.restart) { inAppUpdate.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.status_green, theme))
            show()
        }
    }
}
