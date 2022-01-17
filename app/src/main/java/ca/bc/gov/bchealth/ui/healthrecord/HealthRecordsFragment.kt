package ca.bc.gov.bchealth.ui.healthrecord

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHealthRecordsBinding
import ca.bc.gov.bchealth.ui.healthrecord.add.AddHealthRecordsOptionsViewModel
import ca.bc.gov.bchealth.ui.healthrecord.add.OptionType
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthRecordsFragment : Fragment(R.layout.fragment_health_records) {

    private val binding by viewBindings(FragmentHealthRecordsBinding::bind)
    private val viewModel: HealthRecordsViewModel by viewModels()
    private val addHealthRecordsOptionsViewModel: AddHealthRecordsOptionsViewModel by viewModels()
    private lateinit var adapter: HealthRecordsAdapter
    private lateinit var optionsAdapter: HealthRecordOptionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optionsAdapter = HealthRecordOptionAdapter {
            when (it) {
                OptionType.VACCINE -> {
                    findNavController().navigate(R.id.fetchVaccineRecordFragment)
                }
                OptionType.TEST -> {
                    findNavController().navigate(R.id.fetchTestRecordFragment)
                }
            }
        }
        adapter = HealthRecordsAdapter {
            val action =
                HealthRecordsFragmentDirections.actionHealthRecordsFragmentToIndividualHealthRecordFragment(
                    it.patientId.toLong(),
                    it.name
                )
            findNavController().navigate(action)
        }

        binding.ivAddHealthRecord.setOnClickListener {
            findNavController().navigate(R.id.addHealthRecordsFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectHealthRecordsFlow()
            }
        }

        val spacing = resources.getDimensionPixelSize(R.dimen.space_2_x) / 2
        with(binding.rvMembers) {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.set(spacing, spacing, spacing, spacing)
                }
            })
        }
    }

    private suspend fun collectHealthRecordsFlow() {
        viewModel.patientHealthRecords.collect { records ->
            if (records.isNotEmpty()) {
                binding.ivAddHealthRecord.visibility = View.VISIBLE
                binding.rvMembers.adapter = adapter
                binding.rvMembers.layoutManager = GridLayoutManager(requireContext(), 2)
                adapter.submitList(records)
            } else {
                binding.ivAddHealthRecord.visibility = View.GONE
                binding.rvMembers.adapter = optionsAdapter
                binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
                optionsAdapter.submitList(
                    addHealthRecordsOptionsViewModel.getHealthRecordOption().toMutableList()
                )
            }
        }
    }
}