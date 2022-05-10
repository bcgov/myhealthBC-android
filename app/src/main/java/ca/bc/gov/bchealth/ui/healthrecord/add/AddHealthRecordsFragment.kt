package ca.bc.gov.bchealth.ui.healthrecord.add

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddHealthRecordsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment
import ca.bc.gov.bchealth.ui.healthrecord.NavigationAction
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddHealthRecordsFragment : BaseFragment(R.layout.fragment_add_health_records) {

    private val binding by viewBindings(FragmentAddHealthRecordsBinding::bind)
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

        binding.btnLogin.setOnClickListener {
            sharedViewModel.destinationId = 0
            findNavController().navigate(R.id.bcscAuthInfoFragment)
        }

        observeNavigationFlow()
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
                        findNavController().navigate(R.id.profileFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
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
}
