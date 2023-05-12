package ca.bc.gov.bchealth.ui.login.error

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.SmallDevicePreview
import ca.bc.gov.bchealth.compose.minButtonSize
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold

@Composable
fun BcscAuthErrorUI(navigationAction: () -> Unit, onClickEmail: () -> Unit) {
    MyHealthScaffold(
        title = stringResource(id = R.string.bcsc_auth_error_title),
        navigationAction = navigationAction
    ) {
        BcscAuthErrorContent(onClickEmail)
    }
}

@Composable
private fun BcscAuthErrorContent(onClickEmail: () -> Unit) {
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.weight(1f))

        DecorativeImage(resourceId = R.drawable.ic_bcsc_auth_error)

        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(R.string.bcsc_auth_error_title),
            style = MyHealthTypography.h2
        )

        MyHealthClickableText(
            style = MyHealthTypography.body2.copy(textAlign = TextAlign.Center),
            fullText = stringResource(R.string.bcsc_auth_error_body),
            clickableText = stringResource(R.string.bcsc_auth_error_click_email),
            action = onClickEmail
        )

        Spacer(modifier = Modifier.weight(1.3f))

        Button(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .defaultMinSize(minHeight = minButtonSize)
                .fillMaxWidth(),
            onClick = onClickEmail,
        ) {

            Row(Modifier.wrapContentSize(align = Alignment.Center)) {

                DecorativeImage(
                    resourceId = R.drawable.ic_email,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .align(alignment = CenterVertically)
                )
                Text(
                    style = MyHealthTypography.button,
                    text = stringResource(R.string.bcsc_auth_error_button)
                )
            }
        }
    }
}

@Composable
@BasePreview
@SmallDevicePreview
private fun PreviewBcscAuthErrorContent() {
    BcscAuthErrorUI({}, {})
}
