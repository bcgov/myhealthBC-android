package ca.bc.gov.bchealth.ui.auth

import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-11-27 at 11:48 a.m.
 */
@Composable
fun BiometricSecurityTipDialogScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BiometricSecurityTipViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BiometricSecurityTipDialogScreenContent(onDismiss, modifier, uiState)
}

@Composable
private fun BiometricSecurityTipDialogScreenContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: BiometricSecurityTipUIState
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        BiometricSecurityTipScreenContent(
            onBackPress = onDismiss,
            modifier = modifier.requiredHeightIn(
                min = 150.dp,
                max = 400.dp
            ),
            uiState = uiState
        )
    }
}

@MultiDevicePreview
@Composable
private fun BiometricSecurityTipDialogScreenPreview(
    @PreviewParameter(BiometricSecurityTipUIStatePreview::class) uiState: BiometricSecurityTipUIState
) {
    HealthGatewayTheme {
        BiometricSecurityTipDialogScreenContent(onDismiss = {}, uiState = uiState)
    }
}

internal class BiometricSecurityTipUIStatePreview :
    PreviewParameterProvider<BiometricSecurityTipUIState> {
    override val values: Sequence<BiometricSecurityTipUIState>
        get() = sequenceOf(
            BiometricSecurityTipUIState(
                R.string.biometric_security_tip_info,
                getSecurityTipList()
            )
        )

    private fun getSecurityTipList(): List<SecurityTipItem> {

        return listOf(
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
}
