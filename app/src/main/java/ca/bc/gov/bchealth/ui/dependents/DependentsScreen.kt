package ca.bc.gov.bchealth.ui.dependents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.HGButton
import ca.bc.gov.bchealth.compose.component.HGCircularProgressIndicator
import ca.bc.gov.bchealth.compose.component.HGOutlinedButton
import ca.bc.gov.bchealth.compose.component.HGTextButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.grey
import ca.bc.gov.bchealth.ui.login.AuthStatus
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME

/**
 * @author pinakin.kansara
 * Created 2023-10-10 at 1:09 p.m.
 */
private const val TITLE_ID = "title_id"
private const val DESCRIPTION_ID = "description_id"
private const val DIVIDER_ID = "divider_id"
private const val EMPTY_VIEW_ID = "empty_view_id"
private const val DEPENDENT_LIST_ID = "dependent_list_id"
private const val BTN_ADD_DEPENDENT_ID = "btn_add_dependent_id"
private const val BTN_MANAGE_DEPENDENTS_ID = "btn_manage_dependents_id"

@Composable
fun DependentsScreen(
    onRequireAuthentication: (LoginStatus) -> Unit,
    onAddDependentClick: () -> Unit,
    onManageDependentClick: () -> Unit,
    onDependentClick: (DependentDetailItem) -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: BcscAuthViewModel,
    viewModel: DependentsViewModel
) {
    val authState: AuthStatus by authViewModel.authStatus.collectAsStateWithLifecycle()
    val uiState: DependentsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dependentsList by viewModel.dependentsList.collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(key1 = Unit) {
        authViewModel.checkSession()
    }

    authState.loginStatus?.let { loginStatus ->
        when (loginStatus) {
            LoginStatus.ACTIVE -> {
            }

            LoginStatus.EXPIRED,
            LoginStatus.NOT_AUTHENTICATED -> {
                LaunchedEffect(key1 = loginStatus) {
                    authViewModel.resetAuthStatus()
                    onRequireAuthentication(loginStatus)
                }
            }
        }
    }

    val context = LocalContext.current
    val workRequest = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        .observeAsState()
    val workState = workRequest.value?.firstOrNull()?.state

    if (workState != null && workState.isFinished) {
        LaunchedEffect(key1 = Unit) {
            viewModel.hideLoadingState()
        }
    } else {
        LaunchedEffect(key1 = Unit) {
            viewModel.displayLoadingState()
        }
    }

    DependentsScreenContent(
        onAddDependentClick = onAddDependentClick,
        onManageDependentClick = onManageDependentClick,
        onDependentClick = onDependentClick,
        onRemoveDependentClick = { dependent ->
            viewModel.removeDependent(dependent.patientId)
        },
        modifier,
        uiState,
        dependentsList
    )
}

@Composable
private fun DependentsScreenContent(
    onAddDependentClick: () -> Unit,
    onManageDependentClick: () -> Unit,
    onDependentClick: (DependentDetailItem) -> Unit,
    onRemoveDependentClick: (DependentDetailItem) -> Unit,
    modifier: Modifier = Modifier,
    uiState: DependentsUiState,
    dependentsList: List<DependentDetailItem>
) {

    val dependent = remember { mutableStateOf<DependentDetailItem?>(null) }

    dependent.value?.let {
        AlertDialog(
            onDismissRequest = {
                dependent.value = null
            },
            title = {
                Text(text = stringResource(id = R.string.dependents_management_remove_title))
            },
            text = {
                Text(stringResource(id = R.string.dependents_management_remove_body, it.firstName))
            },
            confirmButton = {
                HGTextButton(
                    onClick = {
                        onRemoveDependentClick(it)
                        dependent.value = null
                    }
                ) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                HGTextButton(
                    onClick = {
                        dependent.value = null
                    }
                ) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {

        if (uiState.onLoading) {
            HGCircularProgressIndicator()
        } else {
            BoxWithConstraints() {
                ConstraintLayout(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxSize(),
                    constraintSet = dependentsScreenConstraints(dependentsList.isNotEmpty())
                ) {
                    Text(
                        modifier = Modifier.layoutId(TITLE_ID),
                        text = stringResource(id = R.string.dependent),
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.layoutId(DESCRIPTION_ID),
                        text = stringResource(id = R.string.dependents_body),
                        style = MaterialTheme.typography.body1,
                        color = grey
                    )

                    AnimatedVisibility(
                        modifier = Modifier.layoutId(DIVIDER_ID),
                        visible = !dependentsList.isNullOrEmpty()
                    ) {
                        Divider(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth(),
                            thickness = 2.dp
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.layoutId(DEPENDENT_LIST_ID),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(dependentsList) { dependents ->
                            if (dependents.agedOut) {
                                DependentAgedOutItemUI(onRemoveDependentClick = {
                                    dependent.value = dependents
                                }, title = dependents.fullName)
                            } else {
                                DependentItemUI(
                                    modifier = Modifier.clickable {
                                        onDependentClick(
                                            dependents
                                        )
                                    },
                                    title = dependents.fullName, canUnlink = false
                                )
                            }
                        }
                    }

                    Image(
                        modifier = Modifier.layoutId(EMPTY_VIEW_ID),
                        painter = painterResource(id = R.drawable.img_dependents_empty),
                        contentDescription = ""
                    )

                    HGButton(
                        modifier = Modifier.layoutId(BTN_ADD_DEPENDENT_ID),
                        onClick = onAddDependentClick,
                        text = stringResource(id = R.string.dependents_add_dependent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_dependent),
                            contentDescription = ""
                        )
                    }

                    HGOutlinedButton(
                        modifier = Modifier.layoutId(BTN_MANAGE_DEPENDENTS_ID),
                        onClick = onManageDependentClick,
                        text = stringResource(id = R.string.dependents_manage_dependent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_manage_dependent),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun confirmDependentDeletion(dependent: DependentDetailItem) {
}

private fun dependentsScreenConstraints(hasDependent: Boolean): ConstraintSet {
    return ConstraintSet {
        val titleId = createRefFor(TITLE_ID)
        val descriptionId = createRefFor(DESCRIPTION_ID)
        val dividerId = createRefFor(DIVIDER_ID)
        val dependentListId = createRefFor(DEPENDENT_LIST_ID)
        val btnAddDependentsId = createRefFor(BTN_ADD_DEPENDENT_ID)
        val btnManageDependentsId = createRefFor(BTN_MANAGE_DEPENDENTS_ID)
        val emptyViewId = createRefFor(EMPTY_VIEW_ID)

        constrain(titleId) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
        }

        constrain(descriptionId) {
            start.linkTo(parent.start)
            top.linkTo(titleId.bottom, 16.dp)
        }

        constrain(dividerId) {
            start.linkTo(parent.start)
            top.linkTo(descriptionId.bottom, 16.dp)
            visibility = if (hasDependent) {
                Visibility.Visible
            } else {
                Visibility.Invisible
            }
        }

        constrain(dependentListId) {
            start.linkTo(parent.start)
            top.linkTo(dividerId.bottom)
            end.linkTo(parent.end)
            bottom.linkTo(btnAddDependentsId.top)
            height = Dimension.fillToConstraints
            visibility = if (hasDependent) {
                Visibility.Visible
            } else {
                Visibility.Gone
            }
        }

        constrain(emptyViewId) {
            start.linkTo(parent.start)
            top.linkTo(dividerId.bottom)
            end.linkTo(parent.end)
            bottom.linkTo(btnAddDependentsId.top)
            visibility = if (hasDependent) {
                Visibility.Gone
            } else {
                Visibility.Visible
            }
        }

        constrain(btnAddDependentsId) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(btnManageDependentsId.top, 16.dp, 16.dp)
            width = Dimension.fillToConstraints
        }

        constrain(btnManageDependentsId) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            visibility = if (hasDependent) {
                Visibility.Visible
            } else {
                Visibility.Gone
            }
        }
    }
}

@BasePreview
@Composable
private fun DependentsScreenPreview() {
    HealthGatewayTheme {
    }
}
