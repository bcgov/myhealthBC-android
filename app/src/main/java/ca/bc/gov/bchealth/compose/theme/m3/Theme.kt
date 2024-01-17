package ca.bc.gov.bchealth.compose.theme.m3

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.compose.theme.white

/**
 * @author pinakin.kansara
 * Created 2023-10-12 at 1:34 p.m.
 */

val lightColorScheme = lightColorScheme(
    primary = primaryBlue,
    background = white
)
val darkColorScheme = darkColorScheme(
    // M3 dark Color parameters
)

@Composable
fun HealthGatewayTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HealthGatewayTypography,
        content = content
    )
}
