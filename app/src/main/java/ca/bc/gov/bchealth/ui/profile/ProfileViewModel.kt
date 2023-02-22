package ca.bc.gov.bchealth.ui.profile

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.utils.URL_ADDRESS_CHANGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }

        _uiState.update {
            it.copy(
                fullName = "Jean Smith",
                loading = false,
                uiList = listOf(
                    ProfileItem.Info(
                        R.string.profile_first_name,
                        "Jean"
                    ),
                    ProfileItem.Info(
                        R.string.profile_last_name,
                        "Smith"
                    ),
                    ProfileItem.Info(
                        R.string.profile_phn,
                        "4444 555 999"
                    ),
                    ProfileItem.Address(
                        R.string.profile_physical_address,
                        "Vancouver, BC V8V 2T2",
                        R.string.profile_address_footer,
                        R.string.profile_address_footer_click,
                        URL_ADDRESS_CHANGE
                    ),
                    ProfileItem.EmptyAddress(
                        R.string.profile_mailing_address,
                        R.string.profile_address_empty,
                        R.string.profile_address_empty_footer,
                        R.string.profile_address_empty_footer_click,
                        URL_ADDRESS_CHANGE
                    ),
                )
            )
        }
    }
}

data class ProfileUiState(
    val title: Int = R.string.profile,
    val loading: Boolean = false,
    val fullName: String = "",
    val uiList: List<ProfileItem> = listOf(),
)

sealed class ProfileItem {
    data class Info(
        val label: Int,
        val content: String
    ) : ProfileItem()

    data class EmptyAddress(
        val label: Int,
        val placeholder: Int,
        val footer: Int,
        val clickableText: Int,
        val url: String,
    ) : ProfileItem()

    data class Address(
        val label: Int,
        val content: String,
        val footer: Int,
        val clickableText: Int,
        val url: String,
    ) : ProfileItem()
}
