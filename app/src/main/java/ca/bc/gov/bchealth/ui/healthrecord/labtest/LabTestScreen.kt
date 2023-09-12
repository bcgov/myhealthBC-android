package ca.bc.gov.bchealth.ui.healthrecord.labtest

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.bannerBackgroundBlue
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.darkText
import ca.bc.gov.bchealth.compose.theme.dividerGrey
import ca.bc.gov.bchealth.compose.theme.green
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.compose.theme.red
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.component.HGSmallOutlinedButton
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_PDF
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_ORDER
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_TEST
import ca.bc.gov.bchealth.ui.healthrecord.labtest.LabTestDetailViewModel.Companion.ITEM_VIEW_TYPE_LAB_TEST_BANNER
import ca.bc.gov.bchealth.utils.orPlaceholderIfNullOrBlank
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
        LabTestUi(uiState, onClickViewPdf, onClickFaq, onClickComments, commentsSummary)

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentInputUI(onSubmitComment = onSubmitComment)
        }
    }
}

@Composable
private fun ColumnScope.LabTestUi(
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
private fun LabTestPdfButton(onClickViewPdf: () -> Unit) {
    HGSmallOutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        onClick = onClickViewPdf,
        text = "View PDF"
    )

    LabTestDivider()
}

@Composable
private fun LabTestBannerUi(labTestDetail: LabTestDetail, onClickFaq: () -> Unit) {

    val title = labTestDetail.bannerHeader?.let { stringResource(it) } ?: return
    val body = labTestDetail.bannerText?.let { stringResource(it) } ?: return
    val clickableText = labTestDetail.bannerClickableText?.let { stringResource(it) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(color = bannerBackgroundBlue)
            .padding(16.dp)
    ) {
        DecorativeImage(resourceId = R.drawable.ic_info)
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = title,
                color = blue,
                style = MyHealthTypography.body2.bold()
            )

            clickableText?.let {
                MyHealthClickableText(
                    style = MyHealthTypography.body2.copy(color = blue),
                    fullText = body,
                    clickableText = it,
                    action = onClickFaq,
                    clickableStyle = SpanStyle(
                        color = primaryBlue,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                )
            } ?: run {
                Text(
                    style = MyHealthTypography.body2.copy(color = blue),
                    text = body,
                )
            }
        }
    }
}

@Composable
private fun LabTestUi(labTestDetail: LabTestDetail, onClickFaq: () -> Unit) {
    LabTestHeaderUi(
        labTestDetail.header,
        labTestDetail.summary?.let { stringResource(id = it) },
        onClickFaq
    )

    LabTestItemUi(
        labTestDetail.title1,
        labTestDetail.testName.orPlaceholderIfNullOrBlank(),
    )

    LabTestResultItemUi(labTestDetail)

    LabTestItemUi(
        labTestDetail.title3,
        labTestDetail.testStatus?.let { stringResource(id = it) },
    )

    LabTestDivider()
}

@Composable
private fun LabOrderUi(labTestDetail: LabTestDetail) {
    LabTestItemUi(
        labTestDetail.title1,
        labTestDetail.timelineDateTime.orPlaceholderIfNullOrBlank(),
    )

    LabTestItemUi(
        labTestDetail.title2,
        labTestDetail.orderingProvider.orPlaceholderIfNullOrBlank(),
    )

    LabTestItemUi(
        labTestDetail.title3,
        labTestDetail.reportingSource.orPlaceholderIfNullOrBlank(),
    )

    LabTestDivider()
}

@Composable
private fun LabTestHeaderUi(title: Int?, body: String?, onClickFaq: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        title?.let {
            Text(
                text = stringResource(id = it),
                style = MyHealthTypography.body2.bold()
            )
        }

        body?.let {
            MyHealthClickableText(
                style = MyHealthTypography.body2,
                fullText = it,
                clickableText = stringResource(R.string.learn_more),
                action = onClickFaq,
                clickableStyle = SpanStyle(
                    color = primaryBlue,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun LabTestItemUi(title: Int?, body: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        title?.let {
            Text(
                text = stringResource(id = it),
                style = MyHealthTypography.body2.bold()
            )
        }

        body?.let {
            Text(
                text = it,
                style = MyHealthTypography.body2
            )
        }
    }
}

@Composable
private fun LabTestResultItemUi(labTestDetail: LabTestDetail) {

    val (result, color) = getTestResult(
        labTestDetail.isOutOfRange,
        labTestDetail.testStatus,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        labTestDetail.title2?.let {
            Text(
                text = stringResource(id = it),
                style = MyHealthTypography.body2.bold()
            )
        }

        Text(
            text = result?.let { stringResource(id = it) }.orPlaceholderIfNullOrBlank(),
            color = color,
            style = MyHealthTypography.body2.bold()
        )
    }
}

private fun getTestResult(
    outOfRange: Boolean?,
    testStatus: Int?,
): Pair<Int?, Color> {
    outOfRange?.let {
        return if (outOfRange) {
            Pair(
                R.string.out_of_range,
                red
            )
        } else {
            Pair(
                R.string.in_range,
                green
            )
        }
    } ?: run {
        return Pair(
            testStatus,
            darkText
        )
    }
}

@Composable
private fun LabTestDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        color = dividerGrey,
        thickness = 1.dp
    )
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
