package ca.bc.gov.bchealth.ui.healthrecord.labtest

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentLabTestDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.healthrecord.medication.CommentsAdapter
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import ca.bc.gov.bchealth.widget.AddCommentCallback
import ca.bc.gov.common.BuildConfig.FLAG_COMMENTS
import ca.bc.gov.repository.SYNC_COMMENTS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class LabTestDetailFragment : BaseFragment(R.layout.fragment_lab_test_detail) {

    private val binding by viewBindings(FragmentLabTestDetailBinding::bind)
    private val viewModel: LabTestDetailViewModel by viewModels()
    private val commentsViewModel: CommentsViewModel by viewModels()
    private lateinit var labTestDetailAdapter: LabTestDetailAdapter
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val args: LabTestDetailFragmentArgs by navArgs()
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        fileInMemory?.delete()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        viewModel.getLabTestDetails(args.labOrderId)
        observeUiState()
        observePdfData()
        if (FLAG_COMMENTS) {
            observeCommentsSyncCompletion()
        }
    }

    private fun initUI() {
        setUpRecyclerView()
        binding.comment.toggleVisibility(FLAG_COMMENTS)
        if (FLAG_COMMENTS) {
            addCommentListener()
        }
    }

    private lateinit var menuInflated: Menu
    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.filter)
            inflateMenu(R.menu.menu_lab_test_details)
            menuInflated = menu
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_download -> viewModel.getLabTestPdf()
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun setUpRecyclerView() {
        labTestDetailAdapter = LabTestDetailAdapter()

        concatAdapter = if (FLAG_COMMENTS) {
            initCommentsAdapter()
            ConcatAdapter(labTestDetailAdapter, commentsAdapter)
        } else {
            ConcatAdapter(labTestDetailAdapter)
        }

        binding.rvLabTestDetailList.apply {
            adapter = concatAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun initCommentsAdapter() {
        commentsAdapter = CommentsAdapter { parentEntryId ->
            val action = LabTestDetailFragmentDirections
                .actionLabTestDetailFragmentToCommentsFragment(
                    parentEntryId
                )
            findNavController().navigate(action)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    handledServiceDown(state)

                    if (state.labTestDetails?.isNotEmpty() == true) {
                        labTestDetailAdapter.submitList(state.labTestDetails)
                        binding.layoutToolbar.topAppBar.title = state.toolbarTitle
                    }

                    if (state.onError) {
                        showError()
                        viewModel.resetUiState()
                    }

                    handlePdfDownload(state)

                    handleNoInternetConnection(state)

                    if (FLAG_COMMENTS) {
                        getComments()
                    }
                }
            }
        }
        if (FLAG_COMMENTS) {
            observeComments()
        }
    }

    private fun getComments() {
        viewModel.uiState.value.parentEntryId?.let {
            commentsViewModel.getComments(it)
        }
    }

    private fun observeComments() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentsViewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.onLoading

                    if (state.latestComment.isNullOrEmpty().not()) {
                        commentsAdapter.submitList(state.latestComment)
                        // clear comment
                        binding.comment.clearComment()
                    }

                    if (state.onError) {
                        showError()
                    }
                }
            }
        }
    }

    private fun handledServiceDown(state: LabTestDetailUiState) {
        if (!state.isHgServicesUp) {
            binding.root.showServiceDownMessage(requireContext())
            viewModel.resetUiState()
        }
    }

    private fun handleNoInternetConnection(uiState: LabTestDetailUiState) {
        if (!uiState.isConnected) {
            binding.root.showNoInternetConnectionMessage(requireContext())
            viewModel.resetUiState()
        }
    }

    private fun handlePdfDownload(state: LabTestDetailUiState) {

        menuInflated.getItem(0).isVisible = state.showDownloadOption

        if (state.pdfData?.isNotEmpty() == true) {
            pdfDecoderViewModel.base64ToPDFFile(state.pdfData)
            viewModel.resetUiState()
        }
    }

    private fun showError() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error),
            msg = getString(R.string.error_message),
            positiveBtnMsg = getString(R.string.dialog_button_ok)
        )
    }

    private fun observePdfData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pdfDecoderViewModel.uiState.collect { uiState ->
                    uiState.pdf?.let {
                        val (base64Pdf, file) = it
                        if (file != null) {
                            try {
                                fileInMemory = file
                                PdfHelper().showPDF(file, requireActivity(), resultListener)
                            } catch (e: Exception) {
                                fallBackToPdfRenderer(base64Pdf)
                            }
                        } else {
                            fallBackToPdfRenderer(base64Pdf)
                        }
                        pdfDecoderViewModel.resetUiState()
                    }
                }
            }
        }
    }

    private fun fallBackToPdfRenderer(federalTravelPass: String) {
        findNavController().navigate(
            R.id.pdfRendererFragment,
            bundleOf(
                "base64pdf" to federalTravelPass,
                "title" to getString(R.string.lab_test)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fileInMemory != null) {
            fileInMemory?.delete()
            fileInMemory = null
        }
    }

    private fun addCommentListener() {
        binding.comment.addCommentListener(object : AddCommentCallback {
            override fun onSubmitComment(commentText: String) {
                viewModel.uiState.value.parentEntryId?.let {
                    commentsViewModel.addComment(
                        it,
                        commentText,
                        CommentEntryTypeCode.MEDICATION.value,
                    )
                }
            }
        })
    }

    private fun observeCommentsSyncCompletion() {
        val workRequest = WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(SYNC_COMMENTS)
        if (!workRequest.hasObservers()) {
            workRequest.observe(viewLifecycleOwner) {
                if (it.firstOrNull()?.state == WorkInfo.State.SUCCEEDED) {
                    viewModel.uiState.value.parentEntryId?.let { parentEntryId ->
                        commentsViewModel.getComments(
                            parentEntryId
                        )
                    }
                }
            }
        }
    }
}
