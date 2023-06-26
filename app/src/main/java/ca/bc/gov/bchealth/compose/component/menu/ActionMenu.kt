package ca.bc.gov.bchealth.compose.component.menu

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ca.bc.gov.bchealth.R

@Composable
fun ActionMenu(
    items: List<TopAppBarActionItem>,
    isOpen: Boolean,
    onToggleOverflow: () -> Unit,
    maxVisibleItems: Int,
) {
    val menuItems = remember(
        key1 = items,
        key2 = maxVisibleItems,
    ) {
        splitMenuItems(items, maxVisibleItems)
    }

    menuItems.alwaysShownItems.forEach { item ->
        IconButton(onClick = item.onClick) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = item.contentDescription,
                tint = MaterialTheme.colors.primary
            )
        }
    }

    if (menuItems.overflowItems.isNotEmpty()) {
        IconButton(onClick = onToggleOverflow) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(id = R.string.more_options),
                tint = MaterialTheme.colors.primary
            )
        }
        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = onToggleOverflow,
        ) {
            menuItems.overflowItems.forEach { item ->
                DropdownMenuItem(
                    content = {
                        Text(item.title)
                    },
                    onClick = item.onClick
                )
            }
        }
    }
}

private data class MenuItems(
    val alwaysShownItems: List<TopAppBarActionItem.IconActionItem>,
    val overflowItems: List<TopAppBarActionItem>,
)

private fun splitMenuItems(
    items: List<TopAppBarActionItem>,
    maxVisibleItems: Int,
): MenuItems {
    val alwaysShownItems: MutableList<TopAppBarActionItem.IconActionItem> =
        items.filterIsInstance<TopAppBarActionItem.IconActionItem.AlwaysShown>().toMutableList()
    val ifRoomItems: MutableList<TopAppBarActionItem.IconActionItem> =
        items.filterIsInstance<TopAppBarActionItem.IconActionItem.ShowIfRoom>().toMutableList()
    val overflowItems = items.filterIsInstance<TopAppBarActionItem.NeverShown>()

    val hasOverflow = overflowItems.isNotEmpty() ||
        (alwaysShownItems.size + ifRoomItems.size - 1) > maxVisibleItems
    val usedSlots = alwaysShownItems.size + (if (hasOverflow) 1 else 0)
    val availableSlots = maxVisibleItems - usedSlots
    if (availableSlots > 0 && ifRoomItems.isNotEmpty()) {
        val visible = ifRoomItems.subList(0, availableSlots.coerceAtMost(ifRoomItems.size))
        alwaysShownItems.addAll(visible)
        ifRoomItems.removeAll(visible)
    }

    return MenuItems(
        alwaysShownItems = alwaysShownItems,
        overflowItems = ifRoomItems + overflowItems,
    )
}
