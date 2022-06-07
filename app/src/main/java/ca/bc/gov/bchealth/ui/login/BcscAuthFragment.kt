package ca.bc.gov.bchealth.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBcscAuthBinding
import ca.bc.gov.bchealth.ui.healthrecord.filter.FilterViewModel
import ca.bc.gov.bchealth.ui.healthrecord.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.tos.TermsOfServiceFragment
import ca.bc.gov.bchealth.ui.tos.TermsOfServiceStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.repository.bcsc.PostLoginCheck
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/*
* @auther amit_metri on 04,January,2022
*/
@AndroidEntryPoint
class BcscAuthFragment : Fragment(R.layout.fragment_bcsc_auth) {

    private val binding by viewBindings(FragmentBcscAuthBinding::bind)
    private val viewModel: BcscAuthViewModel by viewModels()
    private val filterSharedViewModel: FilterViewModel by activityViewModels()
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
            AlertDialogHelper.showAlertDialog(
                context = requireContext(),
                title = getString(R.string.error),
                msg = getString(R.string.error_message),
                positiveBtnMsg = getString(R.string.dialog_button_ok)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle
            ?.set(BCSC_AUTH_STATUS, BcscAuthState.NO_ACTION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<TermsOfServiceStatus>(
            TermsOfServiceFragment.TERMS_OF_SERVICE_STATUS
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<TermsOfServiceStatus>(
                TermsOfServiceFragment.TERMS_OF_SERVICE_STATUS
            )
            when (it) {
                TermsOfServiceStatus.ACCEPTED -> {
                    viewModel.acceptTermsAndService()
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
                        filterSharedViewModel.updateFilter(listOf(TimelineTypeFilter.ALL.name), null, null)
                    }

                    handleQueueIt(it)

                    handleAgeLimitCheck(it)

                    handleTosCheck(it)

                    handleEndSessionRequest(it)
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
                viewModel.isTermsOfServiceAccepted()
            }
            AgeLimitCheck.FAILED -> {
                showAgeLimitRestrictionDialog()
                viewModel.resetAuthStatus()
            }
        }
    }

    private fun handleTosCheck(authStatus: AuthStatus) {

        if (authStatus.tosAccepted != null) {
            viewModel.resetAuthStatus()
            when (authStatus.tosAccepted) {
                TOSAccepted.ACCEPTED -> {
                    respondToSuccess()
                    viewModel.executeOneTimeDataFetch()
                }
                else -> {
                    findNavController().navigate(R.id.termsOfServiceFragment)
                }
            }
        }
    }

    private fun handleQueueIt(authStatus: AuthStatus) {
        if (authStatus.onMustBeQueued && authStatus.queItUrl != null) {
            viewModel.resetAuthStatus()
            queUser(authStatus.queItUrl)
        }

        if (authStatus.queItTokenUpdated) {
            viewModel.resetAuthStatus()
            viewModel.checkAgeLimit() // Queueit exception is thrown for age limit check API
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

    private fun queUser(value: String) {
        try {
            val uri = Uri.parse(URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
            val customerId = uri.getQueryParameter("c")
            val waitingRoomId = uri.getQueryParameter("e")
            QueueService.IsTest = false
            val queueITEngine =
                QueueITEngine(requireActivity(), customerId, waitingRoomId, "", "", queueListener)
            queueITEngine.run(requireActivity())
        } catch (e: Exception) {
            Log.i(this::class.java.name, "Exception in queUser: ${e.message}")
        }
    }

    private val queueListener = object : QueueListener() {
        override fun onQueuePassed(queuePassedInfo: QueuePassedInfo?) {
            viewModel.setQueItToken(queuePassedInfo?.queueItToken)
        }

        override fun onQueueDisabled() {
            // Do nothing
        }

        override fun onQueueViewWillOpen() {
            // Do nothing
        }

        override fun onError(error: Error?, errorMessage: String?) {
            // Do nothing
        }

        override fun onQueueItUnavailable() {
            // Do nothing
        }
    }

    private fun respondToSuccess() {
        viewModel.setPostLoginCheck(PostLoginCheck.COMPLETE)
        showLoginSuccessDialog()
    }

    private fun showLoginSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.login_success_title))
            .setCancelable(false)
            .setMessage(getString(R.string.login_success_message))
            .setPositiveButton(getString(R.string.ok_camel_case)) { dialog, _ ->
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(BCSC_AUTH_STATUS, BcscAuthState.SUCCESS)
                findNavController().popBackStack()
                dialog.dismiss()
            }
            .show()
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
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error),
            msg = getString(R.string.error_message),
            positiveBtnMsg = getString(R.string.dialog_button_ok)
        )
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

    companion object {
        const val BCSC_AUTH_STATUS = "BCSC_AUTH_SUCCESS"
    }
}

enum class BcscAuthState {
    SUCCESS,
    NO_ACTION,
    NOT_NOW
}
