package ca.bc.gov.bchealth.ui.healthrecord.healthvisits

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem

@Composable
fun HealthVisitDetailUI(
    viewModel: HealthVisitViewModel,
    navigationAction: () -> Unit,
    onClickFaq: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    MyHealthScaffold(
        title = uiState.title,
        navigationAction = navigationAction
    ) {
        HealthVisitDetailContent(uiState, onClickFaq)
    }
}

@Composable
private fun HealthVisitDetailContent(uiState: HealthVisitDetailUiState, onClickFaq: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                MyHealthClickableText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                    style = MyHealthTypography.caption,
                    fullText = stringResource(id = R.string.information_is_from_the_billing_claim),
                    clickableText = stringResource(id = R.string.faq),
                    action = onClickFaq
                )
            }
            uiState.uiList.forEach { listItem ->
                item {
                    HealthRecordListItem(
                        stringResource(id = listItem.title),
                        listItem.description.orEmpty(),
                    )
                }
            }
        }
        if (uiState.onLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewHealthVisitDetailContent() {
    HealthVisitDetailContent(
        HealthVisitDetailUiState(
            uiList = listOf(
                HealthRecordDetailItem(
                    R.string.clinic_name,
                    "FRANCIS N WER"
                ),
                HealthRecordDetailItem(
                    R.string.practitioner_name,
                    "Daniel Something"
                )
            )
        )
    ) {}
}
