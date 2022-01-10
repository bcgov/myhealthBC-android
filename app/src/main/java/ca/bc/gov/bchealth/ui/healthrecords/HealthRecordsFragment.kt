package ca.bc.gov.bchealth.ui.healthrecords

import android.graphics.Rect
import android.os.Bundle
import android.transition.Scene
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHealthRecordsBinding
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.ui.login.LoginViewModel
import ca.bc.gov.bchealth.utils.getNavOptions
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HealthRecordsFragment : Fragment(R.layout.fragment_health_records) {

    private val binding by viewBindings(FragmentHealthRecordsBinding::bind)

    private val viewModel: HealthRecordsViewModel by viewModels()

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var healthRecordsAdapter: HealthRecordsAdapter

    private lateinit var sceneAddHealthRecords: Scene

    private lateinit var sceneListHealthRecords: Scene

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                            listHealthRecords(healthRecords.toMutableList())
                        } else {
                            addHealthRecords()
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

    private fun addHealthRecords() {

        sceneAddHealthRecords = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_health_records_add,
            requireContext()
        )

        sceneAddHealthRecords.enter()

        val vgGetVaccinationRecords = sceneAddHealthRecords.sceneRoot
            .findViewById<ConstraintLayout>(R.id.vg_get_vaccination_records)

        vgGetVaccinationRecords.setOnClickListener {
            it.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            loginViewModel.checkLogin(
                R.id.fetchVaccineRecordFragment,
                getNavOptions(),
                findNavController()
            )
        }

        val vgGetCovidTestResults = sceneAddHealthRecords.sceneRoot
            .findViewById<ConstraintLayout>(R.id.vg_get_covid_test_results)

        vgGetCovidTestResults.setOnClickListener {
            it.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            loginViewModel.checkLogin(
                R.id.fetchCovidTestResultFragment,
                getNavOptions(),
                findNavController()
            )
        }
    }

    private fun listHealthRecords(healthRecords: MutableList<HealthRecord>) {

        sceneListHealthRecords = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_health_records_list,
            requireContext()
        )

        sceneListHealthRecords.enter()

        sceneListHealthRecords.sceneRoot.findViewById<ImageView>(R.id.iv_add_health_record)
            .setOnClickListener {
                addHealthRecords()
            }

        setupHealthRecordsList(healthRecords)
    }

    private fun setupHealthRecordsList(members: MutableList<HealthRecord>) {

        healthRecordsAdapter = HealthRecordsAdapter(members) { healthRecord ->
            val action = HealthRecordsFragmentDirections
                .actionHealthRecordsFragmentToIndividualHealthRecordFragment(healthRecord)
            findNavController().navigate(action)
        }

        val gridLayoutManager = GridLayoutManager(context, 2)

        val spacing = resources.getDimensionPixelSize(R.dimen.space_2_x) / 2

        val rvMembers = sceneListHealthRecords.sceneRoot.findViewById<RecyclerView>(R.id.rv_members)

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
