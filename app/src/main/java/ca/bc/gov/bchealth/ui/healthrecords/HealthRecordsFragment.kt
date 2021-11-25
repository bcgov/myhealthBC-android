package ca.bc.gov.bchealth.ui.healthrecords

import android.graphics.Rect
import android.os.Bundle
import android.view.View
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
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HealthRecordsFragment : Fragment(R.layout.fragment_health_records) {

    private val binding by viewBindings(FragmentHealthRecordsBinding::bind)

    private val viewModel: HealthRecordsViewModel by viewModels()

    private lateinit var healthRecordsAdapter: HealthRecordsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivAddHealthRecord.setOnClickListener {
            findNavController()
                .navigate(R.id.action_healthRecordsFragment_to_addHealthRecordsFragment)
        }

        observeHealthRecords()
    }

    /*
    * Observe the health records
    * */
    private fun observeHealthRecords() {

        showLoader(true)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.healthRecords.collect { healthRecords ->

                    showLoader(false)

                    if (healthRecords != null) {
                        if (healthRecords.isNotEmpty()) {
                            setupRecyclerView(healthRecords.toMutableList())
                        } else {
                            navigateToAddHealthRecords()
                        }
                    }
                }
            }
        }
    }

    private fun showLoader(value: Boolean) {
        if (value)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.INVISIBLE
    }

    private fun navigateToAddHealthRecords() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.healthRecordsFragment, true)
            .build()
        findNavController().navigate(
            R.id.addHealthRecordsFragment,
            null,
            navOptions
        )
    }

    private fun setupRecyclerView(members: MutableList<HealthRecord>) {

        healthRecordsAdapter = HealthRecordsAdapter(members)

        val gridLayoutManager = GridLayoutManager(context, 2)

        val spacing = resources.getDimensionPixelSize(R.dimen.space_2_x) / 2

        binding.apply {
            rvMembers.layoutManager = gridLayoutManager
            rvMembers.adapter = healthRecordsAdapter
            rvMembers.setPadding(spacing, spacing, spacing, spacing)
            rvMembers.clipToPadding = false
            rvMembers.clipChildren = false
            with(rvMembers) {
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
    }
}
