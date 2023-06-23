package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import ca.bc.gov.bchealth.ui.component.AppliedFilterUI
import ca.bc.gov.bchealth.ui.component.EmptyStateUI
import ca.bc.gov.bchealth.ui.component.HGLargeButton
import ca.bc.gov.bchealth.ui.component.HealthRecordItemUI
import ca.bc.gov.bchealth.ui.component.SearchBarUI

@Composable
fun TimeLineScreen(
    modifier: Modifier = Modifier,
    healthRecords: List<HealthRecordItem>,
    requiredProtectiveWordValidation: Boolean,
    filtersApplied: List<String>,
    onClick: (HealthRecordItem) -> Unit,
    onUnlockMedicationRecords: () -> Unit,
    onFilterClicked: () -> Unit,
    onFilterCleared: () -> Unit
) {
    TimeLineScreenContent(
        modifier,
        healthRecords,
        requiredProtectiveWordValidation,
        filtersApplied,
        onClick = onClick,
        onUnlockMedicationRecords = onUnlockMedicationRecords,
        onFilterClicked = onFilterClicked,
        onFilterCleared = onFilterCleared
    )
}

@Composable
private fun TimeLineScreenContent(
    modifier: Modifier = Modifier,
    healthRecords: List<HealthRecordItem> = emptyList(),
    requiredProtectiveWordValidation: Boolean,
    filtersApplied: List<String>,
    onUnlockMedicationRecords: () -> Unit = {},
    onClick: (HealthRecordItem) -> Unit = {},
    onFilterClicked: () -> Unit = {},
    onFilterCleared: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {

        SearchBarUI(modifier = Modifier.fillMaxWidth(), onFilterClicked = onFilterClicked)
        Spacer(modifier = Modifier.height(16.dp))
        if (filtersApplied.isNotEmpty()) {
            AppliedFilterUI(
                modifier = Modifier.fillMaxWidth(),
                filtersApplied,
                onFilterCleared
            )
        }

        if (healthRecords.isEmpty()) {
            EmptyStateUI(
                modifier,
                image = R.drawable.ic_no_record,
                title = R.string.no_records_found,
                description = R.string.refresh
            )
        } else {
            LazyColumn(
                modifier
                    .fillMaxSize(),
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
                        contentDescription = stringResource(id = R.string.unlock_records)
                    )
                }
            )
        }
    }
}

@Composable
@BasePreview
private fun TimeLineScreenContentPreview() {
    MyHealthTheme {
        TimeLineScreenContent(
            requiredProtectiveWordValidation = false,
            filtersApplied = emptyList()
        )
    }
}
