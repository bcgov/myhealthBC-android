package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTimelineViewBinding
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.patient.PatientDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimelineViewFragment : Fragment(R.layout.fragment_timeline_view) {

    private val binding by viewBindings(FragmentTimelineViewBinding::bind)
    private val viewModel: TimelineViewModel by viewModels()
    private var patients = mutableListOf<PatientDto>()
    private val patientNames = mutableListOf<String>()
    private lateinit var patientAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()

        observePatientsList()
    }

    private fun observePatientsList() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    if (uiState.onPatientListDto != null) {
                        patients = uiState.onPatientListDto.patientDtos.toMutableList()
                        patients.forEach {
                            patientNames.add(it.fullName.uppercase())
                        }
                        patientAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun initUi() {
        initPatientNames()

        binding.ivAddHealthRecord.setOnClickListener {
            findNavController().navigate(R.id.addHealthRecordsFragment)
        }

        initType()

        initTime()

        initTimeLineView()
    }

    private fun initTimeLineView() {
        // TODO: 10/02/22 Initialize recycler view for showing time line of health records
    }

    private fun initTime() {
        binding.tvTime.editText?.apply {
            isEnabled = false
            hint = context.getString(R.string.time)
        }
        binding.tvTime.setEndIconOnClickListener {
            findNavController().navigate(R.id.timeFilterFragment)
        }
    }

    private fun initType() {
        binding.tvType.editText?.apply {
            isEnabled = false
            hint = context.getString(R.string.type)
        }
        binding.tvType.setEndIconOnClickListener {
            findNavController().navigate(R.id.typeFilterFragment)
        }
    }

    private fun initPatientNames() {
        patients.clear()
        patientNames.clear()
        patientAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            patientNames
        )
        val tvPatientName = binding.tvPatientName.editText as AutoCompleteTextView
        tvPatientName.setAdapter(patientAdapter)
        tvPatientName.isEnabled = false
        tvPatientName.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                viewModel.getTimeLine(patients[p2].id)
            }

        viewModel.getPatients()
    }
}
