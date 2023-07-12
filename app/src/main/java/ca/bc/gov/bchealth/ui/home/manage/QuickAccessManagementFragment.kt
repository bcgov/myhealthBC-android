package ca.bc.gov.bchealth.ui.home.manage

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthBackButton
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import ca.bc.gov.bchealth.ui.home.QuickAccessTileItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickAccessManagementFragment : BaseFragment(null) {
    private val viewModel: QuickAccessManagementViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        MyHealthTheme {
            Scaffold(
                topBar = {
                    MyHealthToolBar(
                        title = stringResource(id = R.string.quick_access_management_title),
                        navigationIcon = { MyHealthBackButton(::popNavigation) },
                        actions = {
                            IconButton(onClick = viewModel::saveSelection) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check),
                                    contentDescription = stringResource(id = R.string.quick_access_management_save),
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                    )
                },
                content = {
                    QuickAccessManagementScreen(
                        viewModel = viewModel,
                        onClickItem = ::onClickItem,
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it)
                    )
                },
            )
        }
    }

    private fun onClickItem(item: QuickAccessTileItem) {
        viewModel.toggleItem(item)
    }
}
