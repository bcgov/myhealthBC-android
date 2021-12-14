package ca.bc.gov.bchealth.ui.healthpass

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.SceneMycardsCardsListBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthPassesFragment : Fragment(R.layout.scene_mycards_cards_list) {

    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(SceneMycardsCardsListBinding::bind)
    private lateinit var healthPassAdapter: HealthPassAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar setup
        val toolBar: MaterialToolbar = view.findViewById(R.id.toolbar)
        val backButton: ShapeableImageView = toolBar.findViewById(R.id.iv_left_option)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        val titleText = toolBar.findViewById<TextView>(R.id.tv_title)
        titleText.visibility = View.VISIBLE
        titleText.text = getString(R.string.bc_vaccine_passes)
        val tvEdit = toolBar.findViewById<TextView>(R.id.tv_right_option)
        tvEdit.text = getString(R.string.edit)
        tvEdit.visibility = View.VISIBLE
        tvEdit.setOnClickListener {
            findNavController().navigate(R.id.action_healthPassesFragment_to_manageHealthPassFragment)
        }

        healthPassAdapter = HealthPassAdapter(emptyList(), qrCodeClickListener = {
            val action =
                HealthPassesFragmentDirections.actionHealthPassesFragmentToExpandQRFragment(it)
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
            if (::healthPassAdapter.isInitialized) {
                healthPassAdapter.healthPasses = healthPasses
                healthPassAdapter.notifyDataSetChanged()
            }
        }
    }
}