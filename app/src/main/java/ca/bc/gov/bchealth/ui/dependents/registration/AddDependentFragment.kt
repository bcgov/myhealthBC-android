package ca.bc.gov.bchealth.ui.dependents.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddDependentBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.PhnHelper
import ca.bc.gov.bchealth.utils.hideKeyboard
import ca.bc.gov.bchealth.utils.validateEmptyInputLayout
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddDependentFragment : BaseFragment(R.layout.fragment_add_dependent) {
    private val binding by viewBindings(FragmentAddDependentBinding::bind)
    private val viewModel: AddDependentsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDobUI()
        setUpRegistrationButton()
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setUpRegistrationButton() = with(binding) {
        btnRegister.setOnClickListener {
            context?.hideKeyboard(it)
            binding.scrollView.clearFocus()

            if (validateInputFields()) {
                viewModel.registerDependent(
                    firstName = etFirstName.text.toString(),
                    lastName = etLastName.text.toString(),
                    dob = etDob.text.toString(),
                    phn = etPhn.text.toString(),
                )
            }
        }
    }

    private fun validateInputFields(): Boolean {
        val isDobValid = DatePickerHelper().validateDatePickerData(
            binding.tilDob, R.string.dob_required
        )
        val isFirstNameValid = binding.tilFirstName.validateEmptyInputLayout(
            R.string.dependents_registration_first_name_error
        )
        val isLastNameValid = binding.tilLastName.validateEmptyInputLayout(
            R.string.dependents_registration_given_last_name_error
        )
        val isPhnValid = PhnHelper().validatePhnData(binding.tilPhn)

        val isChecked = binding.checkboxRemember.isChecked

        return isDobValid && isFirstNameValid && isLastNameValid && isPhnValid && isChecked
    }

    private fun setUpDobUI() {
        DatePickerHelper().initializeDatePicker(
            binding.tilDob,
            R.string.enter_dob,
            parentFragmentManager,
            "DATE_OF_BIRTH"
        )
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.dependents_registration_title)
        }
    }
}
