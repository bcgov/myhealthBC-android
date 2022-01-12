package ca.bc.gov.bchealth.ui.healthrecord.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHealthRecordsBinding
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordOptionAdapter
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddHealthRecordsFragment : Fragment(R.layout.fragment_health_records) {
    private val binding by viewBindings(FragmentHealthRecordsBinding::bind)
    private lateinit var optionsAdapter: HealthRecordOptionAdapter
    private val viewModel: AddHealthRecordsOptionsViewModel by viewModels()

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

        binding.rvMembers.adapter = optionsAdapter
        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
        optionsAdapter.submitList(
            viewModel.getHealthRecordOption().toMutableList()
        )
    }
}