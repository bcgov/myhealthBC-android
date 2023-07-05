package ca.bc.gov.bchealth.ui.home.immunizationschedules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.theme.bannerInfoBg
import ca.bc.gov.bchealth.compose.theme.white
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.home.immunizationschedules.ImmunizationSchedulesViewModel.ImmunizationSchedulesItem

@Composable
fun ImmunizationSchedulesScreen(
    uiList: List<ImmunizationSchedulesItem>,
    onClickItem: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 22.dp),
        content = {
            item {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(id = R.string.immnz_schedules_body),
                    style = MyHealthTypography.caption
                )
            }
            items(uiList) {
                ImmunizationScheduleUI(it, onClickItem)
            }
        }
    )
}

@Composable
private fun ImmunizationScheduleUI(item: ImmunizationSchedulesItem, onClickItem: (String) -> Unit) {
    val url = stringResource(id = item.url)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClickItem.invoke(url) }
            .background(white)
            .padding(start = 10.dp, end = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 10.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(bannerInfoBg),
            contentAlignment = Alignment.Center
        ) {

            DecorativeImage(resourceId = item.icon)
        }

        Text(
            modifier = Modifier
                .padding(start = 22.dp)
                .fillMaxWidth()
                .weight(1f),
            style = MyHealthTypography.body2.bold(),
            text = stringResource(id = item.title)
        )
        DecorativeImage(
            resourceId = R.drawable.ic_arrow_right_24
        )
    }
}

@BasePreview
@Composable
private fun PreviewImmunizationSchedulesScreen() {
    val viewModel = ImmunizationSchedulesViewModel()
    ImmunizationSchedulesScreen(
        viewModel.getUiList()
    ) {}
}
