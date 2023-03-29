package ca.bc.gov.bchealth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
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
import ca.bc.gov.bchealth.utils.HEALTH_GATEWAY_EMAIL_ADDRESS
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import kotlinx.coroutines.flow.StateFlow

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
        getBaseViewModel()?.baseUiState?.collectOnStart(::handleBaseUiState)
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

    fun <T> StateFlow<T>.collectOnStart(action: ((T) -> Unit)) {
        launchOnStart {
            this@collectOnStart.collect { state ->
                action.invoke(state)
            }
        }
    }

    protected fun navigate(@IdRes screenId: Int, args: Bundle? = null) {
        findNavController().navigate(screenId, args)
    }

    fun composeEmail(address: String = HEALTH_GATEWAY_EMAIL_ADDRESS, subject: String = "") {
        (activity as? BaseActivity)?.composeEmail(address, subject)
    }

    fun showGenericError() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error),
            msg = getString(R.string.error_message),
            positiveBtnMsg = getString(R.string.dialog_button_ok)
        )
    }

    fun popNavigation() {
        findNavController().popBackStack()
    }

    fun setupComposeToolbar(composeView: ComposeView, title: String? = null) {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyHealthTheme {
                    MyHealthToolbar(title) {
                        popNavigation()
                    }
                }
            }
        }
    }

    private fun resetBaseUiState() = getBaseViewModel()?.resetBaseUiState()

    private fun showServiceDownMessage() {
        view?.let {
            it.showServiceDownMessage(it.context)
        }
    }

    private fun showNoInternetConnectionMessage() {
        view?.let {
            it.showNoInternetConnectionMessage(it.context)
        }
    }

    private fun getAppBarConfiguration() = AppBarConfiguration(
        setOf(
            R.id.homeFragment,
            R.id.healthPassFragment,
            R.id.individualHealthRecordFragment,
            R.id.dependentsFragment
        ),
        null
    )
}
