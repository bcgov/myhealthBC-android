package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun HomeScreen(
    greeting: String,
    bannerUiState: BannerItem,
    onClickToggle: () -> Unit,
    onClickDismiss: () -> Unit,
    onClickLearnMore: () -> Unit,
    homeItems: List<HomeRecordItem>
) {
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
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                ) {
                    HomeContent(
                        greeting,
                        bannerUiState,
                        onClickToggle,
                        onClickDismiss,
                        onClickLearnMore,
                        homeItems
                    )
                }
            },
            contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
        )
    }
}

@Composable
private fun HomeContent(
    greeting: String,
    bannerUiState: BannerItem,
    onClickToggle: () -> Unit,
    onClickDismiss: () -> Unit,
    onClickLearnMore: () -> Unit,
    homeItems: List<HomeRecordItem>,
) {
    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = greeting,
        style = MyHealthTypography.h2,
        color = MaterialTheme.colors.primary
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = stringResource(id = R.string.home_subtitle),
        style = MyHealthTypography.h2,
        color = MaterialTheme.colors.primary
    )
    Spacer(modifier = Modifier.height(16.dp))

    BannerUI(bannerUiState, onClickToggle, onClickLearnMore, onClickDismiss)

    homeItems.forEach {
        HomeCardUI(uiItem = it, onClickItem = {})
    }
}

@BasePreview
@Composable
private fun PreviewHomeScreen() {
    HomeScreen(
        "Hello, Bruno",
        bannerUiState = BannerItem(
            title = "Great news! Really Big Announcement",
            body = "View and manage all your available health records, including dispensed medications, health visits, COVID-19 test results, immunizations and more.",
            date = "",
            displayReadMore = true,
            isHidden = false,
        ),
        onClickToggle = {},
        onClickLearnMore = {},
        onClickDismiss = {},
        homeItems = listOf(
            HomeRecordItem(
                iconTitle = R.drawable.ic_login_info,
                title = R.string.recommendations_home_title,
                description = R.string.home_recommendations_body,
                icon = R.drawable.ic_right_arrow,
                btnTitle = R.string.get_started,
                recordType = HomeNavigationType.RECOMMENDATIONS

            ),
            HomeRecordItem(
                iconTitle = R.drawable.ic_login_info,
                title = R.string.recommendations_home_title,
                description = R.string.home_recommendations_body,
                icon = R.drawable.ic_right_arrow,
                btnTitle = R.string.get_started,
                recordType = HomeNavigationType.RECOMMENDATIONS

            ),
            HomeRecordItem(
                iconTitle = R.drawable.ic_login_info,
                title = R.string.recommendations_home_title,
                description = R.string.home_recommendations_body,
                icon = R.drawable.ic_right_arrow,
                btnTitle = R.string.get_started,
                recordType = HomeNavigationType.RECOMMENDATIONS
            )
        )
    )
}
