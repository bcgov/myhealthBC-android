package ca.bc.gov.bchealth.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.utils.HEALTH_GATEWAY_EMAIL_ADDRESS

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getAppBarConfiguration())
    }

    abstract fun setToolBar(appBarConfiguration: AppBarConfiguration)

    protected fun navigate(@IdRes screenId: Int, args: Bundle? = null) {
        findNavController().navigate(screenId, args)
    }

    fun composeEmail(address: String = HEALTH_GATEWAY_EMAIL_ADDRESS, subject: String = "") {
        (activity as? BaseActivity)?.composeEmail(address, subject)
    }

    private fun getAppBarConfiguration() = AppBarConfiguration(
        setOf(
            R.id.homeFragment,
            R.id.healthPassFragment,
            R.id.individualHealthRecordFragment,
            R.id.dependentsFragment
        ),
        null
    )
}
