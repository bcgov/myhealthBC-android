package ca.bc.gov.bchealth.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import androidx.navigation.fragment.findNavController

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getAppBarConfiguration())
    }

    abstract fun setToolBar(appBarConfiguration: AppBarConfiguration)

    protected fun navigate(@IdRes screenId: Int) {
        findNavController().navigate(screenId)
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
