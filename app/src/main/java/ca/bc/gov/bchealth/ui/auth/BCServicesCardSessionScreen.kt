package ca.bc.gov.bchealth.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.greyBg

@Composable
fun BCServicesCardSessionScreen(
    modifier: Modifier,
    onLoginWithBCSCCard: () -> Unit
) {

    BCServicesCardSessionContent(
        modifier = modifier, title = stringResource(id = R.string.services),
        sessionMessage = stringResource(
            id = R.string.services_session_expired
        )
    ) {
        onLoginWithBCSCCard()
    }
}

@Composable
fun BCServicesCardSessionContent(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    sessionMessage: String,
    onLoginWithBCSCClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
    ) {

        title?.let {
            Text(text = title, style = MyHealthTypography.h2, color = MaterialTheme.colors.primary)
        }
        if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = description, style = MyHealthTypography.h4)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            backgroundColor = greyBg,
            elevation = 4.dp,
        ) {

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.session_time_out),
                    style = MyHealthTypography.h4,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = sessionMessage, style = MyHealthTypography.h4)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onLoginWithBCSCClicked() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.log_in_with_bc_services_card),
                        style = MaterialTheme.typography.button,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
@BasePreview
private fun previewBCServicesCardSession() {
    MyHealthTheme {
        BCServicesCardSessionContent(title = "Title", sessionMessage = "Message") {
        }
    }
}
