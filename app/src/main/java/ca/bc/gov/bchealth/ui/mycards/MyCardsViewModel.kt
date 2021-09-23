package ca.bc.gov.bchealth.ui.mycards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.CardType
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [MyCardsViewModel]
 *
 * @author Pinakin Kansara
 */
@HiltViewModel
class MyCardsViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    val cards = repository.cards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(10000),
        initialValue = null
    )

    fun saveCard(uri: String, type: CardType) = viewModelScope.launch {
        repository.insertHealthCard(HealthCard(uri, type))
    }
}
