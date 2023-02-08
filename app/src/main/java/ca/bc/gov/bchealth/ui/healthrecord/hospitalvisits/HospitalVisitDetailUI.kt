package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.descriptionGrey
import ca.bc.gov.bchealth.compose.italic

@Composable
fun HospitalVisitDetailUI(uiList: List<HospitalVisitDetailItem>) {
    LazyColumn {
        uiList.forEach { listItem ->
            item {
                HospitalVisitListItem(
                    label = stringResource(id = listItem.title),
                    body = listItem.placeholder?.let {
                        stringResource(id = it)
                    } ?: listItem.description,
                    footer = listItem.footer?.let { stringResource(it) }
                )
            }
        }
    }
}

@Composable
fun HospitalVisitListItem(label: String, body: String, footer: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp, bottom = 10.dp, start = 32.dp),
    ) {
        Text(text = label, style = MyHealthTypography.body2.bold())

        if (body.isNotEmpty()) {
            Text(
                text = body,
                style = MyHealthTypography.body2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        footer?.let {
            Text(
                text = it,
                style = MyHealthTypography.body2.italic().copy(color = descriptionGrey),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewHospitalVisitListItem() {
    LazyColumn {
        item {
            HospitalVisitListItem(
                "Location",
                "Vancouver General Hospital",
                "Virtual visits show your provider's location"
            )
        }

        item {
            HospitalVisitListItem(
                "Visit type",
                "Patient",
                null
            )
        }
    }
}
