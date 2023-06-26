package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
    onQuickAccessTileClicked: (QuickAccessTileItem) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    LaunchedEffect(key1 = Unit) {
        viewModel.loadQuickAccessTiles()
    }
    HomeScreenContent(modifier, onQuickAccessTileClicked, uiState.quickAccessTileItems)
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onQuickAccessTileClicked: (QuickAccessTileItem) -> Unit,
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
            Text(
                text = stringResource(id = R.string.quick_access),
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
        }

        items(quickAccessTileItems) {
            QuickAccessTileItemUI(
                onClick = { onQuickAccessTileClicked(it) },
                icon = painterResource(id = it.icon),
                title = stringResource(
                    it.name
                )
            )
        }
    }
}

@Composable
@BasePreview
private fun HomeScreenPreview() {
    HealthGatewayTheme {
        HomeScreenContent(onQuickAccessTileClicked = {}, quickAccessTileItems = emptyList())
    }
}
