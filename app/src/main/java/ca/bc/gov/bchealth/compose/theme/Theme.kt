package ca.bc.gov.bchealth.compose.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColors = lightColors(
    primary = primaryBlue,
    onPrimary = white,
    background = white,
    surface = white
)

@Composable
fun HealthGatewayTheme(content: @Composable () -> Unit) =
    MaterialTheme(colors = LightColors, typography = HealthGatewayTypography, content = content)
