package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentNewFeatureBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewFeatureFragment : Fragment(R.layout.fragment_new_feature) {

    private val binding by viewBindings(FragmentNewFeatureBinding::bind)

    private val viewModel: NewFeatureViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOk.setOnClickListener {

            showLoader()

            viewModel.setNewFeatureShown(true).invokeOnCompletion {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.newFeatureFragment, true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build()
                viewModel.checkLogin(R.id.myCardsFragment, navOptions, findNavController())
            }
        }
    }

    private fun showLoader() {
        binding.btnOk.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
    }
}
