package ca.bc.gov.bchealth.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBiometricSecurityTipBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiometricSecurityTipFragment : Fragment(R.layout.fragment_biometric_security_tip) {

    private val binding by viewBindings(FragmentBiometricSecurityTipBinding::bind)
    private lateinit var biometricSecurityTipAdapter: BiometricSecurityTipAdapter
    private val viewModel: BiometricSecurityTipViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        biometricSecurityTipAdapter = BiometricSecurityTipAdapter()
        binding.recSecurityTip.adapter = biometricSecurityTipAdapter
        binding.recSecurityTip.layoutManager = LinearLayoutManager(requireContext())
        biometricSecurityTipAdapter.submitList(viewModel.getSecurityTipList().toMutableList())
    }

    private fun setupToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.biometric_security_tip_title)

            line1.visibility = View.VISIBLE
        }
    }
}
