package ca.bc.gov.bchealth.ui.healthrecord.clinicaldocument

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem

@Composable
fun ClinicalDocumentDetailUI(
    viewModel: ClinicalDocumentDetailViewModel,
    navigationAction: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    MyHealthScaffold(
        title = uiState.toolbarTitle,
        navigationAction = navigationAction,
        isLoading = uiState.onLoading
    ) {
        ClinicalDocumentDetailContent(uiState) { viewModel.onClickDownload() }
    }
}

@Composable
private fun BoxScope.ClinicalDocumentDetailContent(
    uiState: ClinicalDocumentUiState,
    onClickDownload: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.TopCenter)
    ) {
        item {
            OutlinedButton(
                onClick = onClickDownload,
                border = BorderStroke(1.dp, primaryBlue),
                colors = ButtonDefaults.outlinedButtonColors(),
                modifier = Modifier
                    .padding(top = 20.dp, start = 32.dp, end = 32.dp)
                    .defaultMinSize(minHeight = 56.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.clinical_documents_detail_button_download),
                    textAlign = TextAlign.Center,
                    style = MyHealthTypography.button,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewClinicalDocumentDetailContent() {
    Box {
        ClinicalDocumentDetailContent(
            ClinicalDocumentUiState(
                uiList = listOf(
                    HealthRecordDetailItem(
                        R.string.clinical_documents_detail_discipline,
                        "Discipline value"
                    ),
                    HealthRecordDetailItem(
                        R.string.clinical_documents_detail_facility,
                        "Facility value"
                    ),
                )
            )
        ) {}
    }
}
