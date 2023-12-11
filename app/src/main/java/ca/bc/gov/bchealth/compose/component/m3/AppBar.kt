package ca.bc.gov.bchealth.compose.component.m3

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import ca.bc.gov.bchealth.compose.component.menu.ActionMenu
import ca.bc.gov.bchealth.compose.component.menu.TopAppBarActionItem

/**
 * @author pinakin.kansara
 * Created 2023-11-07 at 10:55 a.m.
 */
@ExperimentalMaterial3Api
@Composable
fun HGTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: List<TopAppBarActionItem> = emptyList(),
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    HGTopAppBar(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@ExperimentalMaterial3Api
@Composable
fun HGTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: List<TopAppBarActionItem> = emptyList(),
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    var menuOpen by remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = {
            ActionMenu(
                items = actions,
                isOpen = menuOpen,
                onToggleOverflow = { menuOpen = !menuOpen },
                maxVisibleItems = 3
            )
        },
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}
