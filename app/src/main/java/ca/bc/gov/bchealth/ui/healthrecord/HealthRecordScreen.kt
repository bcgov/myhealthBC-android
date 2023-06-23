package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.blue
import ca.bc.gov.bchealth.compose.grey
import ca.bc.gov.bchealth.ui.component.HGProgressIndicator
import ca.bc.gov.bchealth.ui.component.SwipeToRefreshUI

@Composable
fun HealthRecordScreen(
    modifier: Modifier,
    viewModel: HealthRecordViewModel,
    onUnlockMedicationRecords: () -> Unit,
    onClick: (HealthRecordItem) -> Unit,
    onSwipeToRefresh: () -> Unit,
    onAddNotesClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onFilterCleared: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    if (uiState.isLoading) {
        HGProgressIndicator(modifier = modifier)
    } else {

        val updatedFilters = uiState.filters.map {
            when (it) {
                HealthRecordType.MEDICATION_RECORD.name -> stringResource(R.string.medications)
                HealthRecordType.COVID_TEST_RECORD.name -> stringResource(R.string.covid_19_test_result)
                HealthRecordType.LAB_RESULT_RECORD.name -> stringResource(R.string.lab_results)
                HealthRecordType.IMMUNIZATION_RECORD.name -> stringResource(R.string.immunization)
                HealthRecordType.HEALTH_VISIT_RECORD.name -> stringResource(R.string.health_visits)
                HealthRecordType.SPECIAL_AUTHORITY_RECORD.name -> stringResource(R.string.special_authorities)
                HealthRecordType.HOSPITAL_VISITS_RECORD.name -> stringResource(R.string.hospital_visits)
                HealthRecordType.CLINICAL_DOCUMENT_RECORD.name -> stringResource(R.string.clinical_documents)
                HealthRecordType.DIAGNOSTIC_IMAGING.name -> stringResource(R.string.imaging_reports)
                else -> {
                    it
                }
            }
        }

        HealthRecordScreenContent(
            modifier,
            uiState.requiredProtectiveWordVerification,
            uiState.healthRecords,
            updatedFilters,
            uiState.notes,
            onUnlockMedicationRecords = onUnlockMedicationRecords,
            onClick = { onClick(it) },
            onSwipeToRefresh = onSwipeToRefresh,
            onAddNotesClicked = onAddNotesClicked,
            onFilterClicked = onFilterClicked,
            onFilterCleared = onFilterCleared
        )
    }
}

@Composable
private fun HealthRecordScreenContent(
    modifier: Modifier = Modifier,
    requiredProtectiveWordValidation: Boolean,
    healthRecord: List<HealthRecordItem>,
    filters: List<String>,
    notes: List<String>,
    onUnlockMedicationRecords: () -> Unit,
    onClick: (HealthRecordItem) -> Unit,
    onSwipeToRefresh: () -> Unit,
    onAddNotesClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onFilterCleared: () -> Unit
) {
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    Column(modifier = modifier.fillMaxSize()) {
        HGTabLayout(tabIndex) { index ->
            tabIndex = index
        }
        SwipeToRefreshUI(onRefresh = { onSwipeToRefresh() }) {
            when (tabIndex) {
                0 -> {
                    TimeLineScreen(
                        modifier,
                        healthRecord,
                        requiredProtectiveWordValidation,
                        filters,
                        onClick,
                        onUnlockMedicationRecords,
                        onFilterClicked,
                        onFilterCleared
                    )
                }

                1 -> NotesScreen(
                    modifier,
                    onAddNotesClicked
                )
            }
        }
    }
}

@Composable
private fun HGTabLayout(
    tabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    onTabSelected(tabIndex)
    val tabs = listOf("Timeline", "Notes")
    TabRow(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = blue, selectedTabIndex = tabIndex
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h3,
                        fontWeight = if (tabIndex != index) {
                            FontWeight.Normal
                        } else {
                            FontWeight.Bold
                        },
                        color = if (tabIndex != index) {
                            grey
                        } else {
                            MaterialTheme.colors.primary
                        }
                    )
                },
                selected = tabIndex == index,
                onClick = {
                    onTabSelected(index)
                },
            )
        }
    }
}

@BasePreview
@Composable
private fun HealthRecordScreenPreview() {
    MyHealthTheme {
        HealthRecordScreenContent(
            requiredProtectiveWordValidation = true,
            healthRecord = emptyList(), notes = emptyList(),
            filters = emptyList(),
            onUnlockMedicationRecords = {},
            onClick = {},
            onSwipeToRefresh = {},
            onAddNotesClicked = {},
            onFilterClicked = {},
            onFilterCleared = {}
        )
    }
}
