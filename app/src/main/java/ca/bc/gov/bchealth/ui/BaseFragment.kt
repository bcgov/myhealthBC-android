package ca.bc.gov.bchealth.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getAppBarConfiguration())
    }


    abstract fun setToolBar(appBarConfiguration: AppBarConfiguration)

    protected fun getAppBarConfiguration() = AppBarConfiguration(
        setOf(
            R.id.homeFragment,
            R.id.healthRecordsFragment,
            R.id.healthPassFragment,
            R.id.resourcesFragment
        )
    )
}