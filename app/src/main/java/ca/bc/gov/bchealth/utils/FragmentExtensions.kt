package ca.bc.gov.bchealth.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Fragment.launchOnStart(action: (suspend CoroutineScope.() -> Unit)) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            action.invoke(this)
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
