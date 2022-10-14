package ca.bc.gov.bchealth.ui.dependents

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentsFragment : BaseFragment(R.layout.fragment_dependents) {
    private val binding by viewBindings(FragmentDependentsBinding::bind)

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.appbar) {
            stateListAnimator = null
            elevation = 0f
        }
        with(binding.layoutToolbar.topAppBar) {
            inflateMenu(R.menu.settings_menu)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(R.id.profileFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }
}
