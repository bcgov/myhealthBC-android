package ca.bc.gov.bchealth.ui.setting

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentProfileBinding
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* @auther amit_metri on 07,January,2022
*/
@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBindings(FragmentProfileBinding::bind)
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolBar()

        checkLogin()
    }

    private fun initClickListeners() {

        binding.apply {

            tvDataSecurity.setOnClickListener {
                findNavController().navigate(R.id.settingFragment)
            }

            tvPrivacyStatement.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_privacy_policy))
            }

            btnLogin.setOnClickListener {
                sharedViewModel.destinationId = 0
                findNavController().navigate(R.id.bcscAuthInfoFragment)
            }
        }
    }

    private fun setUpToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.profile_settings)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
            line1.isVisible = true
        }
    }

    private fun checkLogin() {
        bcscAuthViewModel.checkLogin()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect {

                    binding.progressBar.isVisible = it.showLoading
                    binding.tvPrivacyStatement.isClickable = !it.showLoading
                    binding.tvDataSecurity.isClickable = !it.showLoading
                    binding.tvFullName.text = it.userName

                    if (it.showLoading) {
                        return@collect
                    } else {
                        initClickListeners()
                        val isLoginStatusActive = it.loginStatus == LoginStatus.ACTIVE
                        binding.layoutProfile.isVisible = isLoginStatusActive
                        binding.layoutLogin.isVisible = !isLoginStatusActive
                    }
                }
            }
        }
    }
}
