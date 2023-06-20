package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.blue

@Composable
fun HomeScreen(
    patientFirstName: String?,
    bannerUiState: BannerItem?,
    onClickToggle: () -> Unit,
    onClickDismiss: () -> Unit,
    onClickLearnMore: (BannerItem) -> Unit,
    homeItems: List<HomeRecordItem>,
    onClickHomeCard: (HomeNavigationType) -> Unit,
) {
    val greeting = if (!patientFirstName.isNullOrBlank()) {
        stringResource(R.string.hi)
            .plus(" ")
            .plus(patientFirstName)
            .plus(",")
    } else {
        stringResource(R.string.hello).plus(",")
    }

    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = greeting,
        style = MyHealthTypography.h2,
        fontSize = 28.sp,
        color = MaterialTheme.colors.primary
    )
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = stringResource(id = R.string.home_subtitle),
        style = MyHealthTypography.h2,
        fontSize = 20.sp,
        color = blue
    )
    Spacer(modifier = Modifier.height(16.dp))

    bannerUiState?.let {
        BannerUI(it, onClickToggle, onClickLearnMore, onClickDismiss)
    }

    homeItems.forEach {
        HomeCardUI(uiItem = it, onClickItem = { onClickHomeCard.invoke(it.recordType) })
    }
}

@BasePreview
@Composable
private fun PreviewHomeScreen() {
    HomeScreen(
        "Bruno",
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
        ),
        onClickHomeCard = {}
    )
}
