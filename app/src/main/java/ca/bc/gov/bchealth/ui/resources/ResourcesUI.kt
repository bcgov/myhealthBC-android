package ca.bc.gov.bchealth.ui.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.greyBg
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold

@Composable
fun ResourcesUI(
    uiList: List<ResourceItem>,
    navigationAction: () -> Unit,
    onClickResource: (Int) -> Unit
) {
    MyHealthScaffold(
        title = stringResource(R.string.health_resources),
        navigationAction = navigationAction
    ) {
        ResourcesContent(uiList, onClickResource)
    }
}

@Composable
private fun ResourcesContent(
    uiList: List<ResourceItem>,
    onClickResource: (Int) -> Unit
) {
    LazyColumn {
        item {
            Text(
                modifier = Modifier.padding(
                    start = 32.dp,
                    end = 32.dp,
                    top = 10.dp,
                    bottom = 20.dp
                ),
                text = stringResource(id = R.string.resource_screen_message),
                style = MyHealthTypography.body2
            )
        }
        uiList.forEach { listItem ->
            item {
                ResourceItemUI(listItem, onClickResource)
            }
        }
    }
}

@Composable
private fun ResourceItemUI(resourceItem: ResourceItem, onClickResource: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 32.dp, end = 32.dp, bottom = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(greyBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = { onClickResource(resourceItem.link) }
            )
    ) {
        DecorativeImage(resourceId = resourceItem.icon, modifier = Modifier.padding(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = resourceItem.title),
            style = MyHealthTypography.h3.copy(color = blue)
        )
        DecorativeImage(
            resourceId = R.drawable.ic_arrow_forward,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )
    }
}

@Composable
@BasePreview
private fun PreviewResourcesUI() {
    ResourcesUI(
        listOf(
            ResourceItem(
                R.drawable.ic_resources_advice,
                R.string.label_resource_advice,
                -1
            ),
            ResourceItem(
                R.drawable.ic_resources_advice,
                R.string.label_resource_advice,
                -1
            ),
            ResourceItem(
                R.drawable.ic_resources_advice,
                R.string.label_resource_advice,
                -1
            ),
            ResourceItem(
                R.drawable.ic_resources_advice,
                R.string.label_resource_advice,
                -1
            ),
        ),
        {}, {}
    )
}
