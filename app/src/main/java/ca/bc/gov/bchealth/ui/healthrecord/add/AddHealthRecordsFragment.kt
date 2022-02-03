package ca.bc.gov.bchealth.ui.healthrecord.add

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHealthRecordsBinding
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordFragment
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordOptionAdapter
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddHealthRecordsFragment : Fragment(R.layout.fragment_health_records) {
    private val binding by viewBindings(FragmentHealthRecordsBinding::bind)
    private lateinit var optionsAdapter: HealthRecordOptionAdapter
    private val viewModel: AddHealthRecordsOptionsViewModel by viewModels()
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS)
            ?.observe(
                viewLifecycleOwner,
                Observer { recordId ->
                    if (recordId > 0) {
                        findNavController().popBackStack()
                    }
                }
            )

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner,
            Observer {
                if (it > 0) {
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                        FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
                    )
                    findNavController().popBackStack()
                }
            }
        )

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BcscAuthState>(
            BcscAuthFragment.BCSC_AUTH_STATUS
        )?.observe(viewLifecycleOwner, {
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.remove<BcscAuthState>(BcscAuthFragment.BCSC_AUTH_STATUS)
            when (it) {
                BcscAuthState.SUCCESS -> {
                    findNavController().navigate(sharedViewModel.destinationId)
                }
                BcscAuthState.NOT_NOW -> {
                    val destinationId = sharedViewModel.destinationId
                    if (destinationId > 0) {
                        findNavController().navigate(destinationId)
                    } else {
                        findNavController().popBackStack()
                    }
                }
                else -> {}
            }
        })

        optionsAdapter = HealthRecordOptionAdapter {
            when (it) {
                OptionType.VACCINE -> {
                    sharedViewModel.destinationId = R.id.fetchVaccineRecordFragment
                }
                OptionType.TEST -> {
                    sharedViewModel.destinationId = R.id.fetchTestRecordFragment
                }
            }
            checkLogin()
        }
        binding.rvMembers.adapter = optionsAdapter
        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
        optionsAdapter.submitList(viewModel.getHealthRecordOption().toMutableList())
    }

    private fun observeBcscLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect {

                    binding.progressBar.isVisible = it.showLoading

                    if (it.showLoading) {
                        return@collect
                    } else {
                        if (it.isLoggedIn) {
                            findNavController().navigate(sharedViewModel.destinationId)
                        } else {
                            findNavController().navigate(R.id.bcscAuthInfoFragment)
                        }
                    }
                }
            }
        }
    }

    private fun setupToolBar() {
        binding.toolbar.ivRightOption.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }

    private fun checkLogin() {
        bcscAuthViewModel.checkLogin()
        observeBcscLogin()
    }
}
