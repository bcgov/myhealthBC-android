package ca.bc.gov.bchealth.ui.healthrecord.labtest

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.comment.CommentsUiState
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_PDF
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_ORDER
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_TEST
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_TEST_BANNER
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.viewmodel.PdfDecoderUiState
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS

@Composable
fun LabTestScreen(
    onPdfStateChanged: (PdfDecoderUiState) -> Unit,
    onClickFaq: () -> Unit,
    onPopNavigation: () -> Unit,
    showServiceDownMessage: () -> Unit,
    showNoInternetConnectionMessage: () -> Unit,
    navigateToComments: (CommentsSummary) -> Unit,
    hdid: String?,
    labOrderId: Long,
    viewModel: LabTestDetailViewModel,
    commentsViewModel: CommentsViewModel,
    pdfDecoderViewModel: PdfDecoderViewModel,
) {
    val uiState = viewModel.uiState
        .collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value

    var commentState: CommentsUiState? = null

    if (BuildConfig.FLAG_ADD_COMMENTS) {
        uiState.parentEntryId?.let { commentsViewModel.getComments(it) }
        commentState = commentsViewModel.uiState
            .collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value
    }

    ObserveCommentsWorker(viewModel, commentsViewModel)

    val pdfUiState = pdfDecoderViewModel.uiState
        .collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value

    LaunchedEffect(Unit) {
        viewModel.getLabTestDetails(labOrderId)
    }

    MyHealthScaffold(
        title = uiState.toolbarTitle,
        isLoading = uiState.onLoading,
        navigationAction = onPopNavigation
    ) {
        LabTestContent(
            uiState = uiState,
            onClickViewPdf = { viewModel.getLabTestPdf(hdid) },
            onClickFaq = onClickFaq,
            onClickComments = navigateToComments,
            commentsSummary = commentState?.commentsSummary,
            onSubmitComment = { onSubmitComment(viewModel, commentsViewModel, it) }
        )
    }

    onPdfStateChanged(pdfUiState)

    handledServiceDown(uiState, viewModel, showServiceDownMessage)

    if (uiState.onError) {
        ErrorDialog()
        viewModel.resetUiState()
    }

    handlePdfDownload(uiState, viewModel, pdfDecoderViewModel)

    handleNoInternetConnection(uiState, viewModel, showNoInternetConnectionMessage)

    if (commentState?.isBcscSessionActive == false) onPopNavigation.invoke()
}

private fun onSubmitComment(
    viewModel: LabTestDetailViewModel,
    commentsViewModel: CommentsViewModel,
    content: String
) {
    viewModel.uiState.value.parentEntryId?.let { parentEntryId ->
        commentsViewModel.addComment(
            parentEntryId,
            content,
            CommentEntryTypeCode.LAB_RESULTS.value,
        )
    }
}

@Composable
private fun ObserveCommentsWorker(
    viewModel: LabTestDetailViewModel,
    commentsViewModel: CommentsViewModel
) {
    if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

    val workRequest = WorkManager
        .getInstance(LocalContext.current)
        .getWorkInfosForUniqueWorkLiveData(SYNC_COMMENTS)
        .observeAsState()
    val workState = workRequest.value?.firstOrNull()?.state
    if (workState == WorkInfo.State.SUCCEEDED && workState.isFinished) {
        LaunchedEffect(key1 = Unit) {
            viewModel.uiState.value.parentEntryId?.let { parentId ->
                commentsViewModel.getComments(parentId)
            }
        }
    }
}

private fun handledServiceDown(
    state: LabTestDetailUiState,
    viewModel: LabTestDetailViewModel,
    showServiceDownMessage: () -> Unit,
) {
    if (!state.isHgServicesUp) {
        showServiceDownMessage()
        viewModel.resetUiState()
    }
}

private fun handleNoInternetConnection(
    uiState: LabTestDetailUiState,
    viewModel: LabTestDetailViewModel,
    showNoInternetConnectionMessage: () -> Unit,
) {
    if (!uiState.isConnected) {
        showNoInternetConnectionMessage()
        viewModel.resetUiState()
    }
}

private fun handlePdfDownload(
    state: LabTestDetailUiState,
    viewModel: LabTestDetailViewModel,
    pdfDecoderViewModel: PdfDecoderViewModel,
) {
    if (state.pdfData?.isNotEmpty() == true) {
        pdfDecoderViewModel.base64ToPDFFile(state.pdfData)
        viewModel.resetPdfUiState()
    }
}

@Composable
private fun ErrorDialog() {
    AlertDialogHelper.showAlertDialog(
        context = LocalContext.current,
        title = stringResource(R.string.error),
        msg = stringResource(R.string.error_message),
        positiveBtnMsg = stringResource(R.string.dialog_button_ok)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LabTestContent(
    uiState: LabTestDetailUiState,
    onClickViewPdf: () -> Unit,
    onClickFaq: () -> Unit,
    onClickComments: (CommentsSummary) -> Unit,
    commentsSummary: CommentsSummary?,
    onSubmitComment: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onPress = { keyboardController?.hide() })
            },
    ) {
        LabTestContent(uiState, onClickViewPdf, onClickFaq, onClickComments, commentsSummary)

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentInputUI(onSubmitComment = onSubmitComment)
        }
    }
}

@Composable
private fun ColumnScope.LabTestContent(
    uiState: LabTestDetailUiState,
    onClickViewPdf: () -> Unit,
    onClickFaq: () -> Unit,
    onClickComments: (CommentsSummary) -> Unit,
    commentsSummary: CommentsSummary?,
) {
    if (uiState.labTestDetails.isNullOrEmpty()) return

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .weight(1f)
    ) {
        items(uiState.labTestDetails) { sample ->
            when (sample.viewType) {
                ITEM_VIEW_TYPE_LAB_ORDER -> LabOrderUi(sample)
                ITEM_VIEW_TYPE_LAB_TEST -> LabTestUi(sample, onClickFaq)
                ITEM_VIEW_TYPE_LAB_TEST_BANNER -> LabTestBannerUi(sample, onClickFaq)
                ITEM_VIEW_PDF -> LabTestPdfButton(onClickViewPdf)
            }
        }

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            item { Spacer(modifier = Modifier.weight(1f)) }
            item {
                CommentsSummaryUI(
                    commentsSummary = commentsSummary,
                    onClickComments = onClickComments
                )
            }
        }
    }
}

@Composable
@BasePreview
fun LabTestContentPreview() {
    val sample = listOf(
        LabTestDetail(
            bannerHeader = R.string.lab_test_banner_pending_title,
            bannerText = R.string.lab_test_banner_pending_message_1,
            bannerClickableText = R.string.lab_test_banner_pending_clickable_text,
            viewType = ITEM_VIEW_TYPE_LAB_TEST_BANNER
        ),

        LabTestDetail(
            viewType = ITEM_VIEW_PDF
        ),

        LabTestDetail(
            title1 = R.string.collection_date,
            collectionDateTime = "08/11/2022",
            timelineDateTime = "09/11/2022",
            title2 = R.string.ordering_provider,
            orderingProvider = "provider",
            title3 = R.string.reporting_lab,
            reportingSource = "source"
        ),

        LabTestDetail(
            header = R.string.test_summary,
            summary = R.string.summary_desc,
            title1 = R.string.test_name,
            testName = "the test name",
            title2 = R.string.result,
            isOutOfRange = false,
            title3 = R.string.lab_test_status,
            testStatus = R.string.corrected,
            viewType = ITEM_VIEW_TYPE_LAB_TEST
        ),

        LabTestDetail(
            header = R.string.test_summary,
            summary = R.string.summary_desc,
            title1 = R.string.test_name,
            testName = "the test name",
            title2 = R.string.result,
            isOutOfRange = false,
            title3 = R.string.lab_test_status,
            testStatus = R.string.corrected,
            viewType = ITEM_VIEW_TYPE_LAB_TEST
        ),
    )
    HealthGatewayTheme {
        LabTestContent(
            LabTestDetailUiState(labTestDetails = sample, toolbarTitle = "Lab Results"),
            {},
            {},
            {},
            null,
            {}
        )
    }
}
