package ca.bc.gov.bchealth.ui.healthrecord.add

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddHealthRecordsBinding
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.utils.setActionToPreviousBackStackEntry
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddHealthRecordsFragment : BaseSecureFragment(R.layout.fragment_add_health_records) {

    private val binding by viewBindings(FragmentAddHealthRecordsBinding::bind)
    private val sharedViewModel: SharedViewModel by activityViewModels()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            sharedViewModel.destinationId = 0
            findNavController().navigate(R.id.bcscAuthInfoFragment)
        }
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

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            with(binding.layoutToolbar.appbar) {
                stateListAnimator = null
                elevation = 0f
            }
            inflateMenu(R.menu.settings_menu)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(R.id.settingsFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }
}
