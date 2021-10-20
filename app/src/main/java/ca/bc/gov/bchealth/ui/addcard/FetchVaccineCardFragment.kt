package ca.bc.gov.bchealth.ui.addcard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.system.Os.bind
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchVaccineCardBinding
import ca.bc.gov.bchealth.di.ApiClientModule
import ca.bc.gov.bchealth.http.MustBeQueued
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.isOnline
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.util.Base64
import androidx.core.graphics.drawable.toIcon
import com.google.mlkit.vision.common.InputImage


@AndroidEntryPoint
class FetchVaccineCardFragment : Fragment(R.layout.fragment_fetch_vaccine_card) {

    private val binding by viewBindings(FragmentFetchVaccineCardBinding::bind)

    private val simpleDateFormat = SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH)

    private val _queuePassed = AtomicBoolean(false)

    private val parentJob = CoroutineScope(IO)

    private val parentJob1 = CoroutineScope(IO)

    private val viewModel: FetchVaccineCardViewModel by viewModels()

    /* private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
         print(exception.printStackTrace())

         if (exception !is MustBeQueued) {
             exception.printStackTrace()
         }
         assert(exception is MustBeQueued)
         val handler = Handler(Looper.getMainLooper())
         handler.post { queueUser((exception as MustBeQueued).getValue()) }
     }*/

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
                redirect(getString(R.string.url_help))
            }

            line1.visibility = View.VISIBLE
        }
    }

    private fun iniUI() {

        if (BuildConfig.DEBUG) {
            /*binding.edPhnNumber.editText?.setText("9000201422")
            binding.edDob.editText?.setText("1989-12-12")
            binding.edDov.editText?.setText("2021-05-15")*/

            binding.edPhnNumber.editText?.setText("9000691304")
            binding.edDob.editText?.setText("1965-01-14")
            binding.edDov.editText?.setText("2021-07-15")
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

                viewLifecycleOwner.lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                        try {
                            viewModel.getVaccineStatus(
                                binding.edPhnNumber.editText?.text.toString(),
                                binding.edDob.editText?.text.toString(),
                                binding.edDov.editText?.text.toString()
                            )
                        } catch (exception: Exception){
                            if (exception !is MustBeQueued) {
                                exception.printStackTrace()
                            }
                            assert(exception is MustBeQueued)
                            val handler = Handler(Looper.getMainLooper())
                            handler.post { queueUser((exception as MustBeQueued).getValue()) }
                        }

                        viewModel.vaxStatusResponseLiveData.collect(){
                            when (it) {
                                is Response.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Response.Success -> {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    it.data?.resourcePayload?.qrCode?.data?.let { it1 ->

                                        val decodedString: ByteArray =
                                            Base64.decode(it1, Base64.DEFAULT)
                                        val decodedByte = BitmapFactory.decodeByteArray(
                                            decodedString,
                                            0,
                                            decodedString.size
                                        )

                                        var image: InputImage? = null
                                        try {
                                            image = InputImage.fromBitmap(decodedByte, 0)
                                        } catch (e: java.lang.Exception) {
                                            e.printStackTrace()
                                        }
                                        image?.let { it2 -> viewModel.processImage(it2) }
                                    }
                                }
                                is Response.Error -> {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    showError(getString(R.string.error), message = it.errorMessage.toString())
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack(R.id.myCardsFragment, false)
            } else {
                showError(
                    getString(R.string.bc_invalid_barcode_title),
                    getString(R.string.bc_invalid_barcode_upload_message)
                )
            }
        })
    }

    private fun validateInputData(): Boolean {

        if (binding.edPhnNumber.editText?.text.isNullOrEmpty()) {
            binding.edPhnNumber.isErrorEnabled = true
            binding.edPhnNumber.error = "Personal Health Number is required"
            binding.edPhnNumber.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edPhnNumber.isErrorEnabled = false
                        binding.edPhnNumber.error = null
                    }
            }
            return false
        }

        if (binding.edDob.editText?.text.isNullOrEmpty()) {
            binding.edDob.isErrorEnabled = true
            binding.edDob.error = "Date of Birth is required"
            binding.edDob.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edDob.isErrorEnabled = false
                        binding.edDob.error = null
                    }
            }
            return false
        }

        if (binding.edDov.editText?.text.isNullOrEmpty()) {
            binding.edDov.isErrorEnabled = true
            binding.edDov.error = "Date of Vaccination is required"
            binding.edDov.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edDov.isErrorEnabled = false
                        binding.edDov.error = null
                    }
            }
            return false
        }

        if(!requireContext().isOnline()){
            showError(
                getString(R.string.no_internet),
                getString(R.string.check_connection)
            )
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
        binding.edDob.editText?.setOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, "DATE_OF_BIRTH")
        }
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
        binding.edDov.editText?.setOnClickListener {
            dateOfVaccinationPicker.show(parentFragmentManager, "DATE_OF_VACCINATION")
        }
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

                        viewLifecycleOwner.lifecycleScope.launch {
                            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                try {
                                    viewModel.getVaccineStatus(
                                        binding.edPhnNumber.editText?.text.toString(),
                                        binding.edDob.editText?.text.toString(),
                                        binding.edDov.editText?.text.toString()
                                    )
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                            }
                        }
                        /*parentJob1.launch {
                            viewModel.getVaccineStatus(
                                binding.edPhnNumber.editText?.text.toString(),
                                binding.edDob.editText?.text.toString(),
                                binding.edDov.editText?.text.toString()
                            )
                        }*/
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

    private fun showError(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setCancelable(false)
            .setMessage(message)
            .setPositiveButton(getString(android.R.string.ok)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}
