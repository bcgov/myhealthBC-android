package ca.bc.gov.bchealth.ui.healthrecord

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHealthRecordsBinding
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
    private lateinit var adapter: HealthRecordsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HealthRecordsAdapter {
            val action =
                HealthRecordsFragmentDirections
                    .actionHealthRecordsFragmentToIndividualHealthRecordFragment(
                        it.patientId,
                        it.name
                    )
            findNavController().navigate(action)
        }

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

    private fun initUi() {
        binding.ivAddHealthRecord.visibility = View.VISIBLE
        binding.tvTitle1.visibility = View.VISIBLE
        binding.tvMessage.visibility = View.VISIBLE
        setupToolBar()
    }

    private suspend fun collectHealthRecordsFlow() {
        viewModel.patientHealthRecords.collect { records ->
            binding.progressBar.isVisible = false
            if (records.isNotEmpty()) {
                if (records.size == 1) {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.healthRecordsFragment, true)
                        .build()

                    val action =
                        HealthRecordsFragmentDirections
                            .actionHealthRecordsFragmentToIndividualHealthRecordFragment(
                                records.first().patientId,
                                records.first().name
                            )
                    findNavController().navigate(action, navOptions)
                } else {
                    initUi()
                    binding.rvMembers.adapter = adapter
                    binding.rvMembers.layoutManager = GridLayoutManager(requireContext(), 2)
                    adapter.submitList(records)
                }
            } else {
                findNavController().navigate(R.id.addHealthRecordsFragment)
            }
        }
    }

    private fun setupToolBar() {
        binding.toolbar.ivRightOption.apply {
            isVisible = true
            setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }
}
