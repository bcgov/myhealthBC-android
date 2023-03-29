package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.descriptionGrey
import ca.bc.gov.bchealth.compose.italic

@Composable
fun HealthRecordListItem(label: String, value: String, footer: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, start = 32.dp, end = 32.dp),
    ) {
        Text(text = label, style = MyHealthTypography.body2.bold())

        if (value.isNotEmpty()) {
            Text(
                text = value,
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

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun PreviewHealthRecordListItem() {
    Column(Modifier.fillMaxSize()) {
        HealthRecordListItem("label", "value")
        HealthRecordListItem("label", "value", "footer")
        HealthRecordListItem("label", "", "footer")
        HealthRecordListItem("label", "")
    }
}
