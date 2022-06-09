package ca.bc.gov.bchealth.ui.healthrecord.add

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchCovidTestResultBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.PhnHelper
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.RecentPhnDobViewModel
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
class FetchTestRecordFragment : BaseFragment(R.layout.fragment_fetch_covid_test_result) {
    private val binding by viewBindings(FragmentFetchCovidTestResultBinding::bind)
    private val viewModel: FetchTestRecordsViewModel by viewModels()
    private val recentPhnDobViewModel: RecentPhnDobViewModel by viewModels()

    companion object {
        const val TEST_RECORD_ADDED_SUCCESS = "TEST_RECORD_ADDED_SUCCESS"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPhnUI()

        setUpDobUI()

        setUpDotUI()

        initClickListeners()

        observeCovidTestResult()

        observeCovidTestRecordAddition()
    }

    private fun observeCovidTestRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            TEST_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner
        ) {
            if (it > 0) {
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(TEST_RECORD_ADDED_SUCCESS, it)
                findNavController().popBackStack()
            }
        }
    }

    private fun showLoader(value: Boolean) {
        binding.btnSubmit.isEnabled = !value
        binding.progressBar.isVisible = value
    }

    private fun observeCovidTestResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    showLoader(state.onLoading)

                    if (state.errorData != null) {
                        AlertDialogHelper.showAlertDialog(
                            context = requireContext(),
                            title = getString(state.errorData.title),
                            msg = getString(state.errorData.message),
                            positiveBtnMsg = getString(R.string.btn_ok)
                        )
                    }

                    if (state.queItTokenUpdated) {
                        fetchTestRecord()
                    }

                    if (state.onTestResultFetched > 0) {
                        if (binding.checkboxRemember.isChecked) {
                            val phn = binding.edPhn.text.toString()
                            val dob = binding.edtDob.text.toString()
                            recentPhnDobViewModel.setRecentPhnDobData(phn, dob)
                        }
                        val action = FetchTestRecordFragmentDirections
                            .actionFetchTestRecordFragmentToTestResultDetailFragment(
                                state.patientId,
                                state.onTestResultFetched
                            )
                        findNavController().navigate(action)
                    }

                    if (state.onMustBeQueued && state.queItUrl != null) {
                        queUser(state.queItUrl)
                    }

                    if (!state.isConnected) {
                        binding.root.showNoInternetConnectionMessage(requireContext())
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.btnSubmit.setOnClickListener {
            fetchTestRecord()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun fetchTestRecord() {
        val phn = binding.edPhn.text.toString()
        val dob = binding.edtDob.text.toString()
        val dot = binding.edtDoc.text.toString()

        if (PhnHelper().validatePhnData(
                binding.edPhnNumber,
                requireContext()
            ) &&
            DatePickerHelper().validateDatePickerData(
                    binding.tipDob,
                    requireContext(),
                    getString(R.string.dob_required)
                ) &&
            DatePickerHelper().validateDatePickerData(
                    binding.tipDot,
                    requireContext(),
                    getString(R.string.dot_required)
                )
        ) {

            viewModel.fetchTestRecord(phn, dob, dot)
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.add_covid_test_result)
            inflateMenu(R.menu.help_menu)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_help -> {
                        requireActivity().redirect(getString(R.string.url_help))
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun setUpPhnUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recentPhnDobViewModel.recentPhnDob.collect { recentPhnDob ->
                    val (phn, dob) = recentPhnDob
                    val phnArray = arrayOf(phn)

                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        phnArray
                    )

                    val textView = binding.edPhnNumber.editText as AutoCompleteTextView
                    textView.setAdapter(adapter)
                    textView.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, _, _ ->
                            binding.tipDob.editText?.setText(dob)
                            binding.tipDot.editText?.requestFocus()
                        }

                    binding.edPhnNumber.setEndIconDrawable(R.drawable.ic_arrow_down)
                    binding.edPhnNumber.setEndIconOnClickListener {
                        textView.showDropDown()
                    }
                }
            }
        }
    }

    private fun setUpDobUI() {
        DatePickerHelper().initializeDatePicker(
            binding.tipDob,
            getString(R.string.enter_dob),
            parentFragmentManager,
            "DATE_OF_BIRTH"
        )
    }

    private fun setUpDotUI() {
        DatePickerHelper().initializeDatePicker(
            binding.tipDot,
            getString(R.string.enter_dot),
            parentFragmentManager,
            "DATE_OF_TEST"
        )
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
