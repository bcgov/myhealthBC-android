package ca.bc.gov.bchealth.ui.healthrecord.add

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchCovidTestResultBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class FetchTestRecordFragment : Fragment(R.layout.fragment_fetch_covid_test_result) {
    private val binding by viewBindings(FragmentFetchCovidTestResultBinding::bind)
    private val viewModel: FetchTestRecordsViewModel by viewModels()

    companion object {
        private const val TAG = "FetchTestRecordFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            viewModel.fetchTestRecord("9875023209","1955-10-23","2021-11-06")
        }

        binding.btnCancel.setOnClickListener {
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