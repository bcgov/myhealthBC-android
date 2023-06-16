package ca.bc.gov.bchealth.ui.custom

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.compose.white

@Composable
fun MyHealthToolbar(
    title: String?,
    modifier: Modifier = Modifier,
    navigationAction: (() -> Unit)? = null
) = CustomTopAppBar(
    modifier = modifier,
    title = {
        val paddingEnd = navigationAction?.let { 48.dp } ?: 0.dp
        Text(
            text = title.orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = paddingEnd),
            color = primaryBlue,
            textAlign = TextAlign.Center,
            style = MyHealthTypography.h3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    backgroundColor = white,
    contentColor = primaryBlue,
    navigationIcon = {
        navigationAction?.let {
            IconButton(onClick = it) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_toolbar_back),
                    contentDescription = stringResource(
                        id = R.string.back
                    )
                )
            }
        }
    },
)

/**
 * This toolbar is currently used only in
 * - [ca.bc.gov.bchealth.ui.auth.BcServicesCardSessionFragment]
 * - [ca.bc.gov.bchealth.ui.auth.BcServicesCardLoginFragment]
 * - [ca.bc.gov.bchealth.ui.services.ServicesFragment]
 *
 * note:- it has ability to add menu items
 * in future multiple implementation will go away
 * once we improve centralize toolbar.
 */
@Composable
fun MyHealthToolBar(
    modifier: Modifier = Modifier,
    title: String = "",
    isCenterAligned: Boolean = true,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    elevation: Dp = 0.dp
) = TopAppBar(
    modifier = modifier,
    title = {
        Text(
            modifier = Modifier.fillMaxWidth(1F),
            text = title,
            style = MaterialTheme.typography.h3,
            textAlign = if (isCenterAligned) { TextAlign.Center } else {
                null
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.primary
        )
    },
    navigationIcon = navigationIcon,
    actions = actions,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation
)

@Composable
@Preview
private fun PreviewSmallTitleNoIcon() = MyHealthToolbar(title = "Small title")

@Composable
@Preview
private fun PreviewSmallTitle() = MyHealthToolbar(title = "Small title") {}

@Composable
@Preview
private fun PreviewLongTitle() =
    MyHealthToolbar(title = "Really long title to test the ellipsize property") {}

@Composable
@Preview
private fun PreviewMyHealthMaterialToolBar() {
    MyHealthTheme {
        MyHealthToolBar(
            title = "Really long title to test the ellipsize property",
            navigationIcon = {
            }, actions = {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = stringResource(
                        id = R.string.settings
                    ),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
        )
    }
}
