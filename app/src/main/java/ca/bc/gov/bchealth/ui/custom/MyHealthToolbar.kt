package ca.bc.gov.bchealth.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.compose.white

@Composable
fun MyHealthToolbar(title: String, navigationAction: (() -> Unit)? = null) = CustomTopAppBar(
    title = {
        val paddingEnd = navigationAction?.let { 48.dp } ?: 0.dp
        Text(
            text = title,
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
