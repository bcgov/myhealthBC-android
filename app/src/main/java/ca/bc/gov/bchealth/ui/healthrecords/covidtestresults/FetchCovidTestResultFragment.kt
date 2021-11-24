package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchCovidTestResultBinding
import ca.bc.gov.bchealth.utils.adjustOffset
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FetchCovidTestResultFragment : Fragment(R.layout.fragment_fetch_covid_test_result) {

    private val binding by viewBindings(FragmentFetchCovidTestResultBinding::bind)

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

    private val viewModel: FetchCovidTestResultViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        iniUI()
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_covid_test_result)

            ivRightOption.visibility = View.VISIBLE
            ivRightOption.setImageResource(R.drawable.ic_help)
            ivRightOption.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_help))
            }
            ivRightOption.contentDescription = getString(R.string.help)

            line1.visibility = View.VISIBLE
        }
    }

    private fun iniUI() {

        setUpPhnUI()

        setUpDobUI()

        setUpDovUI()

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /*
     * Fetch saved form data
     * */
    private fun setUpPhnUI() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRecentFormData.collect {
                    if (it.isNotEmpty()) {

                        val pair = Pair(
                            it.subSequence(0, 10),
                            it.subSequence(10, 20)
                        )

                        val phnArray = arrayOf(pair.first.toString())

                        val adapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            phnArray
                        )

                        val textView = binding.edPhnNumber.editText as AutoCompleteTextView
                        textView.setAdapter(adapter)
                        textView.onItemClickListener =
                            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                                binding.edDob.editText?.setText(pair.second.toString())
                                binding.edDov.editText?.requestFocus()
                            }

                        binding.edPhnNumber.setEndIconDrawable(R.drawable.ic_arrow_down)
                        binding.edPhnNumber.setEndIconOnClickListener {
                            textView.showDropDown()
                        }
                    }
                }
            }
        }
    }

    private fun setUpDobUI() {
        val dateOfBirthPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        binding.edDob.editText?.setOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, "DATE_OF_BIRTH")
        }
        binding.edDob.setEndIconOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, "DATE_OF_BIRTH")
        }
        dateOfBirthPicker.addOnPositiveButtonClickListener {
            binding.edDob.editText
                ?.setText(simpleDateFormat.format(it.adjustOffset()))
        }
    }

    private fun setUpDovUI() {
        val dateOfVaccinationPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        binding.edDov.editText?.setOnClickListener {
            dateOfVaccinationPicker.show(parentFragmentManager, "DATE_OF_VACCINATION")
        }
        binding.edDov.setEndIconOnClickListener {
            dateOfVaccinationPicker.show(parentFragmentManager, "DATE_OF_VACCINATION")
        }
        dateOfVaccinationPicker.addOnPositiveButtonClickListener {
            binding.edDov.editText?.setText(simpleDateFormat.format(it.adjustOffset()))
        }
    }
}
