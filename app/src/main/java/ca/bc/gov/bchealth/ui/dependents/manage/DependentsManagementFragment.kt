package ca.bc.gov.bchealth.ui.dependents.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentsManagementBinding
import ca.bc.gov.bchealth.ui.dependents.BaseDependentFragment
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentsManagementFragment : BaseDependentFragment(R.layout.fragment_dependents_management) {
    private val viewModel: DependentsManagementViewModel by viewModels()
    private val binding by viewBindings(FragmentDependentsManagementBinding::bind)
    private lateinit var adapter: DependentsManagementAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        launchOnStart { collectDependents() }
        launchOnStart { collectUiState() }
    }

    private fun setUpRecyclerView() {
        adapter = DependentsManagementAdapter(emptyList(), ::confirmDeletion)

        binding.rvDependents.adapter = adapter
        binding.rvDependents.layoutManager =
            LinearLayoutManager(requireContext())

        adapter.notifyItemRangeChanged(0, adapter.itemCount)
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.dependents_management_title)
            inflateMenu(R.menu.menu_done)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_done -> viewModel.updateDependentOrder(adapter.dependents)
                        .invokeOnCompletion { findNavController().popBackStack() }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private suspend fun collectDependents() {
        viewModel.dependents.collect { dependents ->
            if (::adapter.isInitialized) {
                adapter.dependents = dependents
                adapter.notifyDataSetChanged()
            }
        }
    }

    private suspend fun collectUiState() {
        viewModel.uiState.collect { uiState ->
            binding.viewLoading.root.toggleVisibility(uiState.isLoading)
            uiState.error?.let { showGenericError() }
        }
    }

    override fun deleteDependent(patientId: Long) {
        viewModel.deleteDependent(patientId, adapter.dependents)
    }
}
