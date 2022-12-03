package ca.bc.gov.bchealth.ui.dependents.profile

import android.os.Bundle
import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentProfileBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentProfileFragment : BaseFragment(R.layout.fragment_dependent_profile) {
    private val args: DependentProfileFragmentArgs by navArgs()
    private val viewModel: DependentProfileViewModel by viewModels()

    private val binding by viewBindings(FragmentDependentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patientId = args.patientId
        viewModel.loadInformation(patientId)

        binding.btnRemove.setOnClickListener { viewModel.removeDependent(patientId) }

        launchOnStart {
            viewModel.uiState.collect { uiState ->
                binding.viewLoading.root.toggleVisibility(uiState.isLoading)
                uiState.error?.let { showGenericError() }

                binding.tvFullName.text = uiState.dependentName

                if (uiState.dependentInfo.isNotEmpty()) {
                    binding.composeBody.apply {
                        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                        setContent {
                            MaterialTheme {
                                DependentProfileUI(uiState.dependentInfo)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showGenericError() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error),
            msg = getString(R.string.error_message),
            positiveBtnMsg = getString(R.string.dialog_button_ok)
        )
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setupWithNavController(findNavController(), appBarConfiguration)
            setNavigationIcon(R.drawable.ic_toolbar_back)
            title = getString(R.string.profile_settings)
        }
    }
}
