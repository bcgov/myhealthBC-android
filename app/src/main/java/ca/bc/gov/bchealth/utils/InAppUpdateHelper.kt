package ca.bc.gov.bchealth.utils

import android.app.Activity
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * [InAppUpdateHelper] lifecycle aware component
 * @author Pinakin Kansara
 */
internal class InAppUpdateHelper(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val onFlexUpdateDownloaded: () -> Unit
) : DefaultLifecycleObserver {

    private lateinit var appUpdateManager: AppUpdateManager
    private var updateType = AppUpdateType.FLEXIBLE

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            onFlexUpdateDownloaded()
        }
    }

    companion object {
        const val REQUEST_CODE_APP_UPDATE = 1001
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        appUpdateManager = AppUpdateManagerFactory.create(context)
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when (updateType) {
                AppUpdateType.IMMEDIATE -> {
                    if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        startUpdate(info, updateType)
                    }
                }
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    fun checkForUpdate(@AppUpdateType updateType: Int) {
        this.updateType = updateType
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    it.isUpdateTypeAllowed(updateType)
                ) {
                    startUpdate(it, updateType)
                }
            }
        }
    }

    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo, @AppUpdateType updateType: Int) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo, updateType, context as Activity,
            REQUEST_CODE_APP_UPDATE
        )
    }
}
