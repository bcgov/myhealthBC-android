package ca.bc.gov.bchealth.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBcscAuthBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.repository.worker.FetchAuthenticatedHealthRecordsWorker
import ca.bc.gov.repository.worker.FetchAuthenticatedPatientDataWorker
import ca.bc.gov.repository.worker.PATIENT_ID
import ca.bc.gov.repository.worker.WORK_RESULT
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
import java.util.concurrent.TimeUnit

/*
* @auther amit_metri on 04,January,2022
*/
const val BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME = "BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME"
const val BACKGROUND_AUTH_RECORD_FETCH_WORK_INTERVAL = 15L
@AndroidEntryPoint
class BcscAuthFragment : Fragment(R.layout.fragment_bcsc_auth) {

    private val binding by viewBindings(FragmentBcscAuthBinding::bind)
    private val viewModel: BcscAuthViewModel by viewModels()
    private val authResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        processAuthResponse(activityResult)
    }
    private lateinit var workRequest: WorkRequest
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle
            ?.set(BCSC_AUTH_STATUS, BcscAuthState.NO_ACTION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        initUI()
    }

    private fun setupToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.go_to_health_gateway)

            line1.visibility = View.VISIBLE
        }
    }

    private fun initUI() {

        binding.tvLoginInfoMessage.text = Html.fromHtml(
            getString(R.string.login_info_message), Html.FROM_HTML_MODE_LEGACY
        )

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnContinue.setOnClickListener {
            viewModel.initiateLogin()

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.authStatus.collect {

                        showLoader(it.showLoading)
                        handleError(it.isError)

                        if (it.authRequestIntent != null) {
                            authResultLauncher.launch(it.authRequestIntent)
                            viewModel.resetAuthStatus()
                        }

                        if (it.isLoggedIn) {
                            fetchAuthenticatedPatientData()
                        }

                        if (it.queItTokenUpdated) {
                            workManager.enqueue(workRequest)
                        }
                    }
                }
            }
        }
    }

    private fun handleError(isError: Boolean) {
        if (isError) {
            respondToError()
            viewModel.resetAuthStatus()
        }
    }

    private fun fetchAuthenticatedPatientData() {
        workRequest = OneTimeWorkRequestBuilder<FetchAuthenticatedPatientDataWorker>().build()
        workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(workRequest)
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(viewLifecycleOwner) { info ->
                if (info != null && info.state.isFinished) {
                    val queItUrl = info.outputData.getString(WORK_RESULT)
                    if (queItUrl != null) {
                        queUser(queItUrl)
                    }

                    if (info.outputData.getLong(PATIENT_ID, 0) > 0) {
                        respondToSuccess()
                        fetchAuthenticatedRecords(info)
                        showLoader(false)
                    }
                }
            }
    }

    private fun fetchAuthenticatedRecords(info: WorkInfo) {
        val patientId = info.outputData.getLong(PATIENT_ID, 0)
        val data: Data = workDataOf(PATIENT_ID to patientId)
        val workRequest = PeriodicWorkRequestBuilder<FetchAuthenticatedHealthRecordsWorker>(BACKGROUND_AUTH_RECORD_FETCH_WORK_INTERVAL, TimeUnit.MINUTES)
            .setInputData(data)
            .build()
        workManager = WorkManager.getInstance(requireContext())
        workManager.enqueueUniquePeriodicWork(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest)
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
            viewModel.processAuthResponse(data)
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
