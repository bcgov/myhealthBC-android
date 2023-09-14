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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_PDF
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_ORDER
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_TEST
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_TEST_BANNER
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LabTestScreen(
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
fun LabTestScreenPreview() {
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
        LabTestScreen(
            LabTestDetailUiState(labTestDetails = sample, toolbarTitle = "Lab Results"),
            {},
            {},
            {},
            null,
            {}
        )
    }
}
