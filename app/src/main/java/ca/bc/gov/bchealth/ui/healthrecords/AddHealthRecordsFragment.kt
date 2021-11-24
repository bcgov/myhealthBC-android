package ca.bc.gov.bchealth.ui.healthrecords

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddHealthRecordsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddHealthRecordsFragment : Fragment(R.layout.fragment_add_health_records) {

    private val binding by viewBindings(FragmentAddHealthRecordsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vgGetVaccinationRecords.setOnClickListener {
            findNavController()
                .navigate(R.id.action_addHealthRecordsFragment_to_fetchVaccineRecordFragment)
        }

        binding.vgGetCovidTestResults.setOnClickListener {
            findNavController()
                .navigate(R.id.action_addHealthRecordsFragment_to_fetchCovidTestResultFragment)
        }
    }
}
