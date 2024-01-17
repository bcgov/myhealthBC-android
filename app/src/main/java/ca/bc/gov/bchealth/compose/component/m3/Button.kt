package ca.bc.gov.bchealth.compose.component.m3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    minHeight: Dp = ButtonDefaults.MinHeight,
    leadingIcon: Painter
) {
    HGTextButton(
        onClick = onClick,
        text = text,
        modifier = modifier,
        enabled = enabled,
        minHeight = minHeight,
        leadingIcon = {
            Icon(
                painter = leadingIcon,
                contentDescription = text
            )
        }
    )
}

@Composable
fun HGTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minHeight: Dp = ButtonDefaults.MinHeight,
    leadingIcon: @Composable (() -> Unit)? = null
) {

    HGTextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = minHeight),
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.TextButtonWithIconContentPadding
        } else {
            ButtonDefaults.TextButtonContentPadding
        },
        shape = ShapeDefaults.Small
    ) {
        HGButtonContent(
            content = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = leadingIcon
        )
    }
}

@Composable
fun HGTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ShapeDefaults.Small,
    minHeight: Dp = ButtonDefaults.MinHeight,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = minHeight),
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
private fun HGButtonContent(
    content: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    if (leadingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            leadingIcon()
        }
    }
    Box(
        Modifier
            .padding(
                start = if (leadingIcon != null) {
                    ButtonDefaults.IconSpacing
                } else {
                    0.dp
                },
            ),
    ) {
        content()
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
