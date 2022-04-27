package ca.bc.gov.bchealth.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBiometricSecurityTipBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiometricSecurityTipFragment : BaseFragment(R.layout.fragment_biometric_security_tip) {

    private val binding by viewBindings(FragmentBiometricSecurityTipBinding::bind)
    private lateinit var biometricSecurityTipAdapter: BiometricSecurityTipAdapter
    private val viewModel: BiometricSecurityTipViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        biometricSecurityTipAdapter = BiometricSecurityTipAdapter()
        binding.recSecurityTip.adapter = biometricSecurityTipAdapter
        binding.recSecurityTip.layoutManager = LinearLayoutManager(requireContext())
        biometricSecurityTipAdapter.submitList(viewModel.getSecurityTipList().toMutableList())
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.biometric_security_tip_title)
        }
    }
}
