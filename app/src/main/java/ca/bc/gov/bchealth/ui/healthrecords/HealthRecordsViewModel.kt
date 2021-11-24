package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/*
* Created by amit_metri on 23,November,2021
*/
@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    cardRepository: CardRepository
) : ViewModel() {

    val members = cardRepository.cards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0, 0),
        initialValue = null
    )
}
