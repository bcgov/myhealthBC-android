package ca.bc.gov.bchealth.ui.healthpass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.SceneMycardsSingleCardBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthPassFragment : Fragment(R.layout.scene_mycards_single_card) {

    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(SceneMycardsSingleCardBinding::bind)
    private lateinit var healthPassAdapter: HealthPassAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnViewAll.setOnClickListener {
            navigateToViewAllHealthPasses()
        }

        binding.ivAddCard.setOnClickListener {
            findNavController().navigate(R.id.addCardOptionFragment)
        }

        healthPassAdapter = HealthPassAdapter(emptyList(), qrCodeClickListener = {
            val action = HealthPassFragmentDirections.actionHealthPassFragmentToExpandQRFragment(it)
            findNavController().navigate(action)
        })
        binding.recCardsList.adapter = healthPassAdapter
        binding.recCardsList.layoutManager =
            LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectHealthPasses()
            }
        }
    }

    private suspend fun collectHealthPasses() {
        viewModel.healthPasses.collect { healthPasses ->

            binding.btnViewAll.visibility = if (healthPasses.size > 1) View.VISIBLE else View.GONE
            binding.tvNumberOfPasses.visibility =
                if (healthPasses.size > 1) View.VISIBLE else View.GONE

            if (::healthPassAdapter.isInitialized) {
                healthPassAdapter.healthPasses = healthPasses
                healthPassAdapter.notifyDataSetChanged()
            }
            val numberOfPasses = "${healthPasses.size} passes"
            binding.tvNumberOfPasses.text = numberOfPasses
        }
    }

    private fun navigateToViewAllHealthPasses() {
        findNavController().navigate(R.id.action_healthPassFragment_to_healthPassesFragment)
    }
}