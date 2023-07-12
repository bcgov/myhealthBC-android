package ca.bc.gov.bchealth.ui.home.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.compose.theme.statusBlue
import ca.bc.gov.bchealth.compose.theme.white
import ca.bc.gov.bchealth.ui.home.QuickAccessTileItem

@Composable
fun QuickAccessManagementScreen(
    viewModel: QuickAccessManagementViewModel,
    onClickItem: (QuickAccessTileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadUiList()
    }

    QuickAccessManagementContent(uiState, onClickItem, modifier)
}

@Composable
private fun QuickAccessManagementContent(
    uiState: Map<Int, List<QuickAccessTileItem>>,
    onClickItem: (QuickAccessTileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 20.dp, bottom = 64.dp),
        content = {
            item {
                Text(
                    text = stringResource(id = R.string.quick_access_management_body),
                    style = MyHealthTypography.body1,
                    color = primaryBlue
                )
            }

            item { Spacer(modifier = Modifier.size(16.dp)) }

            for ((category, tiles) in uiState) {
                item {
                    Text(
                        text = stringResource(id = category),
                        style = MyHealthTypography.body1.bold(),
                        color = statusBlue
                    )
                }

                item { Spacer(modifier = Modifier.size(12.dp)) }

                items(tiles) { tile ->
                    TileItemUi(tile, onClickItem)
                    Spacer(modifier = Modifier.size(10.dp))
                }

                item { Spacer(modifier = Modifier.size(6.dp)) }
            }
        }
    )
}

@Composable
private fun TileItemUi(
    item: QuickAccessTileItem,
    onClickItem: (QuickAccessTileItem) -> Unit,
) {
    val checkedState = remember { mutableStateOf(item.enabled) }

    Card(
        modifier = Modifier.clickable {
            checkedState.value = checkedState.value.not()
            onClickItem.invoke(item)
        },
        shape = RoundedCornerShape(4.dp),
        backgroundColor = white,
        elevation = 5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 20.dp, top = 13.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TileName(item)

            Checkbox(
                modifier = Modifier.size(24.dp),
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    onClickItem.invoke(item)
                }
            )
        }
    }
}

@Composable
private fun RowScope.TileName(item: QuickAccessTileItem) {
    val name = when (item) {
        is QuickAccessTileItem.PredefinedItem -> stringResource(id = item.nameId)
        is QuickAccessTileItem.DynamicItem -> item.text
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        text = name,
        style = MyHealthTypography.body1.bold(),
    )
}

// @BasePreview
// @Composable
// private fun PreviewQuickAccessManagementContent() {
//     MyHealthTheme {
//         QuickAccessManagementContent(
//             listOf(
//                 QuickAccessManagementViewModel.QuickAccessManagementList(
//                     "Health record",
//                     listOf(
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "My Notes",
//                             false
//                         ),
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "Immunization",
//                             true
//                         ),
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "Medications",
//                             false
//                         ),
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "Lab Results",
//                             false
//                         ),
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "Special authority",
//                             false
//                         ),
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "Health visit",
//                             false
//                         ),
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "Clinic documents",
//                             false
//                         ),
//                     )
//                 ),
//                 QuickAccessManagementViewModel.QuickAccessManagementList(
//                     "Service",
//                     listOf(
//                         QuickAccessManagementViewModel.QuickAccessManagementItem(
//                             "Organ donor",
//                             false
//                         ),
//                     )
//                 ),
//                 QuickAccessManagementViewModel.QuickAccessManagementList(
//                     "Dependents’ records",
//                     listOf(
//                         QuickAccessManagementViewModel.QuickAccessManagementItem("Jane", false),
//                         QuickAccessManagementViewModel.QuickAccessManagementItem("Anne", false),
//                     )
//                 )
//             ),
//             {}
//         )
//     }
// }
