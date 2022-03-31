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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHealthRecordsBinding
import ca.bc.gov.bchealth.ui.healthrecord.protectiveword.HiddenMedicationRecordAdapter
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.AuthenticationStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */

private const val GRID_SPAN_COUNT = 2
private const val LIST_SPAN_COUNT = 1

@AndroidEntryPoint
class HealthRecordsFragment : Fragment(R.layout.fragment_health_records) {

    private val binding by viewBindings(FragmentHealthRecordsBinding::bind)
    private val viewModel: HealthRecordsViewModel by viewModels()
    private lateinit var adapter: HealthRecordsAdapter
    private lateinit var hiddenMedicationRecordsAdapter: HiddenMedicationRecordAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        adapter = HealthRecordsAdapter {
            val action =
                HealthRecordsFragmentDirections
                    .actionHealthRecordsFragmentToIndividualHealthRecordFragment(
                        it.patientId,
                        it.name
                    )
            findNavController().navigate(action)
        }
        hiddenMedicationRecordsAdapter = HiddenMedicationRecordAdapter { onMedicationAccessClick() }

        concatAdapter = ConcatAdapter(
            hiddenMedicationRecordsAdapter,
            adapter
        )
        binding.rvMembers.adapter = concatAdapter

        binding.ivAddHealthRecord.setOnClickListener {
            findNavController().navigate(R.id.addHealthRecordsFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
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

    private fun onMedicationAccessClick() {
        val isProtectiveWordRequired = viewModel.isProtectiveWordRequired()
        if (isProtectiveWordRequired) {
            findNavController().navigate(R.id.protectiveWordFragment)
        }
    }

    private suspend fun collectHealthRecordsFlow() {
        viewModel.patientHealthRecords.collect { records ->
            if (records.isNotEmpty()) {
                binding.ivAddHealthRecord.visibility = View.VISIBLE
                // binding.rvMembers.adapter = concatAdapter
                if (records.any { it.authStatus == AuthenticationStatus.AUTHENTICATED }) {
                    binding.rvMembers.layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT).apply {
                        spanSizeLookup =
                            object : GridLayoutManager.SpanSizeLookup() {
                                override fun getSpanSize(position: Int) = when (position) {
                                    0 -> GRID_SPAN_COUNT
                                    else -> LIST_SPAN_COUNT
                                }
                            }
                    }
                } else {
                    binding.rvMembers.layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
                }
                adapter.submitList(records)
            } else {
                findNavController().navigate(R.id.addHealthRecordsFragment)
            }
        }
    }

    private fun setupToolBar() {
        binding.toolbar.ivRightOption.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }
}
