package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.menu.ActionMenu
import ca.bc.gov.bchealth.compose.component.menu.TopAppBarActionItem
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.primaryBlue

@Composable
fun HGTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    actionItems: List<TopAppBarActionItem> = emptyList(),
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    var menuOpen by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            ActionMenu(
                items = actionItems,
                isOpen = menuOpen,
                onToggleOverflow = { menuOpen = !menuOpen },
                maxVisibleItems = 3
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.primary,
        elevation = elevation
    )
}

@Composable
fun HGCenterAlignedTopAppBar(
    modifier: Modifier = Modifier,
    onNavigationAction: () -> Unit,
    title: String,
    actionItems: List<TopAppBarActionItem> = emptyList()
) {
    var menuOpen by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { onNavigationAction() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_toolbar_back),
                    contentDescription = stringResource(id = R.string.back),
                    tint = primaryBlue
                )
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        actions = {
            ActionMenu(
                items = actionItems,
                isOpen = menuOpen,
                onToggleOverflow = { menuOpen = !menuOpen },
                maxVisibleItems = 3
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.primary
    )
}

@Composable
@BasePreview
private fun HGTopAppBarPreview() {
    HealthGatewayTheme {
        HGTopAppBar(title = stringResource(id = R.string.home))
    }
}

@Composable
@BasePreview
private fun HGCenterAlignedTopAppBarPreview() {
    HealthGatewayTheme {
        HGCenterAlignedTopAppBar(onNavigationAction = {}, title = stringResource(id = R.string.home))
    }
}
