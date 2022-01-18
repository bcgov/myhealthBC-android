package ca.bc.gov.bchealth.ui.healthpass.add

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.AnalyticsText
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.databinding.FragmentAddCardOptionsBinding
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.VaccineRecordState
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [AddCardOptionFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddCardOptionFragment : Fragment(R.layout.fragment_add_card_options) {

    private val binding by viewBindings(FragmentAddCardOptionsBinding::bind)

    private val addOrUpdateCardViewModel: AddOrUpdateCardViewModel by viewModels()

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
        val savedStateHandle: SavedStateHandle =
            findNavController().currentBackStackEntry!!.savedStateHandle
        savedStateHandle.getLiveData<Pair<VaccineRecordState, PatientVaccineRecord?>>(
            FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
        )
            .observe(findNavController().currentBackStackEntry!!, Observer {
                if (it != null) {
                    addOrUpdateCardViewModel.processResult(it)
                }
            })


        binding.btnScanQrCode.setOnClickListener {
            findNavController().navigate(R.id.action_addCardOptionFragment_to_onBoardingFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                addOrUpdateCardViewModel.uiState.collect { state ->
                    performActionBasedOnState(state.state, state.vaccineRecord)
                }
            }
        }

        binding.btnImagePicker.setOnClickListener {
            action.launch("image/*")
        }

        binding.btnEnterInfo.setOnClickListener {
            findNavController().navigate(R.id.action_addCardOptionFragment_to_fetchVaccineRecordFragment)
        }

        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_a_health_pass)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
            line1.visibility = View.VISIBLE
        }
    }

    private fun performActionBasedOnState(state: Status, record: PatientVaccineRecord?) =
        when (state) {

            Status.CAN_INSERT -> {
                record?.let { insert(it) }
            }
            Status.CAN_UPDATE -> {
                record?.let { updateRecord(it) }
            }
            Status.INSERTED,
            Status.UPDATED -> {
                navigateToCardsList()
            }
            Status.DUPLICATE -> {
                requireContext().showError(
                    getString(R.string.error_duplicate_title),
                    getString(R.string.error_duplicate_message)
                )
            }
            else -> {
            }
        }

    private fun updateRecord(vaccineRecord: PatientVaccineRecord) {
        requireContext().showAlertDialog(
            title = getString(R.string.replace_health_pass_title),
            message = getString(R.string.replace_health_pass_message),
            positiveButtonText = getString(R.string.replace),
            negativeButtonText = getString(R.string.not_now)
        ) {
            addOrUpdateCardViewModel.update(vaccineRecord)
        }
    }

    private fun insert(vaccineRecord: PatientVaccineRecord) {
        addOrUpdateCardViewModel.insert(vaccineRecord)
    }

    private fun navigateToCardsList() {
        // Snowplow event
        Snowplow.getDefaultTracker()?.track(
            SelfDescribingEvent
                .get(
                    AnalyticsAction.AddQR.value,
                    AnalyticsText.Upload.value
                )
        )
        findNavController().navigate(R.id.action_addCardOptionFragment_to_healthPassFragment)
    }
}
