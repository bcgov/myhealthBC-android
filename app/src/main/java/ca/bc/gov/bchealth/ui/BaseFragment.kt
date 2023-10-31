package ca.bc.gov.bchealth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.custom.MyHealthToolbar
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.launchAndRepeatWithLifecycle
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage

// todo: Create open fun to get contentLayoutId.
// Hilt doesn't support default value for constructor
abstract class BaseFragment(@LayoutRes private val contentLayoutId: Int?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = contentLayoutId?.let {
        inflater.inflate(it, container, false)
    } ?: getComposeView()

    private fun getComposeView() = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent { GetComposableLayout() }
    }

    @Composable
    open fun GetComposableLayout() {
    }

    open fun getBaseViewModel(): BaseViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getAppBarConfiguration())

        launchAndRepeatWithLifecycle {
            getBaseViewModel()?.baseUiState?.collect {
                handleBaseUiState(it)
            }
        }
    }

    private fun handleBaseUiState(baseUiState: BaseUiState) = baseUiState.apply {
        if (connected.not()) {
            showNoInternetConnectionMessage()
            resetBaseUiState()
        } else if (serviceUp.not()) {
            showServiceDownMessage()
            resetBaseUiState()
        }
    }

    open fun setToolBar(appBarConfiguration: AppBarConfiguration) {}

    fun showGenericError() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error),
            msg = getString(R.string.error_message),
            positiveBtnMsg = getString(R.string.dialog_button_ok)
        )
    }

    fun setupComposeToolbar(composeView: ComposeView, title: String? = null) {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyHealthTheme {
                    MyHealthToolbar(title) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun resetBaseUiState() = getBaseViewModel()?.resetBaseUiState()

    fun showServiceDownMessage() {
        view?.let {
            it.showServiceDownMessage(it.context)
        }
    }

    fun showNoInternetConnectionMessage() {
        view?.let {
            it.showNoInternetConnectionMessage(it.context)
        }
    }

    private fun getAppBarConfiguration() = AppBarConfiguration(
        setOf(
            R.id.homeFragment,
            R.id.healthPassFragment,
            R.id.healthRecordFragment,
            R.id.dependentsFragment
        ),
        null
    )
}
