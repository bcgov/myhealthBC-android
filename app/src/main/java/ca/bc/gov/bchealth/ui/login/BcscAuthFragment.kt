package ca.bc.gov.bchealth.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBcscAuthBinding
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* @auther amit_metri on 04,January,2022
*/
@AndroidEntryPoint
class BcscAuthFragment : Fragment(R.layout.fragment_bcsc_auth) {

    private val binding by viewBindings(FragmentBcscAuthBinding::bind)
    private val viewModel: BcscAuthViewModel by viewModels()
    private val authResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        processAuthResponse(activityResult)
    }
    private lateinit var savedStateHandle: SavedStateHandle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(BCSC_AUTH_SUCCESS, false)
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
            viewModel.initiateLogin()

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.authStatus.collect {

                        showLoader(it.showLoading)

                        if (it.isError) {
                            respondToError()
                            viewModel.resetAuthStatus()
                        }

                        if (it.authRequestIntent != null) {
                            authResultLauncher.launch(it.authRequestIntent)
                            viewModel.resetAuthStatus()
                        }

                        if (it.isLoggedIn) {
                            respondToSuccess()
                            viewModel.resetAuthStatus()
                        }
                    }
                }
            }
        }
    }

    private fun respondToSuccess() {
        showLoginSuccessDialog()
    }

    private fun showLoginSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.login_success_title))
            .setCancelable(false)
            .setMessage(getString(R.string.login_success_message))
            .setPositiveButton(getString(R.string.ok_camel_case)) { dialog, _ ->
                savedStateHandle.set(BCSC_AUTH_SUCCESS, true)
                findNavController().popBackStack()
                dialog.dismiss()
            }
            .show()
    }

    private fun respondToError() {
        requireContext().showError(
            getString(R.string.error),
            getString(R.string.error_message),
        )
    }

    private fun showLoader(value: Boolean) {
        binding.btnContinue.isEnabled = !value
        binding.progressBar.isVisible = value
    }

    /*
    * App Auth: Process Login response
    * */
    private fun processAuthResponse(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data: Intent? = activityResult.data
            viewModel.processAuthResponse(data)
        } else {
            respondToError()
        }
    }

    companion object {
        const val BCSC_AUTH_SUCCESS = "BCSC_AUTH_SUCCESS"
    }
}
