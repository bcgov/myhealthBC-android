package ca.bc.gov.bchealth.ui.custom

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.compose.white

@Composable
fun HappToolbar(title: String, navigationAction: (() -> Unit)? = null) = TopAppBar(
    title = {
        Text(
            text = title,
            color = primaryBlue,
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
