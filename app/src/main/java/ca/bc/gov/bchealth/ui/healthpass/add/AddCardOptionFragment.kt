package ca.bc.gov.bchealth.ui.healthpass.add

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddCardOptionsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.repository.model.PatientVaccineRecord
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [AddCardOptionFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddCardOptionFragment : BaseFragment(R.layout.fragment_add_card_options) {

    private val binding by viewBindings(FragmentAddCardOptionsBinding::bind)
    private val addOrUpdateCardViewModel: AddOrUpdateCardViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()
    private lateinit var action: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        action = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            if (it != null) {
                addOrUpdateCardViewModel.processQRCode(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner,
            Observer {
                if (it > 0) {
                    sharedViewModel.setModifiedRecordId(it)
                    findNavController().currentBackStackEntry?.savedStateHandle
                        ?.remove<Long>(FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS)
                    findNavController().popBackStack()
                }
            }
        )

        binding.btnScanQrCode.setOnClickListener {
            findNavController().navigate(R.id.action_addCardOptionFragment_to_onBoardingFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addOrUpdateCardViewModel.uiState.collect { state ->
                    if (state.state != null) {
                        performActionBasedOnState(state)
                    }
                }
            }
        }

        binding.btnImagePicker.setOnClickListener {
            action.launch("image/*")
        }

        binding.btnEnterInfo.setOnClickListener {
            findNavController().navigate(R.id.fetchVaccineRecordFragment)
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.add_a_health_pass)
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
                sharedViewModel.setModifiedRecordId(state.modifiedRecordId)
                navigateToHealthPass()
                addOrUpdateCardViewModel.resetStatus()
            }
            Status.DUPLICATE -> {

                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.error_duplicate_title),
                    msg = getString(R.string.error_duplicate_message),
                    positiveBtnMsg = getString(R.string.btn_ok),
                    positiveBtnCallback = {
                        addOrUpdateCardViewModel.resetStatus()
                    }
                )
            }

            Status.ERROR -> {
                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.error_invalid_qr_code_title),
                    msg = getString(R.string.error_invalid_qr_code_message),
                    positiveBtnMsg = getString(R.string.btn_ok),
                    positiveBtnCallback = {
                        addOrUpdateCardViewModel.resetStatus()
                    }
                )
            }
            null -> return
        }
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

    private fun navigateToHealthPass() {
        // Snowplow event
        analyticsFeatureViewModel.track(AnalyticsAction.ADD_QR, AnalyticsActionData.UPLOAD)
        findNavController().popBackStack()
    }
}
