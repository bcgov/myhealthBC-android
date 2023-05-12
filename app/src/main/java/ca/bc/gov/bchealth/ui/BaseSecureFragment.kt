package ca.bc.gov.bchealth.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.utils.observeCurrentBackStackForAction
import ca.bc.gov.bchealth.utils.removeActionFromCurrentBackStackEntry
import ca.bc.gov.bchealth.utils.setActionToPreviousBackStackEntry

/**
 * @author Pinakin Kansara
 */
abstract class BaseSecureFragment(@LayoutRes private val contentLayoutId: Int?) :
    BaseFragment(contentLayoutId) {

    companion object {
        const val NAVIGATION_ACTION = "NAVIGATION_ACTION"
        const val BCSC_AUTH = "BCSC_AUTH"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCurrentBackStackForAction<NavigationAction>(NAVIGATION_ACTION) {
            findNavController().removeActionFromCurrentBackStackEntry<NavigationAction>(
                NAVIGATION_ACTION
            )
            it?.let { handleNavigationAction(it) }
        }
        observeCurrentBackStackForAction<BcscAuthState>(BCSC_AUTH) {
            findNavController().removeActionFromCurrentBackStackEntry<BcscAuthState>(BCSC_AUTH)
            it?.let {
                handleBCSCAuthState(it)
            }
        }
    }

    open fun handleNavigationAction(navigationAction: NavigationAction) {}

    open fun handleBCSCAuthState(bcscAuthState: BcscAuthState) {}

    fun setActionToPreviousBackStackEntry(navigationAction: NavigationAction) {
        findNavController().setActionToPreviousBackStackEntry(NAVIGATION_ACTION, navigationAction)
    }
}

enum class NavigationAction {
    ACTION_BACK,
    ACTION_RE_CHECK
}

enum class BcscAuthState {
    SUCCESS,
    NO_ACTION,
    NOT_NOW
}
