package ca.bc.gov.bchealth.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBcscAuthInfoBinding
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment.Companion.BCSC_AUTH_STATUS
import ca.bc.gov.bchealth.utils.makeLinks
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings

/*
* @auther amit_metri on 04,January,2022
*/
class BcscAuthInfoFragment : Fragment(R.layout.fragment_bcsc_auth_info) {

    private val binding by viewBindings(FragmentBcscAuthInfoBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BcscAuthState>(
            BCSC_AUTH_STATUS
        )?.observe(viewLifecycleOwner) {
            if (it == BcscAuthState.SUCCESS) {
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(BCSC_AUTH_STATUS, BcscAuthState.SUCCESS)
                findNavController().popBackStack()
            }
        }

        binding.btnNotNow.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle
                ?.set(BCSC_AUTH_STATUS, BcscAuthState.NOT_NOW)
            findNavController().popBackStack()
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_bcscAuthInfoFragment_to_bcscAuthFragment)
        }

        binding.tvDownloadApp.makeLinks(
            Pair(
                getString(R.string.download_small_case),
                View.OnClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store)))
                        startActivity(intent)
                    } catch (e: Exception) {
                        requireContext().redirect(getString(R.string.play_store))
                    }
                }
            )
        )
    }
}
