package ca.bc.gov.bchealth.ui.travelpass

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.databinding.FragmentFetchTravelPassBinding
import ca.bc.gov.bchealth.di.ApiClientModule
import ca.bc.gov.bchealth.http.MustBeQueued
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.hideKeyboard
import ca.bc.gov.bchealth.utils.isOnline
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showCardReplacementDialog
import ca.bc.gov.bchealth.utils.viewBindings
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class FetchTravelPassFragment : Fragment(R.layout.fragment_fetch_travel_pass) {

    private val binding by viewBindings(FragmentFetchTravelPassBinding::bind)

    private val viewModel: FetchTravelPassViewModel by viewModels()

    private val args: FetchTravelPassFragmentArgs by navArgs()

    private lateinit var healthCardDto: HealthCardDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        healthCardDto = args.healthCardDto
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        iniUI()
    }

    /*
    * Toolbar UI and functionality
    * */
    private fun setToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                requireContext().hideKeyboard(binding.edPhnNumber)
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.get_federal_travel_pass)

            ivRightOption.visibility = View.VISIBLE
            ivRightOption.setImageResource(R.drawable.ic_help)
            ivRightOption.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_help))
            }
            ivRightOption.contentDescription = getString(R.string.help)

            line1.visibility = View.VISIBLE
        }
    }

    /*
    * Initialize UI functionality
    * */
    private fun iniUI() {

        binding.btnCancel.setOnClickListener {
            requireContext().hideKeyboard(binding.edPhnNumber)
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {

            if (validateInputData()) {

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        viewModel.getFederalTravelPass(
                            healthCardDto,
                            binding.edPhnNumber.editText?.text.toString()
                        )
                    } catch (e: Exception) {
                        if (e is MustBeQueued) {
                            withContext(Dispatchers.Main) {
                                queueUser(e.getValue())
                            }
                        } else {
                            e.printStackTrace()
                            showError(
                                getString(R.string.error),
                                getString(R.string.error_message)
                            )
                        }
                    }
                }
            }
        }

        /*
        * Fetch saved form data
        * */
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRecentFormData.collect {
                    if (it.isNotEmpty()) {

                        val pair = Pair(
                            it.subSequence(0, 10),
                            it.subSequence(10, 20)
                        )

                        val phnArray = arrayOf(pair.first.toString())

                        val adapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            phnArray
                        )

                        val textView = binding.edPhnNumber.editText as AutoCompleteTextView
                        textView.setAdapter(adapter)

                        binding.edPhnNumber.setEndIconDrawable(R.drawable.ic_arrow_down)
                        binding.edPhnNumber.setEndIconOnClickListener {
                            textView.showDropDown()
                        }
                    }
                }
            }
        }

        observeResponse()
    }

    private fun observeResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.responseSharedFlow.collect {
                    when (it) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE

                            val pair = it.data as Pair<*, *>
                            val healthCard = pair.first as HealthCard
                            healthCardDto.federalPass = healthCard.federalPass

                            if (pair.second as Boolean) {
                                requireContext().showCardReplacementDialog() {
                                    viewModel.replaceExitingHealthPass(healthCard)
                                        .invokeOnCompletion {
                                            showFederalTravelPass()
                                        }
                                }
                            } else {
                                showFederalTravelPass()
                            }
                        }
                        is Response.Error -> {
                            ApiClientModule.queueItToken = ""
                            binding.progressBar.visibility = View.INVISIBLE
                            showError(
                                it.errorData?.errorTitle.toString(),
                                it.errorData?.errorMessage.toString()
                            )
                        }
                        is Response.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    /*
    * Validations before network call
    * */
    private fun validateInputData(): Boolean {

        if (binding.edPhnNumber.editText?.text.isNullOrEmpty()) {
            binding.edPhnNumber.isErrorEnabled = true
            binding.edPhnNumber.error = getString(R.string.phn_number_required)
            binding.edPhnNumber.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edPhnNumber.isErrorEnabled = false
                        binding.edPhnNumber.error = null
                    }
            }
            return false
        }

        if (binding.edPhnNumber.editText?.text?.length != 10) {
            binding.edPhnNumber.isErrorEnabled = true
            binding.edPhnNumber.error = getString(R.string.phn_should_be_10_digit)
            binding.edPhnNumber.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edPhnNumber.isErrorEnabled = false
                        binding.edPhnNumber.error = null
                    }
            }
            return false
        }

        if (!requireContext().isOnline()) {
            showError(
                getString(R.string.no_internet),
                getString(R.string.check_connection)
            )
            return false
        }

        return true
    }

    private fun showFederalTravelPass() {
        ApiClientModule.queueItToken = ""
        requireContext().hideKeyboard(binding.edPhnNumber)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.fetchTravelPassFragment, true)
            .build()
        val action = FetchTravelPassFragmentDirections
            .actionFetchTravelPassFragmentToTravelPassFragment(healthCardDto)
        findNavController().navigate(action, navOptions)
    }

    /*
    * HGS APIs are protected by Queue.it
    * User will see the Queue.it waiting page if there are more number of users trying to
    * access HGS service at the same time. Ref: https://github.com/queueit/android-webui-sdk
    * */
    private fun queueUser(value: String) {
        try {
            val valueUri = Uri.parse(URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
            val customerId = valueUri.getQueryParameter("c")
            val waitingRoomId = valueUri.getQueryParameter("e")
            QueueService.IsTest = false
            val q = QueueITEngine(
                requireActivity(),
                customerId,
                waitingRoomId,
                "",
                "",
                object : QueueListener() {
                    override fun onQueuePassed(queuePassedInfo: QueuePassedInfo) {

                        ApiClientModule.queueItToken = queuePassedInfo.queueItToken

                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                viewModel.getFederalTravelPass(
                                    healthCardDto,
                                    binding.edPhnNumber.editText?.text.toString()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showError(
                                    getString(R.string.error),
                                    getString(R.string.error_message)
                                )
                            }
                        }
                    }

                    override fun onQueueViewWillOpen() {
                        Toast.makeText(
                            requireActivity(),
                            "Please wait! We are receiving more requests at the moment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onUserExited() {
                    }

                    override fun onQueueDisabled() {
                    }

                    override fun onQueueItUnavailable() {
                        showError(
                            getString(R.string.error),
                            getString(R.string.error_message)
                        )
                    }

                    override fun onError(error: Error, errorMessage: String) {
                        showError(
                            getString(R.string.error),
                            getString(R.string.error_message)
                        )
                    }

                    override fun onWebViewClosed() {
                    }
                }
            )
            q.run(requireActivity())
        } catch (e: QueueITException) {
            e.printStackTrace()
            showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        }
    }

    private fun showError(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setCancelable(false)
            .setMessage(message)
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
