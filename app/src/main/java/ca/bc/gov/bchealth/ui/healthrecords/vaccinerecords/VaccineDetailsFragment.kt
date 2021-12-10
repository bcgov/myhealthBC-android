package ca.bc.gov.bchealth.ui.healthrecords.vaccinerecords

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentVaccineDetailsBinding
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.utils.showHealthRecordDeleteDialog
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VaccineDetailsFragment : Fragment(R.layout.fragment_vaccine_details) {

    private val binding by viewBindings(FragmentVaccineDetailsBinding::bind)

    private val viewModel: VaccineDetailsViewModel by viewModels()

    private val args: VaccineDetailsFragmentArgs by navArgs()

    private lateinit var adapter: VaccineDetailsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        initUI()
    }

    private fun initUI() {

        binding.tvFullName.text = args.healthRecord.name

        binding.tvIssueDate.text = getString(R.string.issued_on)
            .plus(" ")
            .plus(args.healthRecord.issueDate)

        args.healthRecord.immunizationStatus?.let { setUiState(it) }

        setUpRecyclerView()
    }

    // Recycler view setup
    private fun setUpRecyclerView() {

        adapter = VaccineDetailsAdapter(args.healthRecord.vaccineDataList)

        val recyclerView = binding.rvVaccineList

        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
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

                requireContext().showHealthRecordDeleteDialog {

                    binding.progressBar.visibility = View.VISIBLE

                    args.healthRecord.healthPassId?.let { it1 ->

                        viewModel.deleteVaccineRecord(it1).invokeOnCompletion {
                            findNavController().popBackStack()
                        }
                    }
                }
            }

            line1.visibility = View.VISIBLE
        }
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

            ImmunizationStatus.INVALID_QR_CODE -> {
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
