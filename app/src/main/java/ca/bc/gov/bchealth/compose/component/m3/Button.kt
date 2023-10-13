package ca.bc.gov.bchealth.compose.component.m3

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-10-18 at 11:27 a.m.
 */
@Composable
fun HGButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = minHeight),
        enabled = enabled,
        shape = ShapeDefaults.Small,
        colors = colors
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun HGTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minHeight: Dp = ButtonDefaults.MinHeight
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = minHeight),
        enabled = enabled,
        shape = ShapeDefaults.Small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun HGButtonPreview() {
    HealthGatewayTheme {
        HGButton(onClick = { /*TODO*/ }, text = "Button")
    }
}

@Preview
@Composable
private fun HgTextButtonPreview() {
    HealthGatewayTheme {
        HGTextButton(onClick = { /*TODO*/ }, text = "Text Button")
    }
}
