package ca.bc.gov.bchealth.ui.healthpass.add

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchVaccineRecordBinding
import ca.bc.gov.bchealth.utils.viewBindings
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
class FetchVaccineRecordFragment : Fragment(R.layout.fragment_fetch_vaccine_record) {
    private val binding by viewBindings(FragmentFetchVaccineRecordBinding::bind)
    private val viewModel: FetchVaccineRecordViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle

    companion object {
        private const val TAG = "FetchVaccineRecordFragment"
        const val VACCINE_RECORD_ADDED_SUCCESS = "VACCINE_RECORD_ADDED_SUCCESS"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(VACCINE_RECORD_ADDED_SUCCESS, null)

        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_bc_vaccine_record)
            line1.visibility = View.VISIBLE
        }

        binding.btnSubmit.setOnClickListener {
            val phn = binding.edPhn.text.toString()
            val dob = binding.edDob.text.toString()
            val dov = binding.edDov.text.toString()
            when {
                phn.isBlank() -> {
                    binding.edPhnNumber.error = "Invalid PHN"
                    binding.edPhnNumber.requestFocus()
                }
                dob.isBlank() -> {
                    binding.tipDob.error = "Invalid DOB"
                    binding.tipDob.requestFocus()
                }
                dov.isBlank() -> {
                    binding.tipDov.error = "Invalid DOV"
                    binding.tipDov.requestFocus()
                }
                else -> {
                    viewModel.fetchVaccineRecord(phn, dob, dov)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.queItTokenUpdated) {
                        Log.d(TAG, "QueueIt token updated")
                        viewModel.fetchVaccineRecord("9000691304", "1965-01-14", "2021-07-15")
                    }

                    if (uiState.onMustBeQueued && uiState.queItUrl != null) {
                        Log.d(TAG, "Mut be queue, url = ${uiState.queItUrl}")
                        queUser(uiState.queItUrl)
                    }

                    if (uiState.vaccineRecord != null) {
                        savedStateHandle.set(VACCINE_RECORD_ADDED_SUCCESS, uiState.vaccineRecord)
                        findNavController().popBackStack()
                    }
                }
            }
        }
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
                })
        } catch (e: Exception) {

        }
    }
}