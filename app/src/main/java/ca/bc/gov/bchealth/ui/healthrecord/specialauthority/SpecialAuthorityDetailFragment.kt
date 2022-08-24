package ca.bc.gov.bchealth.ui.healthrecord.specialauthority

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSpecialAuthorityDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpecialAuthorityDetailFragment : BaseFragment(R.layout.fragment_special_authority_detail) {

    private val binding by viewBindings(FragmentSpecialAuthorityDetailBinding::bind)
    private val args: SpecialAuthorityDetailFragmentArgs by navArgs()
    private val viewModel: SpecialAuthorityDetailViewModel by viewModels()
    private lateinit var specialAuthorityAdapter: SpecialAuthorityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeUiState()
        viewModel.getSpecialAuthorityDetails(args.specialAuthorityId)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    binding.layoutToolbar.topAppBar.title = state.toolbarTitle

                    if (state.specialAuthorityDetailItems.isNotEmpty()) {
                        specialAuthorityAdapter.submitList(state.specialAuthorityDetailItems)
                    }
                }
            }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpRecyclerView() {
        specialAuthorityAdapter = SpecialAuthorityAdapter()
        val recyclerView = binding.rvSpecialAuthorityDetails
        recyclerView.adapter = specialAuthorityAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
