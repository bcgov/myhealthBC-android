package ca.bc.gov.bchealth.ui.login

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentLoginInfoBinding
import ca.bc.gov.bchealth.repository.Response
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
* @auther amit_metri on 04,January,2022
*/
@AndroidEntryPoint
class LoginInfoFragment : Fragment(R.layout.fragment_login_info) {

    private val binding by viewBindings(FragmentLoginInfoBinding::bind)

    private val viewModel: LoginInfoViewModel by viewModels()

    private val args: LoginInfoFragmentArgs by navArgs()

    private var destinationId: Int = 0

    private var authResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        viewModel.processAuthResponse(activityResult)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        destinationId = args.destinationId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        initUI()
    }

    private fun setupToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.go_to_health_gateway)

            line1.visibility = View.VISIBLE
        }
    }

    private fun initUI() {

        binding.tvLoginInfoMessage.text = Html.fromHtml(
            getString(R.string.login_info_message), Html.FROM_HTML_MODE_LEGACY
        )

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnContinue.setOnClickListener {
            viewModel.initiateLogin(authResultLauncher, requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiStateSharedFlow.collect {
                    when (it) {
                        is Response.Success -> {
                            respondToSuccess()
                        }
                        is Response.Error -> {
                            respondToError(it)
                        }
                        is Response.Loading -> {
                            showLoader(true)
                        }
                    }
                }
            }
        }
    }

    private fun respondToSuccess() {

        showLoader(false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .build()

        if (destinationId > 0)
            findNavController().navigate(destinationId, null, navOptions)
    }

    private fun respondToError(it: Response.Error<String>) {

        showLoader(false)

        requireContext().showError(
            it.errorData?.errorTitle.toString(),
            it.errorData?.errorMessage.toString()
        )
    }

    private fun showLoader(value: Boolean) {

        binding.btnContinue.isEnabled = !value

        if (value)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.INVISIBLE
    }
}
