package ca.bc.gov.bchealth.ui.healthrecord.labtest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import ca.bc.gov.bchealth.compose.theme.dividerGrey
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.ui.component.HGSmallOutlinedButton
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.utils.orPlaceholderIfNullOrBlank

@Composable
fun LabTestPdfButton(onClickViewPdf: () -> Unit) {
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
fun LabTestBannerUi(labTestDetail: LabTestDetail, onClickFaq: () -> Unit) {

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
fun LabTestUi(labTestDetail: LabTestDetail, onClickFaq: () -> Unit) {
    Column {
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
}

@Composable
fun LabOrderUi(labTestDetail: LabTestDetail) {
    Column {
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
            text = labTestDetail.getResultText()?.let { stringResource(id = it) }
                .orPlaceholderIfNullOrBlank(),
            color = labTestDetail.getResultColor(),
            style = MyHealthTypography.body2.bold()
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

@BasePreview
@Composable
private fun LabTestPdfButtonPreview() {
    HealthGatewayTheme {
        LabTestPdfButton {}
    }
}

@BasePreview
@Composable
private fun LabTestBannerUiPreview() {
    HealthGatewayTheme {
        LabTestBannerUi(
            LabTestDetail(
                bannerHeader = R.string.lab_test_banner_pending_title,
                bannerText = R.string.lab_test_banner_pending_message_1,
                bannerClickableText = R.string.lab_test_banner_pending_clickable_text,
                viewType = LabTestDetailViewModel.ITEM_VIEW_TYPE_LAB_TEST_BANNER
            ),
        ) {}
    }
}

@BasePreview
@Composable
private fun LabTestUiPreview() {
    HealthGatewayTheme {
        LabTestUi(
            LabTestDetail(
                header = R.string.test_summary,
                summary = R.string.summary_desc,
                title1 = R.string.test_name,
                testName = "the test name",
                title2 = R.string.result,
                isOutOfRange = false,
                title3 = R.string.lab_test_status,
                testStatus = R.string.corrected,
                viewType = LabTestDetailViewModel.ITEM_VIEW_TYPE_LAB_TEST
            ),
        ) {}
    }
}

@BasePreview
@Composable
private fun LabOrderUiPreview() {
    HealthGatewayTheme {
        LabOrderUi(
            LabTestDetail(
                title1 = R.string.collection_date,
                collectionDateTime = "08/11/2022",
                timelineDateTime = "09/11/2022",
                title2 = R.string.ordering_provider,
                orderingProvider = "provider",
                title3 = R.string.reporting_lab,
                reportingSource = "source"
            ),
        )
    }
}
