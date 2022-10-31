package ca.bc.gov.bchealth.ui.dependents.records

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentRecordsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings

class DependentRecordsFragment : BaseFragment(R.layout.fragment_dependent_records) {
    private val binding by viewBindings(FragmentDependentRecordsBinding::bind)
    private val args: DependentRecordsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args.fullName ?: throw java.lang.RuntimeException("fullname required")
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        binding.layoutToolbar.apply {
            toolbar.stateListAnimator = null
            toolbar.elevation = 0f

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.text = args.fullName
        }
    }
}
