package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.compose.white
import ca.bc.gov.bchealth.ui.custom.DecorativeImage

@Composable
fun HomeCardUI(uiItem: HomeRecordItem, onClickItem: (HomeRecordItem) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp, start = 32.dp, end = 32.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClickItem.invoke(uiItem) }
            .background(white)
            .padding(bottom = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 32.dp, top = 32.dp)
        ) {
            DecorativeImage(resourceId = uiItem.iconTitle)

            Text(
                modifier = Modifier.padding(start = 8.dp, end = 32.dp),
                text = stringResource(id = uiItem.title),
                style = MyHealthTypography.h3
            )
        }
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp),
            text = stringResource(id = uiItem.description),
            style = MyHealthTypography.h4,
            fontSize = 13.sp,
        )

        CardButtonUI(uiItem, onClickItem)
    }
}

@Composable
private fun CardButtonUI(uiItem: HomeRecordItem, onClickItem: (HomeRecordItem) -> Unit) {
    when (uiItem.recordType) {
        HomeNavigationType.HEALTH_RECORD -> {
            Button(
                onClick = { onClickItem.invoke(uiItem) },
                modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
            ) {
                Text(
                    text = stringResource(id = uiItem.btnTitle),
                    style = MyHealthTypography.button
                )
            }
        }

        else -> {
            Row(
                modifier = Modifier.padding(top = 16.dp, start = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = uiItem.btnTitle),
                    style = MyHealthTypography.h4,
                    color = primaryBlue
                )
                DecorativeImage(
                    resourceId = uiItem.icon,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@BasePreview
@Composable
fun PreviewHomeCardUIHealthRecord() {
    MyHealthTheme {
        HomeCardUI(
            uiItem =
            HomeRecordItem(
                iconTitle = R.drawable.ic_login_info,
                title = R.string.health_records,
                description = R.string.home_recommendations_body,
                icon = R.drawable.ic_right_arrow,
                btnTitle = R.string.get_started,
                recordType = HomeNavigationType.HEALTH_RECORD

            ),
            onClickItem = {},
        )
    }
}

@BasePreview
@Composable
fun PreviewHomeCardUI() {
    MyHealthTheme {
        HomeCardUI(
            uiItem =
            HomeRecordItem(
                iconTitle = R.drawable.ic_login_info,
                title = R.string.recommendations_home_title,
                description = R.string.home_recommendations_body,
                icon = R.drawable.ic_right_arrow,
                btnTitle = R.string.get_started,
                recordType = HomeNavigationType.RECOMMENDATIONS

            ),
            onClickItem = {},
        )
    }
}
