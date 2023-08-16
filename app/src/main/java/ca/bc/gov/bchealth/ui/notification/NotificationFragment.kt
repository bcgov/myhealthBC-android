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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.ui.auth.BCServicesCardSessionContent
import ca.bc.gov.bchealth.ui.custom.MyHealthBackButton
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.healthrecord.filter.PatientFilterViewModel
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.common.model.notification.NotificationActionTypeDto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment(null) {
    private val viewModel: NotificationViewModel by viewModels()
    private val filterSharedViewModel: PatientFilterViewModel by activityViewModels()

    override fun getBaseViewModel(): BaseViewModel = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshNotifications()
    }

    @Composable
    override fun GetComposableLayout() {
        val uiState = viewModel.uiState.collectAsState().value

        MyHealthTheme {
            Scaffold(
                topBar = { NotificationToolbar(uiState) },
                content = {
                    if (uiState.sessionExpired) {
                        BCServicesCardSessionContent(
                            modifier = Modifier.padding(top = 16.dp),
                            title = stringResource(id = R.string.notifications),
                            sessionMessage = stringResource(id = R.string.notifications_session_expired)
                        ) {
                            findNavController().navigate(R.id.bcscAuthInfoFragment)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .statusBarsPadding()
                                .navigationBarsPadding()
                                .padding(it)
                                .fillMaxSize(),
                        ) {
                            NotificationScreen(
                                uiState,
                                viewModel::deleteNotification,
                                ::onClickNotification,
                                viewModel::refreshNotifications
                            )
                        }
                    }
                }
            )
        }

        if (uiState.dismissError) {
            viewModel.resetErrorState()
            showGenericError()
        }
    }

    private fun showDeletionConfirmationDialog() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.notifications_clear),
            msg = getString(R.string.notifications_clear_confirmation_body),
            positiveBtnMsg = getString(R.string.yes),
            negativeBtnMsg = getString(R.string.cancel),
            positiveBtnCallback = viewModel::deleteNotifications,
            cancelable = true
        )
    }

    @Composable
    private fun NotificationToolbar(uiState: NotificationViewModel.NotificationsUIState) {
        MyHealthToolBar(
            title = if (uiState.sessionExpired) "" else stringResource(id = R.string.notifications),
            navigationIcon = { MyHealthBackButton({ findNavController().popBackStack() }) },
            actions = {
                IconButton(onClick = {
                    if (isDeleteIconEnabled(uiState)) {
                        showDeletionConfirmationDialog()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash_can),
                        contentDescription = stringResource(id = R.string.notifications_clear),
                        tint = primaryBlue.copy(alpha = if (isDeleteIconEnabled(uiState)) 1.0f else 0.2f)
                    )
                }
            },
            elevation = 4.dp
        )
    }

    private fun onClickNotification(notificationItem: NotificationViewModel.NotificationItem) {
        when (notificationItem.actionType) {
            NotificationActionTypeDto.EXTERNAL -> context?.redirect(notificationItem.actionUrl)
            NotificationActionTypeDto.INTERNAL -> goToRecords(notificationItem.category)

            NotificationActionTypeDto.NONE -> {}
        }
    }

    private fun goToRecords(category: String) {
        filterSharedViewModel.updateFilter(
            listOf(TimelineTypeFilter.findByFilterValue(category).name),
        )

        findNavController().navigate(R.id.health_records)
    }

    private fun isDeleteIconEnabled(uiState: NotificationViewModel.NotificationsUIState) =
        uiState.loading.not() && uiState.sessionExpired.not() && uiState.list.isEmpty().not()

    @Composable
    @BasePreview
    private fun PreviewNotification() {
        GetComposableLayout()
    }
}
