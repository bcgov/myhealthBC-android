package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentProtectiveWordBinding
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProtectiveWordFragment : Fragment(R.layout.fragment_protective_word) {

    private val binding by viewBindings(FragmentProtectiveWordBinding::bind)
    private val viewModel: ProtectiveWordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        binding.btnContinue.setOnClickListener {
            if(viewModel.isProtectiveWordValid(binding.etProtectiveWord.text.toString())) {
                viewModel.saveProtectiveWord(binding.etProtectiveWord.text.toString())
                // viewModel.clearIsProtectiveWordRequired()
                findNavController().popBackStack()
            } else {
                binding.etProtectiveWord.error = "Invalid protective word. Try again."
            }
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.tvTitle.show()
        binding.toolbar.tvTitle.text = "Restricted PharmaNet Records"
        binding.toolbar.ivLeftOption.apply {
            this.show()
            setImageResource(R.drawable.ic_scanner_close)
            setColorFilter(ContextCompat.getColor(context, R.color.primary_blue))
            setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}