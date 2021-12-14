package ca.bc.gov.bchealth.ui.healthrecord.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.SceneHealthRecordsAddBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddHealthRecordsFragment : Fragment(R.layout.scene_health_records_add) {
    private val binding by viewBindings(SceneHealthRecordsAddBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vgGetCovidTestResults.setOnClickListener {
            findNavController().navigate(R.id.fetchTestRecordFragment)
        }

        binding.vgGetVaccinationRecords.setOnClickListener {
            findNavController().navigate(R.id.fetchVaccineRecordFragment)
        }
    }
}