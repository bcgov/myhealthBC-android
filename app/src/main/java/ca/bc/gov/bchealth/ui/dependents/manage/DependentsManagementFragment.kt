package ca.bc.gov.bchealth.ui.dependents.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentsManagementBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.recycler.VerticalDragCallback
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.dependents.DependentDto
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections

@AndroidEntryPoint
class DependentsManagementFragment : BaseFragment(R.layout.fragment_dependents_management) {
    private val viewModel: DependentsManagementViewModel by viewModels()
    private val binding by viewBindings(FragmentDependentsManagementBinding::bind)
    private lateinit var adapter: DependentsManagementAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        launchOnStart {
            collectDependents()
        }
    }

    private fun setUpRecyclerView() {
        adapter = DependentsManagementAdapter(emptyList(), ::confirmDeletion)

        binding.rvDependents.adapter = adapter
        binding.rvDependents.layoutManager =
            LinearLayoutManager(requireContext())

        val helper = ItemTouchHelper(VerticalDragCallback(::onItemMoved))
        helper.attachToRecyclerView(binding.rvDependents)
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
                        .invokeOnCompletion {
                            findNavController().popBackStack()
                        }
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


    private fun onItemMoved(elementIndex: Int, targetIndex: Int) {
        Collections.swap(adapter.dependents, elementIndex, targetIndex)
    }

    private fun confirmDeletion(dependentDto: DependentDto, position: Int) {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.dependents_management_remove_title),
            msg = getString(R.string.dependents_management_remove_body, dependentDto.firstname),
            positiveBtnMsg = getString(R.string.yes),
            negativeBtnMsg = getString(R.string.no),
            positiveBtnCallback = {
                viewModel.deleteDependent(dependentDto, adapter.dependents)
            }
        )
    }
}
