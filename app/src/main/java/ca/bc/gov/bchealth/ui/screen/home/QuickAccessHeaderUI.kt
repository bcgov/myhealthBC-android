package ca.bc.gov.bchealth.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.component.m3.HGTextButton
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import ca.bc.gov.common.model.UserAuthenticationStatus

/**
 * @author pinakin.kansara
 * Created 2024-01-11 at 12:47 p.m.
 */
@Composable
fun QuickAccessHeaderUI(
    onManageClick: () -> Unit,
    onDismissTutorialClicked: () -> Unit,
    uiState: HomeUiState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.quick_access),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        AnimatedVisibility(visible = (UserAuthenticationStatus.AUTHENTICATED == uiState.userAuthenticationStatus)) {
            HGTextButton(onClick = onManageClick) {
                Text(
                    text = stringResource(id = R.string.manage),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = blue,
                    textDecoration = TextDecoration.Underline
                )
            }
            if (uiState.isQuickAccessTileTutorialRequired) {
                QuickAccessManagementTutorialUI(onDismissTutorialClicked = onDismissTutorialClicked)
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun QuickAccessHeaderUIPreview() {
    HealthGatewayTheme {
        QuickAccessHeaderUI(onManageClick = {}, onDismissTutorialClicked = {}, HomeUiState())
    }
}
