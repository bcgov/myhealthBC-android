package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem

@Composable
fun HospitalVisitDetailUI(
    viewModel: HospitalVisitDetailViewModel,
    navigationAction: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    MyHealthScaffold(
        title = uiState.toolbarTitle,
        navigationAction = navigationAction
    ) {
        HospitalVisitDetailContent(uiState.uiList)
    }
}

@Composable
private fun HospitalVisitDetailContent(
    uiList: List<HealthRecordDetailItem>,
) {
    LazyColumn {
        uiList.forEach { listItem ->
            item {
                HealthRecordListItem(
                    label = stringResource(id = listItem.title),
                    value = listItem.placeholder?.let {
                        stringResource(id = it)
                    } ?: listItem.description.orEmpty(),
                    footer = listItem.footer?.let { stringResource(it) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewHospitalVisitListItem() {

    HospitalVisitDetailContent(
        listOf(
            HealthRecordDetailItem(
                R.string.hospital_visits_detail_location_title,
                "Vancouver General Hospital",
                footer = R.string.hospital_visits_detail_location_title,
            ),
            HealthRecordDetailItem(
                R.string.hospital_visits_detail_location_title,
                "Vancouver General Hospital",
            ),
            HealthRecordDetailItem(
                R.string.hospital_visits_detail_location_title,
                "Vancouver General Hospital",
                placeholder = R.string.place_holder_text,
            )
        )
    )
}
