package ca.bc.gov.bchealth.ui.healthpass.manage

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.SceneMycardsManageCardsBinding
import ca.bc.gov.bchealth.ui.healthpass.HealthPassAdapter
import ca.bc.gov.bchealth.ui.healthpass.HealthPassViewModel
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class ManageHealthPassFragment : Fragment(R.layout.scene_mycards_manage_cards) {
    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(SceneMycardsManageCardsBinding::bind)
    private lateinit var healthPassAdapter: HealthPassAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar: MaterialToolbar = view.findViewById(R.id.toolbar)
        val backButton = toolBar.findViewById<ImageView>(R.id.iv_left_option)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        val titleText = toolBar.findViewById<TextView>(R.id.tv_title)
        titleText.visibility = View.VISIBLE
        titleText.text = getString(R.string.bc_vaccine_passes)
        val tvDone = toolBar.findViewById<TextView>(R.id.tv_right_option)
        tvDone.text = getString(R.string.done)
        tvDone.visibility = View.VISIBLE
        tvDone.setOnClickListener {
            findNavController().navigate(R.id.action_manageHealthPassFragment_to_healthPassFragment)
        }
        healthPassAdapter = HealthPassAdapter(emptyList(), qrCodeClickListener = {

        })
        binding.recManageCards.adapter = healthPassAdapter
        binding.recManageCards.layoutManager =
            LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectHealthPasses()
            }
        }
    }

    private suspend fun collectHealthPasses() {
        viewModel.healthPasses.collect { healthPasses ->
            if (::healthPassAdapter.isInitialized) {
                healthPassAdapter.healthPasses = healthPasses
                healthPassAdapter.notifyDataSetChanged()
            }
        }
    }
}