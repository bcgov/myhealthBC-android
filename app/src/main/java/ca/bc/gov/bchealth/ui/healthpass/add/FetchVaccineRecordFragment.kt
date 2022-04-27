package ca.bc.gov.bchealth.ui.healthpass.add

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import ca.bc.gov.bchealth.databinding.FragmentFetchVaccineRecordBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.PhnHelper
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.bchealth.viewmodel.RecentPhnDobViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.repository.model.PatientVaccineRecord
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
class FetchVaccineRecordFragment : BaseFragment(R.layout.fragment_fetch_vaccine_record) {
    private val binding by viewBindings(FragmentFetchVaccineRecordBinding::bind)
    private val viewModel: FetchVaccineRecordViewModel by viewModels()
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()
    private val recentPhnDobViewModel: RecentPhnDobViewModel by viewModels()
    private val addOrUpdateCardViewModel: AddOrUpdateCardViewModel by viewModels()

    companion object {
        private const val TAG = "FetchVaccineRecordFragment"
        const val VACCINE_RECORD_ADDED_SUCCESS = "VACCINE_RECORD_ADDED_SUCCESS"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPhnUI()

        setUpDobUI()

        setUpDovUI()

        initClickListeners()

        observeUiState()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                addOrUpdateCardViewModel.uiState.collect { state ->
                    if (state.state != null) {
                        performActionBasedOnState(state)
                    }
                }
            }
        }

        observeVaccineRecordAddition()
    }

    private fun observeVaccineRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            VACCINE_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner
        ) {
            if (it > 0) {
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(VACCINE_RECORD_ADDED_SUCCESS, it)
                findNavController().popBackStack()
            }
        }
    }

    private fun performActionBasedOnState(state: AddCardOptionUiState) {
        when (state.state) {

            Status.CAN_INSERT -> {
                state.vaccineRecord?.let { insert(it) }
                addOrUpdateCardViewModel.resetStatus()
            }
            Status.CAN_UPDATE -> {
                state.vaccineRecord?.let { updateRecord(it) }
                addOrUpdateCardViewModel.resetStatus()
            }
            Status.INSERTED,
            Status.UPDATED -> {
                analyticsFeatureViewModel.track(
                    AnalyticsAction.ADD_QR,
                    AnalyticsActionData.GET
                )
                if (binding.checkboxRemember.isChecked) {
                    val phn = binding.edPhn.text.toString()
                    val dob = binding.edDob.text.toString()
                    recentPhnDobViewModel.setRecentPhnDobData(phn, dob)
                }
                if (findNavController().previousBackStackEntry?.destination?.id ==
                    R.id.addCardOptionFragment
                ) {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            VACCINE_RECORD_ADDED_SUCCESS,
                            state.modifiedRecordId
                        )
                    findNavController().popBackStack()
                } else {
                    val action = FetchVaccineRecordFragmentDirections
                        .actionFetchVaccineRecordFragmentToVaccineRecordDetailFragment(
                            state.modifiedRecordId
                        )
                    findNavController().navigate(action)
                }
                addOrUpdateCardViewModel.resetStatus()
            }
            Status.DUPLICATE -> {
                showErrorDialog(R.string.error_duplicate_title, R.string.error_duplicate_message)
            }

            Status.ERROR -> {
                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.error_invalid_qr_code_title),
                    msg = getString(R.string.error_invalid_qr_code_message),
                    positiveBtnMsg = getString(R.string.btn_ok)
                )
            }
        }
    }

    private fun showErrorDialog(title: Int, message: Int) {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(title),
            msg = getString(message),
            positiveBtnMsg = getString(R.string.btn_ok),
            positiveBtnCallback = {
                addOrUpdateCardViewModel.resetStatus()
                findNavController().popBackStack()
            }
        )
    }

    private fun updateRecord(vaccineRecord: PatientVaccineRecord) {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.replace_health_pass_title),
            msg = getString(R.string.replace_health_pass_message),
            positiveBtnMsg = getString(R.string.replace),
            negativeBtnMsg = getString(R.string.not_now),
            positiveBtnCallback = {
                addOrUpdateCardViewModel.update(vaccineRecord)
            }
        )
    }

    private fun insert(vaccineRecord: PatientVaccineRecord) {
        addOrUpdateCardViewModel.insert(vaccineRecord)
    }

    private fun showLoader(value: Boolean) {
        binding.btnSubmit.isEnabled = !value
        binding.progressBar.isVisible = value
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    showLoader(uiState.onLoading)

                    if (uiState.errorData != null) {
                        AlertDialogHelper.showAlertDialog(
                            context = requireContext(),
                            title = getString(uiState.errorData.title),
                            msg = getString(uiState.errorData.message),
                            positiveBtnMsg = getString(R.string.btn_ok)
                        )
                    }

                    handleQueueIt(uiState)

                    if (uiState.vaccineRecord != null) {
                        // savedStateHandle.set(VACCINE_RECORD_ADDED_SUCCESS, uiState.vaccineRecord)
                        addOrUpdateCardViewModel.processResult(uiState.vaccineRecord)
                    }
                }
            }
        }
    }

    private fun handleQueueIt(uiState: FetchVaccineRecordUiState) {
        if (uiState.queItTokenUpdated) {
            fetchVaccineRecord()
        }

        if (uiState.onMustBeQueued && uiState.queItUrl != null) {
            Log.d(TAG, "Mut be queue, url = ${uiState.queItUrl}")
            queUser(uiState.queItUrl)
        }
    }

    private fun initClickListeners() {
        binding.btnSubmit.setOnClickListener {
            fetchVaccineRecord()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun fetchVaccineRecord() {
        val phn = binding.edPhn.text.toString()
        val dob = binding.edDob.text.toString()
        val dov = binding.edDov.text.toString()

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
                    binding.tipDov,
                    requireContext(),
                    getString(R.string.dov_required)
                )
        ) {
            viewModel.fetchVaccineRecord(phn, dob, dov)
        }
    }

    private fun setUpDovUI() {
        DatePickerHelper().initializeDatePicker(
            binding.tipDov,
            getString(R.string.enter_dob),
            parentFragmentManager,
            "DATE_OF_VACCINATION"
        )
    }

    private fun setUpDobUI() {
        DatePickerHelper().initializeDatePicker(
            binding.tipDob,
            getString(R.string.enter_dov),
            parentFragmentManager,
            "DATE_OF_BIRTH"
        )
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
                            binding.tipDov.editText?.requestFocus()
                        }

                    binding.edPhnNumber.setEndIconDrawable(R.drawable.ic_arrow_down)
                    binding.edPhnNumber.setEndIconOnClickListener {
                        textView.showDropDown()
                    }
                }
            }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.add_a_health_pass)
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
            e.printStackTrace()
        }
    }
}
