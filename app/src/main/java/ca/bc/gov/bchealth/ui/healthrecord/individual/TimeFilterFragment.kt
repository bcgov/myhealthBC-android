package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTimeFilterBinding
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeFilterFragment : Fragment(R.layout.fragment_time_filter) {

    private val binding by viewBindings(FragmentTimeFilterBinding::bind)
    private val timelineSharedViewModel: TimelineSharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {

        setupToolbar()

        setupDateFields()

        binding.checkBox2022.setOnCheckedChangeListener { _, checked ->
            timelineSharedViewModel.is2022Checked = checked
            clearDateFields(checked)
        }

        binding.checkBox2021.setOnCheckedChangeListener { _, checked ->
            timelineSharedViewModel.is2021Checked = checked
            clearDateFields(checked)
        }

        binding.checkBox2020.setOnCheckedChangeListener { _, checked ->
            timelineSharedViewModel.is2020Checked = checked
            clearDateFields(checked)
        }

        binding.checkBoxBefore2020.setOnCheckedChangeListener { _, checked ->
            timelineSharedViewModel.isBefore2020Checked = checked
            clearDateFields(checked)
        }

        binding.btnShowResults.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupDateFields() {
        DatePickerHelper().initializeDatePicker(
            binding.tipFrom,
            getString(R.string.select_date),
            parentFragmentManager,
            "DATE_FROM",
        )

        binding.tipFrom.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not required
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not required
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    clearCheckBoxes()
                }
            }
        })

        DatePickerHelper().initializeDatePicker(
            binding.tipTo,
            getString(R.string.select_date),
            parentFragmentManager,
            "DATE_TO",
        )

        binding.tipTo.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not required
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not required
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    clearCheckBoxes()
                }
            }
        })
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
                    clearAllFilters()
                }
            }
        }
    }

    private fun clearCheckBoxes() {
        binding.apply {
            checkBox2022.isChecked = false
            checkBox2021.isChecked = false
            checkBox2020.isChecked = false
            checkBoxBefore2020.isChecked = false
        }
    }

    private fun clearDateFields(clear: Boolean) {
        if (clear) {
            binding.apply {
                tipFrom.editText?.text?.clear()
                tipFrom.clearFocus()
                tipTo.editText?.text?.clear()
                tipTo.clearFocus()
            }
        }
    }

    private fun clearAllFilters() {
        binding.apply {
            clearCheckBoxes()
            clearDateFields(true)
        }
    }
}
