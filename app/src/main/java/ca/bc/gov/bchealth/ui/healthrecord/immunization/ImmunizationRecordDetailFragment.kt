package ca.bc.gov.bchealth.ui.healthrecord.immunization

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
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentImmunizationRecordDetailBinding
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.VaccineDoseDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant

@AndroidEntryPoint
class ImmunizationRecordDetailFragment : Fragment(R.layout.fragment_immunization_record_detail) {

    private val binding by viewBindings(FragmentImmunizationRecordDetailBinding::bind)
    private val viewModel: ImmunizationRecordDetailViewModel by viewModels()
    private lateinit var adapter: ImmunizationDetailsAdapter
    private val args: ImmunizationRecordDetailFragmentArgs by navArgs()

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

            line1.visibility = View.VISIBLE
        }
    }

    private fun initUI() {

        setUpRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    adapter.submitList(
                        listOf(
                            VaccineDoseDto(
                                123, 123, "Test product", "provider name", "lot number",
                                Instant.now()
                            )
                        )
                    )
                }
            }
        }

        viewModel.getImmunizationRecordDetails(args.patientId)
    }

    private fun setUpRecyclerView() {

        adapter = ImmunizationDetailsAdapter()
        binding.rvImmunizationList.adapter = adapter
    }
}