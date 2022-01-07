package ca.bc.gov.bchealth.ui.mycards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.repository.AuthManagerRepo
import ca.bc.gov.bchealth.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MyCardsViewModel]
 *
 * @author Pinakin Kansara
 */
@HiltViewModel
class MyCardsViewModel @Inject constructor(
    private val repository: CardRepository,
    dataStoreRepo: DataStoreRepo,
    private val authManagerRepo: AuthManagerRepo
) : ViewModel() {

    val responseFlow = repository.responseSharedFlow

    val cards = repository.cards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(10000),
        initialValue = null
    )

    fun saveCard(uri: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.insert(HealthCard(uri = uri))
        }
    }

    fun unLink(id: Int, uri: String) = viewModelScope.launch {
        repository.unLink(HealthCard(id, uri))
    }

    fun rearrange(healthCardsDto: List<HealthCardDto>) = viewModelScope.launch {
        val healthCards: MutableList<HealthCard> = mutableListOf()
        healthCardsDto.forEach {
            healthCards.add(HealthCard(uri = it.uri, federalPass = it.federalPass))
        }
        repository.rearrangeHealthCards(healthCards)
    }

    val isOnBoardingShown = dataStoreRepo.isOnBoardingShown.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isNewfeatureShown = dataStoreRepo.isNewFeatureShown.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isAnalyticsEnabled = dataStoreRepo.isAnalyticsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0),
        initialValue = null
    )

    fun replaceExitingHealthPass(healthCard: HealthCard) = viewModelScope.launch {
        repository.replaceExitingHealthPass(healthCard)
    }

    fun checkLogin(
        destinationId: Int,
        navOptions: NavOptions,
        navController: NavController
    ) = viewModelScope.launch {
        authManagerRepo.checkLogin(destinationId, navOptions, navController)
    }
}
