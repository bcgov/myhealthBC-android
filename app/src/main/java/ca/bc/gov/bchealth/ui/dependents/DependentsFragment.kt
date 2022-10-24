package ca.bc.gov.bchealth.ui.dependents

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DependentsFragment : BaseFragment(R.layout.fragment_dependents) {
    private val binding by viewBindings(FragmentDependentsBinding::bind)
    private val viewModel: DependentsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddDependent.setOnClickListener {
            findNavController().navigate(R.id.addDependentFragment)
        }
        binding.btnLogIn.setOnClickListener {
            findNavController().navigate(R.id.bcscAuthInfoFragment)
        }

        launchOnStart {
            launch { observeUiState() }
            launch { observeDependentList() }
        }
        viewModel.loadDependents()
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.progressBar.indicator.toggleVisibility(uiState.onLoading)

            binding.groupLogIn.toggleVisibility(uiState.isBcscAuthenticated == false)
            binding.containerImageEmpty.toggleVisibility(uiState.isBcscAuthenticated == true)
            binding.btnAddDependent.toggleVisibility(uiState.isBcscAuthenticated == true)
        }
    }

    private suspend fun observeDependentList() {
        viewModel.dependentsList.collect { list ->
        }
    }

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
