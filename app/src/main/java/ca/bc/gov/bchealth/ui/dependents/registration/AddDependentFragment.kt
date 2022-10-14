package ca.bc.gov.bchealth.ui.dependents.registration

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddDependentBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddDependentFragment : BaseFragment(R.layout.fragment_add_dependent) {
    private val binding by viewBindings(FragmentAddDependentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        setUpDobUI()
    }

    private fun setUpDobUI() {
        DatePickerHelper().initializeDatePicker(
            binding.tilDob,
            getString(R.string.enter_dob),
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
