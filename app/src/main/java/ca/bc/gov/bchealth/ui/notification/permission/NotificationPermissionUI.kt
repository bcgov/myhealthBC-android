package ca.bc.gov.bchealth.ui.notification.permission

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.SmallDevicePreview
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.minButtonSize
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.ui.custom.DecorativeImage

@Composable
fun NotificationPermissionUI(
    acceptAction: () -> Unit,
    cancelAction: () -> Unit,
) {
    MyHealthTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.notification_permission_title),
                style = MyHealthTypography.h2.copy(color = primaryBlue)
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                text = stringResource(id = R.string.notification_permission_body),
                style = MyHealthTypography.body2
            )

            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter,
            ) {
                DecorativeImage(resourceId = R.drawable.img_notification_permission)
            }

            Button(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minButtonSize),
                onClick = acceptAction
            ) {
                Text(
                    text = stringResource(id = R.string.notification_permission_button_agree),
                    style = MyHealthTypography.button.bold(),
                )
            }

            OutlinedButton(
                onClick = cancelAction,
                border = BorderStroke(1.dp, primaryBlue),
                colors = ButtonDefaults.outlinedButtonColors(),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .defaultMinSize(minHeight = minButtonSize),
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = MyHealthTypography.button.bold(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@SmallDevicePreview
@BasePreview
@Composable
private fun PreviewNotificationPermissionFragment() {
    NotificationPermissionUI({}, {})
}
