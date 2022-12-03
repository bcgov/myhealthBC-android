package ca.bc.gov.bchealth.ui.dependents.profile

import android.os.Bundle
import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentProfileBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentProfileFragment : BaseFragment(R.layout.fragment_dependent_profile) {

    private val binding by viewBindings(FragmentDependentProfileBinding::bind)

    val list = listOf(
        R.string.dependents_profile_first to "John",
        R.string.dependents_profile_last to "Tester",
        R.string.dependents_profile_phn to "4444 555 999",
        R.string.dependents_profile_dob to "2010-Aug-20",
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeBody.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    DependentProfileUI(list)
                }
            }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setupWithNavController(findNavController(), appBarConfiguration)
            setNavigationIcon(R.drawable.ic_toolbar_back)
            title = getString(R.string.profile_settings)
        }
    }
}
