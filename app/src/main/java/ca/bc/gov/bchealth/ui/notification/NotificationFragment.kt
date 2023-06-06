package ca.bc.gov.bchealth.ui.notification

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthBackButton
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment(null) {
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationViewModel.getNotifications()
    }

    @Composable
    override fun GetComposableLayout() {
        val uiState = notificationViewModel.uiState.collectAsState().value

        MyHealthTheme {
            Scaffold(
                topBar = { NotificationToolbar() },
                content = {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it)
                            .fillMaxSize(),
                    ) {
                        NotificationScreen(uiState)
                    }
                }
            )
        }
    }

    @Composable
    private fun NotificationToolbar() {
        MyHealthToolBar(
            title = stringResource(id = R.string.notifications),
            navigationIcon = { MyHealthBackButton(::popNavigation) },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash_can),
                        contentDescription = stringResource(id = R.string.notifications_clear),
                        tint = primaryBlue
                    )
                }
            },
            elevation = 4.dp
        )
    }

    @Composable
    @BasePreview
    private fun PreviewNotification() {
        GetComposableLayout()
    }
}
