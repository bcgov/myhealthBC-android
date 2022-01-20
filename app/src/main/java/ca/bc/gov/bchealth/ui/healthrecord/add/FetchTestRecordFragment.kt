package ca.bc.gov.bchealth.ui.healthrecord.add

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchCovidTestResultBinding
import ca.bc.gov.bchealth.ui.custom.setUpDatePickerUi
import ca.bc.gov.bchealth.ui.custom.validateDatePickerData
import ca.bc.gov.bchealth.ui.custom.validatePhnNumber
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class FetchTestRecordFragment : Fragment(R.layout.fragment_fetch_covid_test_result) {
    private val binding by viewBindings(FragmentFetchCovidTestResultBinding::bind)
    private val viewModel: FetchTestRecordsViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle

    companion object {
        private const val TAG = "FetchTestRecordFragment"
        const val TEST_RECORD_ADDED_SUCCESS = "TEST_RECORD_ADDED_SUCCESS"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(TEST_RECORD_ADDED_SUCCESS, -1L)

        setupToolBar()

        setUpPhnUI()

        setUpDobUI()

        setUpDotUI()

        initClickListeners()

        observeCovidTestResult()
    }

    private fun showLoader(value: Boolean) {
        binding.btnSubmit.isEnabled = !value
        binding.progressBar.isVisible = value
    }

    private fun observeCovidTestResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    showLoader(state.onLoading)

                    if (state.isError) {
                        handleError(state)
                    }

                    if (state.onTestResultFetched > 0) {
                        savedStateHandle.set(TEST_RECORD_ADDED_SUCCESS, state.onTestResultFetched)
                        findNavController().popBackStack()
                    }

                    if (state.onMustBeQueued && state.queItUrl != null) {
                        queUser(state.queItUrl)
                    }
                }
            }
        }
    }

    private fun handleError(state: FetchTestRecordUiState) {
        if (state.errorCode == SERVER_ERROR_DATA_MISMATCH) {
            requireContext().showError(
                getString(R.string.error_data_mismatch_title),
                getString(R.string.error_test_result_data_mismatch_message)
            )
        } else {
            requireContext().showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        }
    }

    private fun initClickListeners() {
        binding.btnSubmit.setOnClickListener {

            val phn = binding.edPhn.text.toString()
            val dob = binding.edtDob.text.toString()
            val dot = binding.edtDoc.text.toString()

            if (this.validatePhnNumber(
                    binding.edPhnNumber,
                    getString(R.string.phn_should_be_10_digit)
                ) &&
                this.validateDatePickerData(
                        binding.tipDob,
                        getString(R.string.dob_required)
                    ) &&
                this.validateDatePickerData(
                        binding.tipDot,
                        getString(R.string.dot_required)
                    )
            ) {
                viewModel.fetchTestRecord(phn, dob, dot)
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
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
            tvTitle.text = getString(R.string.add_covid_test_result)

            ivRightOption.visibility = View.VISIBLE
            ivRightOption.setImageResource(R.drawable.ic_help)
            ivRightOption.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_help))
            }
            ivRightOption.contentDescription = getString(R.string.help)

            line1.visibility = View.VISIBLE
        }
    }

    private fun setUpPhnUI() {
        // TODO: 13/01/22 Autocomplete feature needs to be implemented
    }

    private fun setUpDobUI() {
        this.setUpDatePickerUi(binding.tipDob, "DATE_OF_BIRTH")
    }

    private fun setUpDotUI() {
        this.setUpDatePickerUi(binding.tipDot, "DATE_OF_TEST")
    }

    private fun queUser(value: String) {
        try {
            val uri = Uri.parse(URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
            val customerId = uri.getQueryParameter("c")
            val waitingRoomId = uri.getQueryParameter("e")
            QueueService.IsTest = false
            val queueITEngine = QueueITEngine(
                requireActivity(),
                customerId,
                waitingRoomId,
                "",
                "",
                object : QueueListener() {
                    override fun onQueuePassed(queuePassedInfo: QueuePassedInfo?) {
                        Log.d(TAG, "onQueuePassed: updatedToken ${queuePassedInfo?.queueItToken}")
                        viewModel.setQueItToken(queuePassedInfo?.queueItToken)
                    }

                    override fun onQueueViewWillOpen() {
                    }

                    override fun onQueueDisabled() {
                    }

                    override fun onQueueItUnavailable() {
                    }

                    override fun onError(error: Error?, errorMessage: String?) {
                    }
                }
            )
            queueITEngine.run(requireActivity())
        } catch (e: Exception) {
        }
    }
}
