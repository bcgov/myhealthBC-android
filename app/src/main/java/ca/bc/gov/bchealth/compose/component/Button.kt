package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme

@Composable
fun HGButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = defaultHeight),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            disabledBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.20f),
            disabledContentColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.20f)
        ),
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HGOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = defaultHeight),
        enabled = enabled,
        border = BorderStroke(1.dp, color = MaterialTheme.colors.primary),
        colors = ButtonDefaults.outlinedButtonColors(),
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HGOutlineButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    HGOutlineButton(
        onClick,
        modifier,
        enabled,
        defaultHeight,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        }
    ) {
        HGButtonContent(
            {
                Text(
                    text = text,
                    style = if (defaultHeight == HGButtonDefaults.SmallButtonHeight) {
                        MaterialTheme.typography.body2
                    } else {
                        MaterialTheme.typography.subtitle2
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon
        )
    }
}

@Composable
fun HGButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    HGButton(
        onClick,
        modifier,
        enabled,
        defaultHeight,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        }
    ) {
        HGButtonContent(
            {
                Text(
                    text = text,
                    style = if (defaultHeight == HGButtonDefaults.SmallButtonHeight) {
                        MaterialTheme.typography.body2
                    } else {
                        MaterialTheme.typography.subtitle2
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon
        )
    }
}

@Composable
fun HGButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    content: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    HGButton(
        onClick,
        modifier,
        enabled,
        defaultHeight,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        }
    ) {
        HGButtonContent(
            content,
            leadingIcon
        )
    }
}

@Composable
fun HGTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    leadingIcon: Painter
) {

    HGTextButton(
        onClick,
        text,
        modifier,
        enabled,
        defaultHeight
    ) {
        Icon(
            painter = leadingIcon,
            contentDescription = text
        )
    }
}

@Composable
fun HGTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    HGTextButton(
        onClick,
        modifier,
        enabled,
        defaultHeight,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.TextButtonContentPadding
        } else {
            ButtonDefaults.TextButtonContentPadding
        }
    ) {
        HGButtonContent(
            {
                Text(
                    text = text,
                    style = if (defaultHeight == HGButtonDefaults.SmallButtonHeight) {
                        MaterialTheme.typography.body2
                    } else {
                        MaterialTheme.typography.subtitle2
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon
        )
    }
}

@Composable
fun HGTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    HGTextButton(
        onClick,
        modifier,
        enabled,
        defaultHeight,
        contentPadding = contentPadding
    ) {
        HGButtonContent(
            content,
            leadingIcon
        )
    }
}

@Composable
fun HGTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Dp = HGButtonDefaults.LargeButtonHeight,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit
) {

    TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = defaultHeight),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            disabledContentColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.20f)
        ),
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
private fun HGButtonContent(
    content: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null
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

val ButtonDefaults.ButtonWithIconContentPadding: PaddingValues
    get() = PaddingValues(
        start = 16.dp,
        top = 8.dp,
        end = 8.dp,
        bottom = 8.dp
    )

object HGButtonDefaults {

    val SmallButtonHeight = 42.dp

    val LargeButtonHeight = 54.dp
}

@Composable
@BasePreview
private fun HGButtonLargePreview() {
    HealthGatewayTheme {
        HGButton(onClick = { /*TODO*/ }) {
            Text(text = "Hello")
        }
    }
}

@Composable
@BasePreview
private fun HGButtonLargeDisabledPreview() {
    HealthGatewayTheme {
        HGButton(onClick = { /*TODO*/ }, enabled = false) {
        Text(text = "Hello")
    }
    }
}

@Composable
@BasePreview
private fun HGButtonSmallPreview() {
    HealthGatewayTheme {
        HGButton(onClick = { /*TODO*/ }, defaultHeight = HGButtonDefaults.SmallButtonHeight) {
        Text(text = "Hello")
    }
    }
}

@Composable
@BasePreview
private fun HGButtonLargeSmallPreview() {
    HealthGatewayTheme {
        HGButton(
            onClick = { /*TODO*/ },
            enabled = false,
            defaultHeight = HGButtonDefaults.SmallButtonHeight
        ) {
            Text(text = "Hello")
        }
    }
}

@Composable
@BasePreview
private fun HGTextButtonWithIconPreview() {
    HealthGatewayTheme {
        HGTextButton(onClick = { /*TODO*/ }, text = "TextButton") {
        Icon(painter = painterResource(id = R.drawable.ic_dismiss), contentDescription = "")
    }
    }
}
