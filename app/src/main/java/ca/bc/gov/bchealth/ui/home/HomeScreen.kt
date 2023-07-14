package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.AnnouncementBannerUI
import ca.bc.gov.bchealth.compose.component.LoginInfoCardUI
import ca.bc.gov.bchealth.compose.component.QuickAccessTileItemUI
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeComposeViewModel,
    onQuickAccessTileClicked: (QuickAccessTileItem) -> Unit,
    onClickManage: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value
    LaunchedEffect(key1 = Unit) {
        viewModel.loadQuickAccessTiles()
    }
    HomeScreenContent(
        modifier,
        onQuickAccessTileClicked,
        onClickManage,
        uiState.quickAccessTileItems
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onQuickAccessTileClicked: (QuickAccessTileItem) -> Unit,
    onClickManage: () -> Unit,
    quickAccessTileItems: List<QuickAccessTileItem>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item(span = { GridItemSpan(maxLineSpan) }) {
            AnnouncementBannerUI(
                title = stringResource(id = R.string.home_banner_toolbar_title),
                description = stringResource(id = R.string.home_banner_toolbar_title),
                onLearnMoreClick = { /*TODO*/ },
                onDismissClick = { /*TODO*/ }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            LoginInfoCardUI(
                onClick = { /*TODO*/ },
                title = stringResource(id = R.string.log_in_with_bc_services_card),
                subTitle = stringResource(id = R.string.login_to_view_hidden_records_msg)
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.quick_access),
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )

                // todo: Replace it: HAPP-1537
                Text(
                    modifier = Modifier.clickable { onClickManage.invoke() },
                    text = "Manage",
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
        }

        items(quickAccessTileItems) {
            QuickAccessTileItemUi(it, onQuickAccessTileClicked)
        }
    }
}

@Composable
private fun QuickAccessTileItemUi(
    item: QuickAccessTileItem,
    onQuickAccessTileClicked: (QuickAccessTileItem) -> Unit
) {
    val title: String
    val icon: Int

    when (item) {
        is QuickAccessTileItem.PredefinedItem -> {
            icon = item.icon
            title = stringResource(id = item.nameId)
        }

        is QuickAccessTileItem.DynamicItem -> {
            icon = item.icon
            title = if (item.nameId == null) {
                item.text
            } else {
                stringResource(item.nameId, item.text).replaceFirst("s’s", "s’")
            }
        }
    }

    QuickAccessTileItemUI(
        onClick = { onQuickAccessTileClicked(item) },
        icon = painterResource(id = icon),
        title = title
    )
}

@Composable
@BasePreview
private fun HomeScreenPreview() {
    val dynamicItemSample = QuickAccessTileItem.DynamicItem(
        id = 0,
        icon = R.drawable.ic_health_record,
        nameId = -1,
        text = "",
        destinationId = -1,
        destinationParam = null,
        categoryId = -1,
        enabled = true,
    )

    HealthGatewayTheme {
        HomeScreenContent(
            onQuickAccessTileClicked = {},
            onClickManage = {},
            quickAccessTileItems = listOf(
                dynamicItemSample.copy(
                    nameId = R.string.feature_quick_action_dependents,
                    text = "Jane",
                ),
                dynamicItemSample.copy(
                    nameId = R.string.feature_quick_action_dependents,
                    text = "James",
                ),
                dynamicItemSample.copy(
                    nameId = null,
                    text = "Dynamic text",
                ),
                QuickAccessTileItem.PredefinedItem(
                    id = 0,
                    icon = R.drawable.ic_health_record,
                    nameId = R.string.immnz_schedules_infant,
                    destinationId = -1,
                    destinationParam = null,
                    categoryId = -1,
                    enabled = true,
                ),
            )
        )
    }
}
