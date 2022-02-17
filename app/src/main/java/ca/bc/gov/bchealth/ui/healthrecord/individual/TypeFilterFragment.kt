package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTypeFilterBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TypeFilterFragment : Fragment(R.layout.fragment_type_filter) {

    private val binding by viewBindings(FragmentTypeFilterBinding::bind)
    private val timelineSharedViewModel: TimelineSharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {

        setupToolbar()

        binding.checkBoxImmunisation.setOnCheckedChangeListener { _, checked ->
            timelineSharedViewModel.isImmunisationChecked = checked
        }

        binding.checkBoxMedication.setOnCheckedChangeListener { _, checked ->
            timelineSharedViewModel.isMedicationChecked = checked
        }

        binding.checkBoxCovidTestResult.setOnCheckedChangeListener { _, checked ->
            timelineSharedViewModel.isCovid19Checked = checked
        }

        binding.btnShowResults.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.filter_by_type)
            line1.visibility = View.VISIBLE

            binding.toolbar.tvRightOption.apply {
                visibility = View.VISIBLE
                text = context.getString(R.string.clear)
                setOnClickListener {
                    binding.checkBoxImmunisation.isChecked = false
                    binding.checkBoxMedication.isChecked = false
                    binding.checkBoxCovidTestResult.isChecked = false
                }
            }
        }
    }
}
