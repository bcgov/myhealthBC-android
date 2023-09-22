package ca.bc.gov.bchealth.ui.healthrecord.imaging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.theme.descriptionGrey
import ca.bc.gov.bchealth.ui.component.HGLargeOutlinedButton
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem

@Composable
fun DiagnosticImagingDetailScreen(modifier: Modifier, viewModel: DiagnosticImagingDetailViewModel) {

    val uiState = viewModel.uiState.collectAsState().value
    DiagnosticImagingDetailContent(
        modifier,
        uiState.fileId,
        uiState.details,
        uiState.onLoading
    ) { viewModel.onClickDownload() }
}

@Composable
private fun DiagnosticImagingDetailContent(
    modifier: Modifier = Modifier,
    fileId: String? = null,
    details: List<HealthRecordDetailItem>,
    onLoading: Boolean,
    onClickDownload: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        if (onLoading) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.Center),
                color = MaterialTheme.colors.primary
            )
        }
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 24.dp, end = 32.dp, bottom = 32.dp)
                .wrapContentHeight()
        ) {
            if (!fileId.isNullOrBlank()) {
                item {
                    HGLargeOutlinedButton(
                        onClick = onClickDownload,
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.clinical_documents_detail_button_download)
                    )
                }
            }

            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    text = stringResource(id = R.string.diagnostic_imaging_detail_subtitle),
                    style = MyHealthTypography.h4,
                    color = descriptionGrey
                )
            }

            items(details) {
                HealthRecordListItem(
                    modifier = Modifier.padding(top = 24.dp),
                    stringResource(id = it.title),
                    it.description.orEmpty(),
                )
            }
        }
    }
}

@Composable
@BasePreview
private fun DiagnosticImagingDetailScreenPreview() {
    MyHealthTheme {
        DiagnosticImagingDetailContent(onLoading = true, details = emptyList()) {}
    }
}
