package ca.bc.gov.bchealth.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-11-07 at 3:02 p.m.
 */
@Composable
fun BiometricSecurityTipUI(modifier: Modifier = Modifier, isExpanded: Boolean = false) {
}

@MultiDevicePreview
@Composable
private fun BiometricSecurityTipUIPreview() {

    HealthGatewayTheme {
        BiometricSecurityTipUI()
    }
}
