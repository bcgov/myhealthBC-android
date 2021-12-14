package ca.bc.gov.bchealth.ui.healthpass.add

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    companion object {
        private const val TAG = "FetchVaccineRecordFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            viewModel.fetchVaccineRecord("9000691304", "1965-01-14", "2021-07-15")
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