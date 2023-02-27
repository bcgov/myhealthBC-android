package ca.bc.gov.bchealth.ui.dependents

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentsBinding
import ca.bc.gov.bchealth.ui.dependents.records.filter.DependentFilterViewModel
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentsFragment : BaseDependentFragment(R.layout.fragment_dependents) {
    private val binding by viewBindings(FragmentDependentsBinding::bind)
    private val viewModel: DependentsViewModel by viewModels()
    private val dependentAdapter = DependentAdapter(::onClickDependent, ::confirmDeletion)
    private val filterSharedViewModel: DependentFilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnAddDependent.setOnClickListener { navigate(R.id.addDependentFragment) }
            btnLogIn.setOnClickListener { navigate(R.id.bcscAuthInfoFragment) }
            btnManageDependent.setOnClickListener { navigate(R.id.manageDependentFragment) }
            viewSessionExpired.btnLogin.setOnClickListener { navigate(R.id.bcscAuthInfoFragment) }
        }
        launchOnStart {
            observeUiState()
        }
        viewModel.loadAuthenticationState()

        observeHealthRecordsSyncCompletion()
        filterSharedViewModel.clearFilter()
    }

    private fun observeHealthRecordsSyncCompletion() {
        observeWork(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME) { state ->
            if (state == WorkInfo.State.RUNNING) {
                viewModel.displayLoadingState()
            } else {
                viewModel.hideLoadingState()
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->

            val isDataReadyToDisplay = uiState.isSessionActive == true && uiState.onLoading.not()
            binding.apply {
                progressBar.indicator.toggleVisibility(uiState.onLoading)
                groupLogIn.toggleVisibility(uiState.isBcscAuthenticated == false)
                viewSessionExpired.content.toggleVisibility(uiState.isSessionActive == false)
                tvBody.toggleVisibility(uiState.isSessionActive != false)
                btnAddDependent.toggleVisibility(isDataReadyToDisplay)
                containerImageEmpty.toggleVisibility(isDataReadyToDisplay)
                btnManageDependent.toggleVisibility(isDataReadyToDisplay)
                dividerList.toggleVisibility(isDataReadyToDisplay)
                listDependents.toggleVisibility(isDataReadyToDisplay)
            }

            if (uiState.isSessionActive == true) {
                launchOnStart { observeDependentList() }
            }

            uiState.error?.let { handleError(it) }
        }
    }

    private suspend fun observeDependentList() {
        viewModel.dependentsList.collect { list ->
            if (viewModel.uiState.value.onLoading.not()) {
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
    }

    private fun onClickDependent(dependent: DependentDetailItem) {
        navigate(
            R.id.dependentRecordsFragment,
            bundleOf(
                "patientId" to dependent.patientId,
                "hdid" to dependent.hdid,
                "fullName" to dependent.fullName
            )
        )
    }

    override fun deleteDependent(patientId: Long) {
        viewModel.removeDependent(patientId)
    }

    private fun handleError(e: Exception) {
        viewModel.resetErrorState()
        if (e is NetworkConnectionException) {
            context?.let {
                binding.root.showNoInternetConnectionMessage(it)
            }
        } else {
            showGenericError()
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
                        findNavController().navigate(R.id.settingsFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }
}
