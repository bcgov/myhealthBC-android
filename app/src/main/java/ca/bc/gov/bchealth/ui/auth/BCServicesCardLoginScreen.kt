package ca.bc.gov.bchealth.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography

@Composable
fun BcServicesCardLoginScreen(
    modifier: Modifier,
    loginStateInfo: LoginStateInfo,
    onLoginWithBCSCCard: () -> Unit
) {

    BcServicesCardLoginContent(
        modifier = modifier,
        title = stringResource(id = loginStateInfo.title),
        subtitle = stringResource(id = loginStateInfo.description),
        icon = painterResource(id = loginStateInfo.icon),
        subtitle1 = stringResource(id = loginStateInfo.info)
    ) {
        onLoginWithBCSCCard()
    }
}

@Composable
fun BcServicesCardLoginContent(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: Painter,
    subtitle1: String,
    onLoginWithBCSCCard: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
    ) {

        Text(text = title, style = MyHealthTypography.h2, color = MaterialTheme.colors.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = subtitle, style = MyHealthTypography.h4)
        Spacer(modifier = Modifier.height(64.dp))
        Image(
            painter = icon,
            contentDescription = "HealthRecord",
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = subtitle1, style = MyHealthTypography.h6, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1F))
        Button(
            onClick = { onLoginWithBCSCCard() },
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

@Composable
@BasePreview
private fun previewBcServiceCardLoginContent() {

    MyHealthTheme {
        BcServicesCardLoginContent(
            title = "Tes",
            subtitle = "Test",
            icon = painterResource(id = R.drawable.ic_add_comment),
            subtitle1 = "Icon Desc"
        ) {
        }
    }
}
