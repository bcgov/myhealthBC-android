package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.greyBg

@Composable
fun LoginInfoCardUI(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    buttonText: String,
    image: Painter? = null
) {
    Card(
        modifier = modifier,
        backgroundColor = greyBg
    ) {
        Box(modifier = modifier) {
            image?.let {
                Image(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    painter = it,
                    contentDescription = null
                )
            }

            Column(
                modifier = modifier
                    .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    color = blue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.height(16.dp))
                HGButton(
                    onClick = onClick,
                    text = buttonText,
                    defaultHeight = HGButtonDefaults.SmallButtonHeight
                )
            }
        }
    }
}

@Composable
@BasePreview
private fun LoginInfoCardUIPreview() {
    HealthGatewayTheme {
        LoginInfoCardUI(
            onClick = { /*TODO*/ },
            title = stringResource(id = R.string.log_in_with_bc_services_card),
            description = stringResource(id = R.string.login_to_view_hidden_records_msg),
            buttonText = stringResource(id = R.string.get_started)
        )
    }
}
