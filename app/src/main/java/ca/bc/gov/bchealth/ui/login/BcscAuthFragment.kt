package ca.bc.gov.bchealth.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBcscAuthBinding
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.healthrecord.filter.PatientFilterViewModel
import ca.bc.gov.bchealth.ui.tos.TermsOfServiceFragment
import ca.bc.gov.bchealth.ui.tos.TermsOfServiceStatus
import ca.bc.gov.bchealth.ui.tos.TermsOfServiceViewModel
import ca.bc.gov.bchealth.utils.observeCurrentBackStackForAction
import ca.bc.gov.bchealth.utils.removeActionFromCurrentBackStackEntry
import ca.bc.gov.bchealth.utils.setActionToCurrentBackStackEntry
import ca.bc.gov.bchealth.utils.setActionToPreviousBackStackEntry
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.repository.bcsc.PostLoginCheck
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* @auther amit_metri on 04,January,2022
*/
@AndroidEntryPoint
class BcscAuthFragment : BaseSecureFragment(R.layout.fragment_bcsc_auth) {

    private val binding by viewBindings(FragmentBcscAuthBinding::bind)
    private val viewModel: BcscAuthViewModel by viewModels()
    private val filterSharedViewModel: PatientFilterViewModel by activityViewModels()
    private val termsOfServiceViewModel: TermsOfServiceViewModel by activityViewModels()
    private val authResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        processAuthResponse(activityResult)
    }
    private var logoutResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            viewModel.processLogoutResponse(requireContext())
        } else {
            respondToError()
        }
    }

    companion object {
        private const val TOS_STATUS = "TOS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().setActionToPreviousBackStackEntry(BCSC_AUTH, BcscAuthState.NO_ACTION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCurrentBackStackForAction<TermsOfServiceStatus>(TermsOfServiceFragment.TERMS_OF_SERVICE_STATUS) {
            findNavController().removeActionFromCurrentBackStackEntry<TermsOfServiceStatus>(
                TermsOfServiceFragment.TERMS_OF_SERVICE_STATUS
            )
            when (it) {
                TermsOfServiceStatus.ACCEPTED -> {
                    if (termsOfServiceViewModel.tosUiState.value.termsOfServiceId != null) {
                        observeCurrentBackStackForAction<TOSStatus>(TOS_STATUS) { tos ->
                            findNavController().removeActionFromCurrentBackStackEntry<TOSStatus>(TOS_STATUS)
                            viewModel.acceptTermsAndService(termsOfServiceViewModel.tosUiState.value.termsOfServiceId!!, tos == TOSStatus.UPDATED)
                        }
                    } else {
                        respondToError()
                    }
                }

                else -> {
                    showTosNotAcceptedDialog()
                }
            }
        }
        initUI()
        observeAuthentication()
    }

    private fun observeAuthentication() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authStatus.collect {

                    showLoader(it.showLoading)
                    handleError(it.isError)

                    handleLoadVerification(it)

                    if (it.authRequestIntent != null) {
                        authResultLauncher.launch(it.authRequestIntent)
                        viewModel.resetAuthStatus()
                    }

                    val isLoginStatusActive = it.loginStatus == LoginStatus.ACTIVE
                    if (isLoginStatusActive) {
                        viewModel.resetAuthStatus()
                        viewModel.checkAgeLimit()
                        // reset filter
                        filterSharedViewModel.clearFilter()
                    }

                    handleAgeLimitCheck(it)

                    handleTosCheck(it)

                    handleEndSessionRequest(it)

                    if (!it.isConnected) {
                        viewModel.resetAuthStatus()
                        binding.root.showNoInternetConnectionMessage(requireContext())
                    }
                }
            }
        }
    }

    private fun handleEndSessionRequest(authStatus: AuthStatus) {
        if (authStatus.endSessionIntent != null) {
            logoutResultLauncher.launch(authStatus.endSessionIntent)
            viewModel.resetAuthStatus()
        }
    }

    private fun handleLoadVerification(authStatus: AuthStatus) {
        if (authStatus.canInitiateBcscLogin != null) {
            if (authStatus.canInitiateBcscLogin) {
                viewModel.initiateLogin()
            } else {
                binding.root.showServiceDownMessage(requireContext())
            }
            viewModel.resetAuthStatus()
        }
    }

    private fun handleAgeLimitCheck(authStatus: AuthStatus) {
        when (authStatus.ageLimitCheck) {
            AgeLimitCheck.PASSED -> {
                viewModel.resetAuthStatus()
                viewModel.checkTermsOfServiceStatus()
            }

            AgeLimitCheck.FAILED -> {
                showAgeLimitRestrictionDialog()
                viewModel.resetAuthStatus()
            }

            null -> return
        }
    }

    private fun handleTosCheck(authStatus: AuthStatus) {

        if (authStatus.tosStatus != null) {
            viewModel.resetAuthStatus()
            when (authStatus.tosStatus) {
                TOSStatus.ACCEPTED -> {
                    respondToSuccess()
                    viewModel.executeOneTimeDataFetch()
                }

                else -> {
                    findNavController().setActionToCurrentBackStackEntry(TOS_STATUS, authStatus.tosStatus)
                    findNavController().navigate(R.id.termsOfServiceFragment)
                }
            }
        }
    }

    private fun setupToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.bc_services_card)

            line1.visibility = View.VISIBLE
        }
    }

    private fun initUI() {

        setupToolBar()

        binding.tvLoginInfoMessage.text =
            getString(R.string.login_info_message)

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnContinue.setOnClickListener {
            viewModel.verifyLoad()
        }
    }

    private fun handleError(isError: Boolean) {
        if (isError) {
            respondToError()
            viewModel.resetAuthStatus()
        }
    }

    private fun respondToSuccess() {
        viewModel.setPostLoginCheck(PostLoginCheck.COMPLETE)
        findNavController().setActionToPreviousBackStackEntry(BCSC_AUTH, BcscAuthState.SUCCESS)
        findNavController().popBackStack()
    }

    private fun showAgeLimitRestrictionDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.age_limit_title))
            .setCancelable(false)
            .setMessage(getString(R.string.age_limit_message))
            .setPositiveButton(getString(R.string.ok_camel_case)) { dialog, _ ->
                viewModel.getEndSessionIntent()
                dialog.dismiss()
            }
            .show()
    }

    private fun showTosNotAcceptedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.terms_of_service))
            .setCancelable(false)
            .setMessage(getString(R.string.terms_of_service_message))
            .setPositiveButton(getString(R.string.ok_camel_case)) { dialog, _ ->
                viewModel.getEndSessionIntent()
                dialog.dismiss()
            }
            .show()
    }

    private fun respondToError() {
        findNavController().navigate(R.id.action_bcscAuthFragment_to_bcscAuthErrorFragment)
    }

    private fun showLoader(value: Boolean) {
        binding.btnContinue.isEnabled = !value
        binding.progressBar.isVisible = value
    }

    /*
    * App Auth: Process Login response
    * */
    private fun processAuthResponse(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data: Intent? = activityResult.data
            viewModel.processAuthResponse(data, requireContext())
        } else {
            respondToError()
        }
    }
}
