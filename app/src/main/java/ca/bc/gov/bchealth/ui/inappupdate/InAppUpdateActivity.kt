package ca.bc.gov.bchealth.ui.inappupdate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ActivityInAppUpdateBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateActivity : AppCompatActivity() {
    private val binding by viewBindings(ActivityInAppUpdateBinding::bind)

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateInfo: AppUpdateInfo

    private val installStatusListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_in_app_update)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStatusListener)
        binding.btnUpdate.setOnClickListener { checkForInAppUpdate() }
    }

    private fun checkForInAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            appUpdateInfo = it

            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startForInAppUpdate(appUpdateInfo)
            }

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    REQUEST_CODE_IMMEDIATE_UPDATE
                )
            }

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun startForInAppUpdate(updateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            updateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_IMMEDIATE_UPDATE
        )
    }

    override fun onDestroy() {
        appUpdateManager.unregisterListener(installStatusListener)
        super.onDestroy()
    }

    companion object {
        const val REQUEST_CODE_IMMEDIATE_UPDATE = 1001
    }
}
