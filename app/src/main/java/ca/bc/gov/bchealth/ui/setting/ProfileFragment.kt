package ca.bc.gov.bchealth.ui.setting

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentProfileBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
* @auther amit_metri on 07,January,2022
*/
@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBindings(FragmentProfileBinding::bind)

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolBar()

        initUi()
    }

    private fun showLoader(value: Boolean) {
        binding.tvPrivacyStatement.isClickable = !value
        binding.tvDataSecurity.isClickable = !value
        binding.progressBar.isVisible = value
    }

    private fun initUi() {

        showLoader(true)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isLoggedIn.collect {
                    it?.let {
                        showLoader(false)

                        initClickListeners()

                        binding.layoutProfile.isVisible = it
                        binding.layoutLogin.isVisible = !it
                    }
                }
            }
        }

        viewModel.checkProfile()
    }

    private fun initClickListeners() {

        binding.apply {

            tvDataSecurity.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
            }

            tvPrivacyStatement.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_privacy_policy))
            }

            btnLogin.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("destinationId", R.id.profileFragment)

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.profileFragment, true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build()

                findNavController().navigate(R.id.loginFragment, bundle, navOptions)
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
}
