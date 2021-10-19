package ca.bc.gov.bchealth.ui.addcard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchVaccineCardBinding
import ca.bc.gov.bchealth.di.ApiClientModule
import ca.bc.gov.bchealth.http.MustBeQueued
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.toast
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueITException
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FetchVaccineCardFragment : Fragment(R.layout.fragment_fetch_vaccine_card) {

    private val binding by viewBindings(FragmentFetchVaccineCardBinding::bind)

    private val simpleDateFormat = SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH)

    private val _queuePassed = AtomicBoolean(false)

    private val parentJob = CoroutineScope(IO)

    private val parentJob1 = CoroutineScope(IO)

    private val viewModel: FetchVaccineCardViewModel by viewModels()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        print(exception.printStackTrace())

        if (exception !is MustBeQueued) {
            exception.printStackTrace()
        }
        assert(exception is MustBeQueued)
        val handler = Handler(Looper.getMainLooper())
        handler.post { queueUser((exception as MustBeQueued).getValue()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        iniUI()
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
            ivBack.setImageResource(R.drawable.ic_acion_back)
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_bc_vaccine_card)

            ivSettings.visibility = View.VISIBLE
            ivSettings.setImageResource(R.drawable.ic_help)
            ivSettings.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun iniUI() {

        if (BuildConfig.DEBUG) {
            binding.edPhnNumber.editText?.setText("9000201422")
            binding.edDob.editText?.setText("1989-12-12")
            binding.edDov.editText?.setText("2021-05-15")
        }

        setUpDobUI()

        setUpDovUI()

        val content = SpannableString(getString(R.string.privacy_statement_add_card))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.tvPrivacyStatement.text = content
        binding.tvPrivacyStatement.setOnClickListener {
            redirect(getString(R.string.url_privacy_policy))
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            if (validateInputData()) {
                parentJob.launch(exceptionHandler) {
                    viewModel.getVaccineStatus(
                        binding.edPhnNumber.editText?.text.toString(),
                        binding.edDob.editText?.text.toString(),
                        binding.edDov.editText?.text.toString()
                    )
                }
            }
        }

        viewModel.vaxStatusResponseLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is Response.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    it.data?.resourcePayload?.qrCode?.data?.removePrefix("/")
                        ?.let { it1 ->
                            viewModel.processShcUri(
                                it1
                            )
                        }
                }
                is Response.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        })

        viewModel.uploadStatus.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack(R.id.myCardsFragment, false)
            } else {
                showError()
            }
        })
    }

    private fun validateInputData(): Boolean {
        if (binding.edPhnNumber.editText?.text.isNullOrEmpty()) {
            binding.edPhnNumber.editText?.doOnTextChanged { text, start, before, count ->
                if (count > 5) {
                    binding.edPhnNumber.error = "Invalid!"
                } else {
                    binding.edPhnNumber.error = null
                }
            }
            return false
        }

        if (binding.edDob.editText?.text.isNullOrEmpty()) {
            return false
        }

        if (binding.edDov.editText?.text.isNullOrEmpty()) {
            return false
        }

        return true
    }

    private fun setUpDobUI() {
        val dateOfBirthPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        binding.edDob.editText?.isEnabled = false
        binding.edDob.setEndIconOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, "DATE_OF_BIRTH")
        }
        dateOfBirthPicker.addOnPositiveButtonClickListener {
            val date = Date(it)
            binding.edDob.editText?.setText(simpleDateFormat.format(date))
        }
    }

    private fun setUpDovUI() {
        val dateOfVaccinationPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        binding.edDov.editText?.isEnabled = false
        binding.edDov.setEndIconOnClickListener {
            dateOfVaccinationPicker.show(parentFragmentManager, "DATE_OF_VACCINATION")
        }
        dateOfVaccinationPicker.addOnPositiveButtonClickListener {
            val date = Date(it)
            binding.edDov.editText?.setText(simpleDateFormat.format(date))
        }
    }

    private fun redirect(url: String) {
        try {
            val customTabColorSchemeParams: CustomTabColorSchemeParams =
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(resources.getColor(R.color.white, null))
                    .setSecondaryToolbarColor(resources.getColor(R.color.white, null))
                    .build()

            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            val customTabIntent: CustomTabsIntent = builder
                .setDefaultColorSchemeParams(customTabColorSchemeParams)
                .setCloseButtonIcon(
                    resources.getDrawable(R.drawable.ic_acion_back, null)
                        .toBitmap()
                )
                .build()

            customTabIntent.launchUrl(
                requireContext(),
                Uri.parse(url)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            showURLFallBack(url)
        }
    }

    private fun showURLFallBack(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            context?.toast(getString(R.string.no_app_found))
        }
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

                        parentJob1.launch {
                            viewModel.getVaccineStatus(
                                binding.edPhnNumber.editText?.text.toString(),
                                binding.edDob.editText?.text.toString(),
                                binding.edDov.editText?.text.toString()
                            )
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
}
