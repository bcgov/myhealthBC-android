package ca.bc.gov.bchealth.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBcscAuthInfoBinding
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment.Companion.BCSC_AUTH_SUCCESS
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel

/*
* @auther amit_metri on 04,January,2022
*/
class BcscAuthInfoFragment : Fragment(R.layout.fragment_bcsc_auth_info) {

    private val binding by viewBindings(FragmentBcscAuthInfoBinding::bind)
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedStateHandle: SavedStateHandle =
            findNavController().currentBackStackEntry!!.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(
            BCSC_AUTH_SUCCESS
        )
            .observe(findNavController().currentBackStackEntry!!, {
                if (it) {
                    findNavController().previousBackStackEntry!!.savedStateHandle
                        .set(BCSC_AUTH_SUCCESS, true)
                    findNavController().popBackStack()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNotNow.setOnClickListener {

            val destinationId = sharedViewModel.destinationId

            if (destinationId > 0) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.bcscAuthInfoFragment, true)
                    .build()
                findNavController().navigate(destinationId, null, navOptions)
            } else {
                findNavController().popBackStack()
            }
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_bcscAuthInfoFragment_to_bcscAuthFragment)
        }
    }
}
