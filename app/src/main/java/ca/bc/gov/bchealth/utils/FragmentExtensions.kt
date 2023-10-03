package ca.bc.gov.bchealth.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun Fragment.launchAndRepeatWithLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(state) {
            block()
        }
    }
}

fun Fragment.observeWork(workName: String, action: (WorkInfo.State?) -> Unit) {
    val workRequest = WorkManager.getInstance(requireContext())
        .getWorkInfosForUniqueWorkLiveData(workName)
    if (!workRequest.hasObservers()) {
        workRequest.observe(viewLifecycleOwner) {
            action.invoke(it.firstOrNull()?.state)
        }
    }
}

inline fun <reified T> Fragment.observeCurrentBackStackForAction(
    key: String,
    crossinline action: (T?) -> Unit
) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(
        key
    )?.observe(viewLifecycleOwner) {
        action(it)
    }
}

inline fun <reified T> NavController.removeActionFromCurrentBackStackEntry(key: String) {
    currentBackStackEntry?.savedStateHandle?.remove<T>(key)
}

inline fun <reified T> NavController.setActionToCurrentBackStackEntry(key: String, value: T) {
    currentBackStackEntry?.savedStateHandle?.set(key, value)
}

inline fun <reified T> NavController.removeActionFromPreviousBackStackEntry(key: String) {
    previousBackStackEntry?.savedStateHandle?.remove<T>(key)
}

inline fun <reified T> NavController.setActionToPreviousBackStackEntry(key: String, value: T) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun Fragment.composeEmail(address: String = HEALTH_GATEWAY_EMAIL_ADDRESS, subject: String = "") {
    requireActivity().composeEmail(address, subject)
}
