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
import androidx.transition.Scene
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthPassFragment : Fragment(R.layout.fragment_my_cards) {

    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(FragmentMyCardsBinding::bind)
    private lateinit var healthPassAdapter: HealthPassAdapter
    private lateinit var sceneSingleHealthPass: Scene
    private lateinit var sceneNoCardPlaceHolder: Scene

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneSingleHealthPass = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_mycards_single_card,
            requireContext()
        )

        sceneNoCardPlaceHolder = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_mycards_add_card,
            requireContext()
        )

        healthPassAdapter = HealthPassAdapter(emptyList(), qrCodeClickListener = {
            val action = HealthPassFragmentDirections.actionHealthPassFragmentToExpandQRFragment(it)
            findNavController().navigate(action)
        })

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.visibility =
                        if (state.isLoading) View.VISIBLE else View.GONE

                    if (!state.healthPasses.isNullOrEmpty()) {
                        showHealthPasses(healthPasses = state.healthPasses)
                    } else {
                        showNoCardPlaceHolder()
                    }
                }
            }
        }

        viewModel.loadHealthPasses()
    }

    private fun showHealthPasses(healthPasses: List<HealthPass>) {
        sceneSingleHealthPass.enter()
        if (::healthPassAdapter.isInitialized) {
            healthPassAdapter.healthPasses = healthPasses
            healthPassAdapter.notifyDataSetChanged()
        }
        val recHealthPasses: RecyclerView =
            sceneSingleHealthPass.sceneRoot.findViewById(R.id.rec_cards_list)
        recHealthPasses.adapter = healthPassAdapter
        recHealthPasses.layoutManager = LinearLayoutManager(requireContext())
        val txtNumberOfPasses: MaterialTextView =
            sceneSingleHealthPass.sceneRoot.findViewById(R.id.tv_number_of_passes)
        val numberOfPasses = "${healthPasses.size} passes"
        txtNumberOfPasses.text = numberOfPasses
        val btnViewAll: MaterialButton =
            sceneSingleHealthPass.sceneRoot.findViewById(R.id.btn_view_all)
        btnViewAll.setOnClickListener {
            navigateToViewAllHealthPasses()
        }
        btnViewAll.visibility = if (healthPasses.size > 1) View.VISIBLE else View.GONE
    }

    private fun showNoCardPlaceHolder() {
        sceneNoCardPlaceHolder.enter()
        val btnAddCardOptions: MaterialButton =
            sceneNoCardPlaceHolder.sceneRoot.findViewById(R.id.btn_add_card)
        btnAddCardOptions.setOnClickListener {
            findNavController().navigate(R.id.addCardOptionFragment)
        }
    }

    private fun navigateToViewAllHealthPasses() {
        findNavController().navigate(R.id.action_healthPassFragment_to_healthPassesFragment)
    }
}