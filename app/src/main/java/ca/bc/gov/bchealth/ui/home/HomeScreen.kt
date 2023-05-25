package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar

@Composable
fun HomeScreen(greeting: String) {
    MyHealthTheme {
        Scaffold(
            topBar = {
                MyHealthToolBar(
                    title = "",
                    actions = {
                        IconButton(onClick = { }) {
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
            },
            content = {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(it),
                ) {
                    HomeContent(greeting)
                }
            },
            contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
        )
    }
}

@Composable
private fun ColumnScope.HomeContent(greeting: String) {
    Text(
        text = greeting,
        style = MyHealthTypography.h2,
        color = MaterialTheme.colors.primary
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(id = R.string.home_subtitle),
        style = MyHealthTypography.h2,
        color = MaterialTheme.colors.primary
    )
    Spacer(modifier = Modifier.height(16.dp))

    // BannerUI(ioState, {}, {}, {})
}

@BasePreview
@Composable
private fun PreviewHomeScreen() {
    HomeScreen("Hello, Bruno")
}
