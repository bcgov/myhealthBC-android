package ca.bc.gov.bchealth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ca.bc.gov.bchealth.SplashViewModel.UpdateType.CHECK_SOFT_UPDATE
import ca.bc.gov.bchealth.SplashViewModel.UpdateType.FORCE_UPDATE
import ca.bc.gov.bchealth.ui.inappupdate.InAppUpdateActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * [SplashActivity]
 *
 * @author amit metri
 */
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        observeUpdateStatus()
        viewModel.checkAppVersion()
    }

    private fun observeUpdateStatus() {
        viewModel.updateType.observe(this@SplashActivity) {
            when (it) {
                FORCE_UPDATE -> openInAppUpdateActivity()
                // Soft Update will be handled on HAPP-1167
                CHECK_SOFT_UPDATE -> openMainActivity()
                else -> openMainActivity()
            }
        }
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openInAppUpdateActivity() {
        startActivity(Intent(this, InAppUpdateActivity::class.java))
        finish()
    }
}
