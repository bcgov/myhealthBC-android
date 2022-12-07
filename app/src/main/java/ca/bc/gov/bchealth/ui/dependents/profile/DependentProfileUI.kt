package ca.bc.gov.bchealth.ui.dependents.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.statusBlue30

@Composable
fun DependentProfileUI(dependentItems: List<DependentProfileViewModel.DependentProfileItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        ListDivider()

        dependentItems.forEach {
            DependentProfileItem(stringResource(id = it.label), it.value)
            ListDivider()
        }
    }
}

@Composable
fun DependentProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp, bottom = 20.dp, start = 32.dp),
    ) {
        Text(text = label, style = MyHealthTypography.body1)
        Text(
            text = value,
            style = MyHealthTypography.body2,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ListDivider() {
    Divider(
        thickness = 0.5.dp,
        color = statusBlue30
    )
}
