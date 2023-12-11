package ca.bc.gov.bchealth.compose.component.m3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-11-15 at 3:40 p.m.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthGatewayScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(topBar = {
        HGTopAppBar(title = title)
    }, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthGatewayScaffold(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HGTopAppBar(title = title, navigationIcon = {
                IconButton(onClick = onBackPress) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_toolbar_back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            })
        },
        content = content
    )
}

@Composable
@MultiDevicePreview
private fun HealthGatewayScaffoldPreview() {
    HealthGatewayTheme {
        HealthGatewayScaffold(
            onBackPress = {}, title = stringResource(id = R.string.biometric_security_tip_title)
            ) {
            }
        }
    }
