package ca.bc.gov.bchealth.ui.healthrecord

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
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
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordFragment.Companion.VACCINE_RECORD_ADDED_SUCCESS
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment.Companion.PLACE_HOLDER_NAVIGATION
import ca.bc.gov.bchealth.ui.healthrecord.add.FetchTestRecordFragment.Companion.TEST_RECORD_ADDED_SUCCESS
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */

@AndroidEntryPoint
class HealthRecordsFragment : Fragment(R.layout.fragment_health_records) {

    private val binding by viewBindings(FragmentHealthRecordsBinding::bind)
    private val viewModel: HealthRecordPlaceholderViewModel by viewModels()
    private lateinit var adapter: HealthRecordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_BACK
                        )
                    findNavController().popBackStack()
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeVaccineRecordAddition()

        observeCovidTestRecordAddition()

        observeHealthRecordDeletion()

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
            val action = HealthRecordsFragmentDirections
                .actionHealthRecordsFragmentToAddHealthRecordFragment(true)
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                collectHealthRecordsFlow()
            }
        }
        viewModel.getPatientsAndHealthRecordCounts()

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

    private fun observeCovidTestRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            TEST_RECORD_ADDED_SUCCESS
        )
            ?.observe(
                viewLifecycleOwner
            ) { recordId ->
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                    TEST_RECORD_ADDED_SUCCESS
                )
                if (recordId > 0) {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_RE_CHECK
                        )
                    findNavController().popBackStack()
                }
            }
    }

    private fun observeVaccineRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            VACCINE_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner
        ) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                VACCINE_RECORD_ADDED_SUCCESS
            )
            if (it > 0) {
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(
                        PLACE_HOLDER_NAVIGATION,
                        NavigationAction.ACTION_RE_CHECK
                    )
                findNavController().popBackStack()
            }
        }
    }

    private fun observeHealthRecordDeletion() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<NavigationAction>(
            PLACE_HOLDER_NAVIGATION
        )
            ?.observe(
                viewLifecycleOwner
            ) {
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<NavigationAction>(
                    PLACE_HOLDER_NAVIGATION
                )
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(
                        PLACE_HOLDER_NAVIGATION,
                        NavigationAction.ACTION_RE_CHECK
                    )
                findNavController().popBackStack()
            }
    }

    private fun initUi() {
        binding.ivAddHealthRecord.visibility = View.VISIBLE
        binding.tvTitle1.visibility = View.VISIBLE
        binding.tvMessage.visibility = View.VISIBLE
        setupToolBar()
    }

    private suspend fun collectHealthRecordsFlow() {
        viewModel.uiState.collect { uiState ->
            binding.progressBar.isVisible = uiState.isLoading
            if (uiState.patientsAndHealthRecordCounts != null) {
                if (uiState.patientsAndHealthRecordCounts.isNotEmpty()) {
                    if (uiState.patientsAndHealthRecordCounts.size == 1) {
                        findNavController().previousBackStackEntry?.savedStateHandle
                            ?.set(
                                PLACE_HOLDER_NAVIGATION,
                                NavigationAction.ACTION_RE_CHECK
                            )
                        findNavController().popBackStack()
                    } else {
                        initUi()
                        binding.rvMembers.adapter = adapter
                        binding.rvMembers.layoutManager = GridLayoutManager(requireContext(), 2)
                        adapter.submitList(uiState.patientsAndHealthRecordCounts)
                    }
                } else {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_RE_CHECK
                        )
                    findNavController().popBackStack()
                }
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
