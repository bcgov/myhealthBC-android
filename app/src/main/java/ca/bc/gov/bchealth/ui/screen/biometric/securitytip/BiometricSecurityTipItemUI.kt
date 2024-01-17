package ca.bc.gov.bchealth.ui.screen.biometric.securitytip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.statusBlue30
import ca.bc.gov.bchealth.ui.auth.SecurityTipItem

/**
 * @author pinakin.kansara
 * Created 2023-11-20 at 1:53 p.m.
 */
@Composable
fun BiometricSecurityTipItemUI(modifier: Modifier = Modifier, securityTipItem: SecurityTipItem) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            modifier = Modifier.size(48.dp).background(statusBlue30).clip(RoundedCornerShape(4.dp)),
            painter = painterResource(id = securityTipItem.icon),
            contentScale = ContentScale.None,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(1f),
            text = stringResource(id = securityTipItem.title)
        )
    }
}

@MultiDevicePreview
@Composable
private fun BiometricSecurityTipItemUIPreview(
    @PreviewParameter(BiometricSecurityTipItemPreviewProvider::class) securityTipItem: SecurityTipItem,
) {

    HealthGatewayTheme {
        BiometricSecurityTipItemUI(securityTipItem = securityTipItem)
    }
}

internal class BiometricSecurityTipItemPreviewProvider : PreviewParameterProvider<SecurityTipItem> {

    override val values: Sequence<SecurityTipItem>
        get() = sequenceOf(
            SecurityTipItem(
                R.drawable.ic_finger_print,
                R.string.biometric_security_tip_1
            ),
            SecurityTipItem(
                R.drawable.ic_passcode,
                R.string.biometric_security_tip_2
            ),
            SecurityTipItem(
                R.drawable.ic_unlocked_device,
                R.string.biometric_security_tip_3
            )
        )
}
