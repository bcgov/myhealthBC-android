package ca.bc.gov.bchealth.ui.healthrecord.add

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddHealthRecordsBinding
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordFragment
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordOptionAdapter
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment
import ca.bc.gov.bchealth.ui.healthrecord.NavigationAction
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddHealthRecordsFragment : Fragment(R.layout.fragment_add_health_records) {
    private val binding by viewBindings(FragmentAddHealthRecordsBinding::bind)
    private lateinit var optionsAdapter: HealthRecordOptionAdapter
    private val viewModel: AddHealthRecordsOptionsViewModel by viewModels()
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val args: AddHealthRecordsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_BACK
                        )
                    findNavController().popBackStack()
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        observeBcscLogin()

        bcscAuthViewModel.checkSession()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner
        ) { recordId ->
            if (recordId > 0) {
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS, recordId)
                findNavController().popBackStack()
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner
        ) {
            if (it > 0) {
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS, it)
                findNavController().popBackStack()
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BcscAuthState>(
            BcscAuthFragment.BCSC_AUTH_STATUS
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.remove<BcscAuthState>(BcscAuthFragment.BCSC_AUTH_STATUS)
            it?.let {
                when (it) {
                    BcscAuthState.SUCCESS -> {
                        findNavController().previousBackStackEntry?.savedStateHandle
                            ?.set(FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS, 1L)
                        findNavController().popBackStack()
                    }
                    BcscAuthState.NOT_NOW,
                    BcscAuthState.NO_ACTION -> {
                        // no implementation required
                    }
                }
            }
        }

        optionsAdapter = HealthRecordOptionAdapter {
            when (it) {
                OptionType.VACCINE -> {
                    sharedViewModel.destinationId = R.id.fetchVaccineRecordFragment
                    findNavController().navigate(sharedViewModel.destinationId)
                }
                OptionType.TEST -> {
                    sharedViewModel.destinationId = R.id.fetchTestRecordFragment
                    findNavController().navigate(sharedViewModel.destinationId)
                }
                OptionType.LOGIN -> {
                    sharedViewModel.destinationId = 0
                    findNavController().navigate(R.id.bcscAuthInfoFragment)
                }
            }
        }
        binding.rvMembers.adapter = optionsAdapter
        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeBcscLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect {

                    binding.progressBar.isVisible = it.showLoading

                    if (it.showLoading) {
                        return@collect
                    } else {
                        if (it.loginStatus == LoginStatus.EXPIRED) {
                            val list = emptyList<HealthRecordOption>().toMutableList()
                            list.addAll(viewModel.getLoginOption())
                            list.addAll(viewModel.getHealthRecordOption())
                            optionsAdapter.submitList(list)
                        } else {
                            optionsAdapter.submitList(viewModel.getHealthRecordOption())
                        }
                    }
                }
            }
        }
    }

    private fun setupToolBar() {
        if (args.isBackButtonEnabled) {
            binding.toolbar.ivLeftOption.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            binding.toolbar.tvTitle.apply {
                visibility = View.VISIBLE
                setText(R.string.add_health_record)
            }
        }
        binding.toolbar.ivRightOption.apply {
            isVisible = !args.isBackButtonEnabled
            setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }
}
