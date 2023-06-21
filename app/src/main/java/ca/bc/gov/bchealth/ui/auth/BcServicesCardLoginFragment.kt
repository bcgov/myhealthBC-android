package ca.bc.gov.bchealth.ui.auth

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import ca.bc.gov.bchealth.utils.setActionToPreviousBackStackEntry
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 * [BcServicesCardLoginFragment] is representing state when the user is not logged in.
 * This screen is designed with compose & in future this might require some modification
 * that makes it a reusable screen to handle logout state.
 *
 * Following places in the code require to be updated in future
 * - [ca.bc.gov.bchealth.ui.dependents.DependentsFragment]
 */
@AndroidEntryPoint
class BcServicesCardLoginFragment : BaseSecureFragment(null) {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val bcServiceCardLoginViewModel: BcServiceCardLoginViewModel by viewModels()
    private val args: BcServicesCardLoginFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().setActionToPreviousBackStackEntry(
                        NAVIGATION_ACTION,
                        NavigationAction.ACTION_BACK
                    )
                    findNavController().popBackStack()
                }
            }
        )
    }

    override fun handleBCSCAuthState(bcscAuthState: BcscAuthState) {
        when (bcscAuthState) {
            BcscAuthState.SUCCESS -> {
                findNavController().setActionToPreviousBackStackEntry(
                    NAVIGATION_ACTION,
                    NavigationAction.ACTION_RE_CHECK
                )
                findNavController().popBackStack()
            }

            BcscAuthState.NOT_NOW,
            BcscAuthState.NO_ACTION -> {
                // no implementation required
            }
        }
    }

    override fun handleNavigationAction(navigationAction: NavigationAction) {
        when (navigationAction) {
            NavigationAction.ACTION_BACK -> {
                findNavController().setActionToPreviousBackStackEntry(
                    NAVIGATION_ACTION,
                    NavigationAction.ACTION_BACK
                )
                findNavController().popBackStack()
            }

            NavigationAction.ACTION_RE_CHECK -> {
                findNavController().setActionToPreviousBackStackEntry(
                    NAVIGATION_ACTION,
                    NavigationAction.ACTION_RE_CHECK
                )
                findNavController().popBackStack()
            }
        }
    }

    @Composable
    override fun GetComposableLayout() {
        MyHealthTheme {
            Scaffold(
                topBar = {
                    MyHealthToolBar(
                        title = "",
                        actions = {
                            IconButton(onClick = { findNavController().navigate(R.id.settingsFragment) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_settings),
                                    contentDescription = stringResource(
                                        id = R.string.settings
                                    ),
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                    )
                },
                content = {
                    BcServicesCardLoginScreen(
                        modifier = Modifier.padding(it),
                        loginStateInfo = bcServiceCardLoginViewModel.getLoginStateInfo(args.source)
                    ) {
                        sharedViewModel.destinationId = 0
                        findNavController().navigate(R.id.bcscAuthInfoFragment)
                    }
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }
}
