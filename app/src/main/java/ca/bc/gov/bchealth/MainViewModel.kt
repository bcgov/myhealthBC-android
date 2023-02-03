package ca.bc.gov.bchealth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.workers.WorkerInvoker
import ca.bc.gov.repository.QueueItTokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 28,May,2022
*/
@HiltViewModel
class MainViewModel @Inject constructor(
    private val queueItTokenRepository: QueueItTokenRepository,
    private val workerInvoker: WorkerInvoker
) : ViewModel() {

    fun setQueItToken(token: String?) = viewModelScope.launch {
        queueItTokenRepository.setQueItToken(token)
        workerInvoker.executeOneTimeDataFetch()
    }
}
