package ca.bc.gov.bchealth.ui.auth

import android.os.Bundle
import android.view.View
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
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment
import ca.bc.gov.bchealth.ui.healthrecord.NavigationAction
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 * [BcServiceCardSessionFragment] is representing state when the auth token is expired.
 * This screen is designed with compose & in future this might require some modification
 * that makes it a reusable screen to handle session expiry state in the app.
 *
 * Following places in the code require to be updated in future
 * - [ca.bc.gov.bchealth.ui.healthrecord.individual.IndividualHealthRecordFragment]
 * - [ca.bc.gov.bchealth.ui.dependents.DependentsFragment]
 */
@AndroidEntryPoint
class BcServiceCardSessionFragment : BaseFragment(null) {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_BACK
                        )
                    findNavController().popBackStack()
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BcscAuthState>(
            BcscAuthFragment.BCSC_AUTH_STATUS
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.remove<BcscAuthState>(BcscAuthFragment.BCSC_AUTH_STATUS)
            it?.let {
                when (it) {
                    BcscAuthState.SUCCESS -> {
                        findNavController().previousBackStackEntry?.savedStateHandle
                            ?.set(
                                HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
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
        }
        observeNavigationFlow()
    }

    private fun observeNavigationFlow() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<NavigationAction>(
            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<NavigationAction>(
                HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION
            )
            it?.let {
                when (it) {
                    NavigationAction.ACTION_BACK -> {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_BACK
                        )
                        findNavController().popBackStack()
                    }
                    NavigationAction.ACTION_RE_CHECK -> {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_RE_CHECK
                        )
                        findNavController().popBackStack()
                    }
                }
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
                    BCServicesCardSessionScreen(modifier = Modifier.padding(it)) {
                        sharedViewModel.destinationId = 0
                        findNavController().navigate(R.id.bcscAuthInfoFragment)
                    }
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }
}
