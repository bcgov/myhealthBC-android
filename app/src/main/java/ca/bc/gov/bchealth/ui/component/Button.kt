package ca.bc.gov.bchealth.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.largeButton
import ca.bc.gov.bchealth.compose.smallButton

@Composable
fun HGButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
        ),
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HGLargeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    HGButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 54.dp),
        enabled
    ) {
        Text(text = text, style = MaterialTheme.typography.largeButton)
    }
}

@Composable
fun HGSmallButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    HGButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 42.dp),
        enabled
    ) {
        Text(text = text, style = MaterialTheme.typography.smallButton)
    }
}

@Composable
fun HGOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        border = BorderStroke(1.dp, color = MaterialTheme.colors.primary),
        colors = ButtonDefaults.outlinedButtonColors(),
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HGLargeOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    HGOutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 54.dp),
        enabled
    ) {
        Text(text = text, style = MaterialTheme.typography.largeButton)
    }
}

@Composable
fun HGSmallOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    HGOutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 38.dp),
        enabled
    ) {
        Text(text = text, style = MaterialTheme.typography.smallButton)
    }
}

@BasePreview
@Composable
private fun LargeButtonPreview() {
    MyHealthTheme {
        HGLargeOutlinedButton(onClick = { }, text = "Hello")
    }
}

@BasePreview
@Composable
private fun LargeOutlinedButtonPreview() {
    MyHealthTheme {
        HGLargeButton(onClick = { }, text = "Hello")
    }
}

@BasePreview
@Composable
private fun SmallButtonPreview() {
    MyHealthTheme {
        HGSmallButton(onClick = { }, text = "Hello")
    }
}

@BasePreview
@Composable
private fun SmallOutlinedButtonPreview() {
    MyHealthTheme {
        HGSmallOutlinedButton(onClick = { }, text = "Hello")
    }
}
