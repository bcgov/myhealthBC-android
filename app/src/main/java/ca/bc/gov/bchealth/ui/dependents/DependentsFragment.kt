package ca.bc.gov.bchealth.ui.dependents

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentsFragment : BaseFragment(R.layout.fragment_dependents) {
    private val binding by viewBindings(FragmentDependentsBinding::bind)
    private val viewModel: DependentsViewModel by viewModels()
    private val dependentAdapter = DependentAdapter(::onClickDependent)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddDependent.setOnClickListener {
            navigate(R.id.addDependentFragment)
        }
        binding.btnLogIn.setOnClickListener {
            navigate(R.id.bcscAuthInfoFragment)
        }

        binding.viewSessionExpired.btnLogin.setOnClickListener {
            navigate(R.id.bcscAuthInfoFragment)
        }

        launchOnStart {
            observeUiState()
        }
        viewModel.loadAuthenticationState()
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.apply {
                progressBar.indicator.toggleVisibility(uiState.onLoading)
                groupLogIn.toggleVisibility(uiState.isBcscAuthenticated == false)
                viewSessionExpired.content.toggleVisibility(uiState.isSessionActive == false)
                tvBody.toggleVisibility(uiState.isSessionActive != false)
                btnAddDependent.toggleVisibility(uiState.isSessionActive == true)
                containerImageEmpty.toggleVisibility(uiState.isSessionActive == true)
                btnManageDependent.toggleVisibility(uiState.isSessionActive == true)
                dividerList.toggleVisibility(uiState.isSessionActive == true)
                listDependents.toggleVisibility(uiState.isSessionActive == true)
            }

            if (uiState.isSessionActive == true) {
                launchOnStart {
                    observeDependentList()
                }
            }
        }
    }

    private suspend fun observeDependentList() {
        viewModel.dependentsList.collect { list ->
            binding.apply {
                containerImageEmpty.toggleVisibility(list.isEmpty())
                btnManageDependent.toggleVisibility(list.isNotEmpty())
                dividerList.toggleVisibility(list.isNotEmpty())
                listDependents.toggleVisibility(list.isNotEmpty())
                listDependents.adapter = dependentAdapter
                dependentAdapter.submitList(list.toMutableList())
            }
        }
    }

    private fun onClickDependent(dependent: DependentDetailItem) {
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
