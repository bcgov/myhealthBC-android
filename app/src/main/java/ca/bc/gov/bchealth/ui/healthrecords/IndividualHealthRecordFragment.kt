package ca.bc.gov.bchealth.ui.healthrecords

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IndividualHealthRecordFragment : Fragment(R.layout.fragment_individual_health_record) {

    private val binding by viewBindings(FragmentIndividualHealthRecordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()
    }

    // Toolbar setup
    private fun setupToolBar() {

        binding.toolbar.ivLeftOption.visibility = View.VISIBLE
        binding.toolbar.ivLeftOption.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.tvTitle.visibility = View.VISIBLE
        binding.toolbar.tvTitle.text = "Records" // TODO: 24/11/21 Need to prefix member name

        binding.toolbar.tvRightOption.visibility = View.VISIBLE
        binding.toolbar.tvRightOption.text = getString(R.string.edit)
        binding.toolbar.tvRightOption.setOnClickListener {
            // TODO: 24/11/21 rearrange logic
        }
    }
}
