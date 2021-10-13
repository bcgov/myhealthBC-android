package ca.bc.gov.bchealth.ui.addcard

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddCardOptionsBinding
import ca.bc.gov.bchealth.di.ApiClientModule
import ca.bc.gov.bchealth.http.MustBeQueued
import ca.bc.gov.bchealth.utils.toast
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueITException
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean

/**
 * [AddCardOptionFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddCardOptionFragment : Fragment(R.layout.fragment_add_card_options) {

    private val binding by viewBindings(FragmentAddCardOptionsBinding::bind)

    private val viewModel: AddCardOptionViewModel by viewModels()

    private val _queuePassed = AtomicBoolean(false)

    private val scope = CoroutineScope(Job())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScanQrCode.setOnClickListener {
            findNavController().navigate(R.id.action_addCardOptionFragment_to_onBoardingFragment)
        }

        val action = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            if (it != null)
                viewModel.processUploadedImage(it, requireContext())
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack(R.id.myCardsFragment, false)
            } else {
                showError()
            }
        })

        binding.btnImagePicker.setOnClickListener {
            action.launch("image/*")
        }

        binding.btnGetCard.setOnClickListener {
            scope.launch {

                try {
                    viewModel.getVaccineStatus()
                } catch (e: Exception) {
                    if (e !is MustBeQueued) {
                        e.printStackTrace()
                    }
                    assert(e is MustBeQueued)
                    val handler = Handler(Looper.getMainLooper())
                    handler.post { queueUser((e as MustBeQueued).getValue()) }
                }
            }
        }

        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
            ivBack.setImageResource(R.drawable.ic_acion_back)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_card)
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }


    }

    private fun showError() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.bc_invalid_barcode_title))
            .setCancelable(false)
            .setMessage(getString(R.string.bc_invalid_barcode_upload_message))
            .setPositiveButton(getString(android.R.string.ok)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun queueUser(value: String) {
        try {
            val valueUri = Uri.parse(URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
            val customerId = valueUri.getQueryParameter("c")
            val wrId = valueUri.getQueryParameter("e")
            QueueService.IsTest = false
            val q = QueueITEngine(
                requireActivity(),
                customerId,
                wrId,
                "",
                "",
                object : QueueListener() {
                    override fun onQueuePassed(queuePassedInfo: QueuePassedInfo) {

                        ApiClientModule.queueItToken = queuePassedInfo.queueItToken

                        _queuePassed.set(true)

                        Toast.makeText(
                            requireActivity(),
                            "You passed the queue! You can try again.",
                            Toast.LENGTH_SHORT
                        ).show()


                        scope.launch {
                            try {
                                viewModel.getVaccineStatus()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onQueueViewWillOpen() {
                        Toast.makeText(
                            requireActivity(),
                            "onQueueViewWillOpen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onUserExited() {
                        Toast.makeText(
                            requireActivity(),
                            "onUserExited",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onQueueDisabled() {
                        Toast.makeText(
                            requireActivity(),
                            "The queue is disabled.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onQueueItUnavailable() {
                        Toast.makeText(
                            requireActivity(),
                            "Queue-it is unavailable",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: Error, errorMessage: String) {
                        Toast.makeText(
                            requireActivity(),
                            "Critical error: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onWebViewClosed() {
                        Toast.makeText(
                            requireActivity(),
                            "WebView closed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
            q.run(requireActivity())
        } catch (e: QueueITException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
}
