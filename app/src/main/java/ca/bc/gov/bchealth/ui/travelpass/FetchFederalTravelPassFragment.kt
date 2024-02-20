package ca.bc.gov.bchealth.ui.travelpass

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchTravelPassBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.healthpass.add.AddOrUpdateCardViewModel
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordUiState
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordViewModel
import ca.bc.gov.bchealth.ui.healthpass.add.Status
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.PhnHelper
import ca.bc.gov.bchealth.utils.hideKeyboard
import ca.bc.gov.bchealth.utils.inflateHelpButton
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.bchealth.viewmodel.RecentPhnDobViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.utils.dateString
import ca.bc.gov.common.utils.yyyy_MM_dd
import ca.bc.gov.repository.model.PatientVaccineRecord
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class FetchFederalTravelPassFragment : BaseFragment(R.layout.fragment_fetch_travel_pass) {
    private val binding by viewBindings(FragmentFetchTravelPassBinding::bind)
    private val viewModel: FetchVaccineRecordViewModel by viewModels()
    private val args: FetchFederalTravelPassFragmentArgs by navArgs()
    private val addOrUpdateCardViewModel: AddOrUpdateCardViewModel by viewModels()
    private lateinit var patientDataDto: PatientWithVaccineAndDosesDto
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()
    private val recentPhnDobViewModel: RecentPhnDobViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            requireContext().hideKeyboard(it)
            binding.scrollView.clearFocus()
            fetchTravelPass()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        launchOnStart {
            launch { collectPhnUi() }
            launch { collectUiState() }
            launch { collectCardUiState() }
        }

        viewModel.getPatientWithVaccineRecord(args.patientId)
        binding.scrollView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboard(view)
            view?.clearFocus()
            return@setOnTouchListener true
        }
    }

    private fun handleNoInternetConnection(uiState: FetchVaccineRecordUiState) {
        if (!uiState.isConnected) {
            binding.root.showNoInternetConnectionMessage(requireContext())
            viewModel.resetUiState()
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.get_federal_travel_pass)
            inflateHelpButton {
                requireActivity().redirect(getString(R.string.url_help))
            }
        }
    }

    private suspend fun collectPhnUi() {
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
            binding.edPhnNumber.setEndIconDrawable(R.drawable.ic_arrow_down)
            binding.edPhnNumber.setEndIconOnClickListener {
                textView.showDropDown()
            }
        }
    }

    private suspend fun collectUiState() {
        viewModel.uiState.collect { uiState ->
            showLoader(uiState.onLoading)
            if (!uiState.isHgServicesUp) {
                binding.root.showServiceDownMessage(requireContext())
                viewModel.resetUiState()
            }

            if (uiState.errorData != null) {
                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(uiState.errorData.title),
                    msg = getString(uiState.errorData.message),
                    positiveBtnMsg = getString(R.string.btn_ok)
                )
            }

            if (uiState.vaccineRecord != null) {
                addOrUpdateCardViewModel.processResult(uiState.vaccineRecord)
            }

            if (uiState.patientDataDto != null) {
                patientDataDto = uiState.patientDataDto
            }

            handleNoInternetConnection(uiState)
        }
    }

    private suspend fun collectCardUiState() {
        addOrUpdateCardViewModel.uiState.collect { state ->

            showLoader(state.onLoading)
            if (state.state != null) {
                performActionBasedOnState(state.state, state.vaccineRecord)
            }
        }
    }

    private fun showLoader(value: Boolean) {
        binding.btnSubmit.isEnabled = !value
        binding.progressBar.indicator.isVisible = value
    }

    private fun fetchTravelPass() {
        val phn = binding.edPhn.text.toString()
        if (PhnHelper().validatePhnData(binding.edPhnNumber)) {
            patientDataDto.vaccineWithDoses?.doses?.let { doses ->
                viewModel.fetchVaccineRecord(
                    phn,
                    patientDataDto.patient.dateOfBirth.dateString(yyyy_MM_dd),
                    doses.last().date.dateString(yyyy_MM_dd)
                )
            }
        }
    }

    private fun performActionBasedOnState(state: Status, record: PatientVaccineRecord?) =
        when (state) {
            Status.CAN_INSERT,
            Status.DUPLICATE,
            Status.CAN_UPDATE -> {
                record?.let { updateRecord(it) }
            }
            Status.INSERTED,
            Status.UPDATED -> {
                record?.vaccineRecordDto?.federalPass?.let { showTravelPass(it) }
            }
            else -> {
            }
        }

    private fun updateRecord(vaccineRecord: PatientVaccineRecord) {
        addOrUpdateCardViewModel.update(vaccineRecord)
    }

    private fun showTravelPass(federalPass: String) {
        // Snowplow event
        analyticsFeatureViewModel.track(AnalyticsAction.ADD_QR, AnalyticsActionData.GET)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.fetchFederalTravelPass, true)
            .build()

        findNavController().navigate(
            R.id.pdfRendererFragment,
            bundleOf(
                "base64pdf" to federalPass,
                "title" to getString(R.string.travel_pass)
            ),
            navOptions
        )
    }
}
