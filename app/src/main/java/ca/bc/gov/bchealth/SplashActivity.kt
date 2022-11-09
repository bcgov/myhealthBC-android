package ca.bc.gov.bchealth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * [SplashActivity]
 *
 * @author amit metri
 */

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateInfo: AppUpdateInfo

    private val installStatusListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStatusListener)
    }

    override fun onResume() {
        super.onResume()
        checkForUpdate()
    }

    private fun checkForUpdate() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMMEDIATE_UPDATE) {
            if (resultCode != RESULT_OK) {
                if (::appUpdateInfo.isInitialized) {
                    startForInAppUpdate(appUpdateInfo)
                }
            }
        }
    }

    override fun onDestroy() {
        appUpdateManager.unregisterListener(installStatusListener)
        super.onDestroy()
    }

    companion object {
        const val REQUEST_CODE_IMMEDIATE_UPDATE = 1001
    }
}
