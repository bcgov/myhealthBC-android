package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import ca.bc.gov.bchealth.compose.component.HGButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.grey

@Composable
fun HiddenMedicationRecordUI(onUnlockMedicationRecords: () -> Unit) {
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
                style = MaterialTheme.typography.subtitle2,
                color = blue,
                maxLines = 2,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.enter_protective_word_to_access_medication_records),
                style = MaterialTheme.typography.body2,
                color = grey,
                fontWeight = FontWeight.Normal,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            HGButton(
                onClick = { onUnlockMedicationRecords() },
                text = stringResource(id = R.string.unlock_records),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = stringResource(id = R.string.unlock_records)
                )
            }
        }
    }
}

@BasePreview
@Composable
private fun HiddenMedicationRecordUIPreview() {
    HealthGatewayTheme {
        HiddenMedicationRecordUI {
        }
    }
}
