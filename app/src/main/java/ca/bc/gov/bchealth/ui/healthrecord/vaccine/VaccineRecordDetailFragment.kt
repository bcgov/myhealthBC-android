package ca.bc.gov.bchealth.ui.healthrecord.vaccine

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentVaccineRecordDetailBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.common.utils.toDateTimeString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VaccineRecordDetailFragment : Fragment(R.layout.fragment_vaccine_record_detail) {

    private val binding by viewBindings(FragmentVaccineRecordDetailBinding::bind)

    private val args: VaccineRecordDetailFragmentArgs by navArgs()

    private val viewModel: VaccineRecordDetailViewModel by viewModels()

    private lateinit var adapter: VaccineDetailsAdapter

    private var vaccineRecordId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        initUI()
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.vaccination_record)

            tvRightOption.visibility = View.VISIBLE
            tvRightOption.text = getString(R.string.delete)
            tvRightOption.setOnClickListener {

                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.delete_hc_record_title),
                    msg = getString(R.string.delete_individual_vaccine_record_message),
                    positiveBtnMsg = getString(R.string.delete),
                    negativeBtnMsg = getString(R.string.not_now),
                    positiveBtnCallback = {
                        viewModel.deleteVaccineRecord(vaccineRecordId).invokeOnCompletion {
                            findNavController().popBackStack()
                        }
                    }
                )
            }

            line1.visibility = View.VISIBLE
        }
    }

    private fun initUI() {

        setUpRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    state.onVaccineRecordDtoDetail?.let { patientAndVaccineRecord ->

                        patientAndVaccineRecord.vaccineWithDoses?.vaccine?.id?.let {
                            vaccineRecordId = it
                        }

                        binding.tvFullName.text = patientAndVaccineRecord.patient.fullName

                        binding.tvIssueDate.text = getString(R.string.issued_on)
                            .plus(" ")
                            .plus(
                                patientAndVaccineRecord.vaccineWithDoses?.vaccine?.qrIssueDate
                                    ?.toDateTimeString()
                            )

                        patientAndVaccineRecord.vaccineWithDoses?.vaccine?.status
                            ?.let { status -> setUiState(status) }

                        patientAndVaccineRecord.vaccineWithDoses?.doses
                            ?.let { doses -> adapter.submitList(doses) }
                    }
                }
            }
        }

        viewModel.getVaccineRecordDetails(args.patientId)
    }

    private fun setUpRecyclerView() {

        adapter = VaccineDetailsAdapter()

        val recyclerView = binding.rvVaccineList

        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUiState(immunizationStatus: ImmunizationStatus) {

        val partiallyVaccinatedColor = resources.getColor(R.color.status_blue, null)
        val fullyVaccinatedColor = resources.getColor(R.color.status_green, null)
        val inValidColor = resources.getColor(R.color.grey, null)

        var color = inValidColor
        var statusText = ""
        when (immunizationStatus) {

            ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                color = partiallyVaccinatedColor
                statusText = getString(R.string.partially_vaccinated)
                binding.tvVaccineStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
            }

            ImmunizationStatus.FULLY_IMMUNIZED -> {
                color = fullyVaccinatedColor
                statusText = getString(R.string.vaccinated)
                binding.tvVaccineStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_mark, 0, 0, 0
                    )
            }

            ImmunizationStatus.INVALID -> {
                color = inValidColor
                statusText = getString(R.string.no_record)
                binding.tvVaccineStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
            }
        }

        binding.tvVaccineStatus.text = statusText

        binding.viewStatus.setBackgroundColor(color)
    }
}
