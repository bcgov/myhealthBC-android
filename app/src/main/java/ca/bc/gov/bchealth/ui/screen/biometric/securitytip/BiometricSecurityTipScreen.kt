package ca.bc.gov.bchealth.ui.screen.biometric.securitytip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.component.m3.HealthGatewayScaffold
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipUIState
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipUIStatePreview
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipViewModel

/**
 * @author pinakin.kansara
 * Created 2023-11-15 at 1:54 p.m.
 */
@Composable
fun BiometricSecurityTipScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BiometricSecurityTipViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BiometricSecurityTipScreenContent(onBackPress, modifier, uiState)
}

@Composable
fun BiometricSecurityTipScreenContent(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: BiometricSecurityTipUIState
) {
    HealthGatewayScaffold(
        modifier = modifier,
        onBackPress = onBackPress,
        title = stringResource(id = R.string.biometric_security_tip_title)
    ) {
        LazyColumn(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(it),
            contentPadding = PaddingValues(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = uiState.securityTipInfo),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(uiState.securityTips) { securityTipItem ->
                BiometricSecurityTipItemUI(securityTipItem = securityTipItem)
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun BiometricSecurityTipScreenPreview(
    @PreviewParameter(BiometricSecurityTipUIStatePreview::class) uiState: BiometricSecurityTipUIState
) {
    HealthGatewayTheme {
        BiometricSecurityTipScreenContent(onBackPress = {}, uiState = uiState)
    }
}
