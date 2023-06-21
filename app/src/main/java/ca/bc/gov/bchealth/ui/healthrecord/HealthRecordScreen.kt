package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.blue
import ca.bc.gov.bchealth.compose.grey
import ca.bc.gov.bchealth.compose.smallButton
import ca.bc.gov.bchealth.ui.component.EmptyStateUI
import ca.bc.gov.bchealth.ui.component.HGLargeButton
import ca.bc.gov.bchealth.ui.component.HGProgressIndicator
import ca.bc.gov.bchealth.ui.component.HealthRecordItemUI
import ca.bc.gov.bchealth.ui.component.SearchBarUI
import ca.bc.gov.bchealth.ui.component.SwipeToRefreshUI

@Composable
fun HealthRecordScreen(
    modifier: Modifier,
    viewModel: HealthRecordViewModel,
    onUnlockMedicationRecords: () -> Unit,
    onClick: (HealthRecordItem) -> Unit,
    onSwipeToRefresh: () -> Unit,
    onAddNotesClicked: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    if (uiState.isLoading) {
        HGProgressIndicator(modifier = modifier)
    } else {
        HealthRecordScreenContent(
            modifier,
            uiState.requiredProtectiveWordVerification,
            uiState.healthRecords,
            uiState.notes,
            onUnlockMedicationRecords = onUnlockMedicationRecords,
            onClick = { onClick(it) },
            onSwipeToRefresh = onSwipeToRefresh,
            onAddNotesClicked = onAddNotesClicked
        )
    }
}

@Composable
private fun HealthRecordScreenContent(
    modifier: Modifier = Modifier,
    requiredProtectiveWordValidation: Boolean,
    healthRecord: List<HealthRecordItem>,
    notes: List<String>,
    onUnlockMedicationRecords: () -> Unit,
    onClick: (HealthRecordItem) -> Unit,
    onSwipeToRefresh: () -> Unit,
    onAddNotesClicked: () -> Unit
) {
    var tabIndex by remember { mutableStateOf(0) }
    Column(modifier = modifier.fillMaxSize()) {
        HGTabLayout { index ->
            tabIndex = index
        }
        when (tabIndex) {
            0 -> {
                SwipeToRefreshUI(onRefresh = { onSwipeToRefresh() }) {
                    if (healthRecord.isEmpty()) {
                        EmptyStateUI(
                            modifier,
                            image = R.drawable.ic_no_record,
                            title = R.string.no_records_found,
                            description = R.string.refresh
                        )
                    } else {
                        TimeLine(
                            modifier,
                            requiredProtectiveWordValidation,
                            healthRecords = healthRecord,
                            onUnlockMedicationRecords = onUnlockMedicationRecords,
                            onClick = { onClick(it) }
                        )
                    }
                }
            }

            1 -> Notes(modifier, emptyList(), onAddNotesClicked)
        }
    }
}

@Composable
private fun HGTabLayout(
    onTabSelected: (Int) -> Unit
) {
    var tabIndex by rememberSaveable { mutableStateOf(0) }
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
                    tabIndex = index
                    onTabSelected(index)
                },
            )
        }
    }
}

@Composable
private fun TimeLine(
    modifier: Modifier,
    requiredProtectiveWordValidation: Boolean,
    healthRecords: List<HealthRecordItem>,
    onUnlockMedicationRecords: () -> Unit,
    onClick: (HealthRecordItem) -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {

        SearchBarUI()
        LazyColumn(
            modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (requiredProtectiveWordValidation) {
                item {
                    HiddenMedicationRecordUI(onUnlockMedicationRecords = { onUnlockMedicationRecords() })
                }
            }

            items(healthRecords) { record ->
                HealthRecordItemUI(
                    image = record.icon,
                    title = record.title,
                    description = record.description,
                    onClick = { onClick(record) }
                )
            }
        }
    }
}

@Composable
private fun HiddenMedicationRecordUI(onUnlockMedicationRecords: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.hidden_medication_records),
                style = MaterialTheme.typography.h3,
                color = blue,
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.enter_protective_word_to_access_medication_records),
                style = MaterialTheme.typography.smallButton,
                color = grey,
                fontWeight = FontWeight.Normal,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            HGLargeButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onUnlockMedicationRecords() },
                text = stringResource(id = R.string.unlock_records),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "lock"
                    )
                }
            )
        }
    }
}

@Composable
private fun Notes(
    modifier: Modifier = Modifier,
    notes: List<String> = emptyList(),
    onAddNotesClicked: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {

        FloatingActionButton(
            onClick = { onAddNotesClicked() },
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.BottomEnd)
        ) {
            Icon(painterResource(id = R.drawable.ic_add_notes), "AddNote")
        }
        if (notes.isEmpty()) {
            EmptyStateUI(
                modifier,
                image = R.drawable.ic_empty_note,
                title = R.string.notes_empty_title,
                description = R.string.notes_empty_description
            )
        } else {
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
            onUnlockMedicationRecords = {},
            onClick = {},
            onSwipeToRefresh = {},
            onAddNotesClicked = {}
        )
    }
}
