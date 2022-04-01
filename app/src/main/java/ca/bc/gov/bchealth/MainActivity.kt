package ca.bc.gov.bchealth

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ca.bc.gov.bchealth.databinding.ActivityMainBinding
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.common.model.settings.AnalyticsFeature
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [MainActivity]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBindings(ActivityMainBinding::bind)

    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()

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
                R.id.healthRecordsFragment,
                R.id.settingFragment,
                R.id.profileFragment,
                R.id.resourcesFragment,
                R.id.individualHealthRecordFragment,
                R.id.vaccineRecordDetailFragment,
                R.id.testResultDetailFragment,
                R.id.addHealthRecordsFragment,
                R.id.homeFragment,
                R.id.newsfeedFragment -> {
                    showBottomNav()
                }
                else -> hideBottomNav()
            }
        }
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
}
