package ca.bc.gov.bchealth.ui.addcard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * [AddCardOptionViewModel]
 *
 * @author amit metri
 */
@HiltViewModel
class AddCardOptionViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    /*
    * Used to manage Success, Error and Loading status in the UI
    * */
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = repository.responseSharedFlow

    fun processUploadedImage(
        uri: Uri,
        context: Context
    ) = viewModelScope.launch {
        repository.processUploadedImage(uri, context)
    }
}
