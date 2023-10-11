package ca.bc.gov.bchealth.ui.healthrecord.specialauthority

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSpecialAuthorityDetailBinding
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.healthrecord.BaseRecordDetailFragment
import ca.bc.gov.bchealth.utils.launchAndRepeatWithLifecycle
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpecialAuthorityDetailFragment :
    BaseRecordDetailFragment(R.layout.fragment_special_authority_detail) {

    private val binding by viewBindings(FragmentSpecialAuthorityDetailBinding::bind)
    private val args: SpecialAuthorityDetailFragmentArgs by navArgs()
    private val viewModel: SpecialAuthorityDetailViewModel by viewModels()
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var specialAuthorityAdapter: SpecialAuthorityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupComposeToolbar(binding.composeToolbar.root)
        setUpRecyclerView()
        observeUiState()
        viewModel.getSpecialAuthorityDetails(args.specialAuthorityId)
        initComments()
    }

    override fun getCommentEntryTypeCode() = CommentEntryTypeCode.SPECIAL_AUTHORITY

    override fun getParentEntryId() = viewModel.uiState.value.parentEntryId

    override fun getCommentView() = binding.viewComment

    private fun observeUiState() {
        launchAndRepeatWithLifecycle {
            viewModel.uiState.collect { state ->

                binding.progressBar.isVisible = state.onLoading
                setupComposeToolbar(binding.composeToolbar.root, state.toolbarTitle)

                if (state.specialAuthorityDetailItems.isNotEmpty()) {
                    specialAuthorityAdapter.submitList(state.specialAuthorityDetailItems)
                }

                getComments(state.parentEntryId)
            }
        }
    }

    override fun getProgressBar() = binding.progressBar

    override fun getScrollableView() = binding.rvSpecialAuthorityDetails

    private fun setUpRecyclerView() {
        specialAuthorityAdapter = SpecialAuthorityAdapter()
        concatAdapter = ConcatAdapter(specialAuthorityAdapter, getRecordCommentsAdapter())

        val recyclerView = binding.rvSpecialAuthorityDetails
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
