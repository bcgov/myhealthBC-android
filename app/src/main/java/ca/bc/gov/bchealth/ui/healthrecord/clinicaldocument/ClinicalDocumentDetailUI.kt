package ca.bc.gov.bchealth.ui.healthrecord.clinicaldocument

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold

@Composable
fun ClinicalDocumentDetailUI(
    uiList: List<ClinicalDocumentDetailItem>,
    onClickDownload: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        item {
            OutlinedButton(onClick = onClickDownload) {
                Text(
                    stringResource(id = R.string.clinical_documents_detail_button_download),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        uiList.forEach { listItem ->
            item {
                ClinicalDocumentListItem(
                    stringResource(id = listItem.title),
                    listItem.description,
                )
            }
        }
    }
}

@Composable
fun ClinicalDocumentListItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp, bottom = 20.dp, start = 32.dp),
    ) {
        Text(text = label, style = MyHealthTypography.body2.bold())

        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MyHealthTypography.body2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewClinicalDocumentDetailUI() {
    ClinicalDocumentDetailUI(
        listOf(
            ClinicalDocumentDetailItem(
                R.string.clinical_documents_detail_discipline,
                "Discipline value"
            ),
            ClinicalDocumentDetailItem(
                R.string.clinical_documents_detail_facility,
                "Facility value"
            ),
        )
    ) {}
}
