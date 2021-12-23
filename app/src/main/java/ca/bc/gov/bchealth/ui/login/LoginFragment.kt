package ca.bc.gov.bchealth.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentLoginBinding
import ca.bc.gov.bchealth.utils.viewBindings

/*
* @auther amit_metri on 04,January,2022
*/
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBindings(FragmentLoginBinding::bind)

    private var destinationId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt("destinationId")?.let {
            destinationId = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (destinationId > 0) {
            binding.btnNotNow.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build()
                findNavController().navigate(destinationId, null, navOptions)
            }
        }

        binding.btnLogin.setOnClickListener {
            if (destinationId > 0) {
                val action = LoginFragmentDirections
                    .actionLoginFragmentToLoginInfoFragment(destinationId)
                findNavController().navigate(action)
            }
        }
    }
}
