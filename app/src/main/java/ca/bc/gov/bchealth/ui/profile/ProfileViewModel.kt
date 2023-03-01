package ca.bc.gov.bchealth.ui.profile

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.utils.URL_ADDRESS_CHANGE
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.PatientAddressDto
import ca.bc.gov.repository.UserProfileRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val userProfileRepository: UserProfileRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }

        try {
            val patient =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)

            val authParameters = bcscAuthRepo.getAuthParametersDto()

            val userProfile = userProfileRepository.getUserProfile(
                token = authParameters.token,
                hdid = authParameters.hdid,
                patientId = patient.id
            )

            _uiState.update {
                it.copy(
                    fullName = patient.fullName,
                    loading = false,
                    uiList = listOf(
                        ProfileItem.Info(
                            R.string.profile_first_name, patient.firstName
                        ),
                        ProfileItem.Info(
                            R.string.profile_last_name, patient.lastName
                        ),
                        ProfileItem.Info(
                            R.string.profile_phn, patient.phn.orEmpty()
                        ),
                        getProfileAddress(
                            patient.physicalAddress, R.string.profile_physical_address
                        ),
                        getProfileAddress(
                            patient.mailingAddress, R.string.profile_mailing_address
                        ),
                    ),
                    email = userProfile.email,
                    isEmailVerified = userProfile.isEmailVerified,
                    phone = userProfile.smsNumber,
                    isPhoneVerified = userProfile.isPhoneVerified,
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(loading = false, error = e)
            }
        }
    }
}

private fun getProfileAddress(addressDto: PatientAddressDto?, @StringRes addressLabel: Int) =
    if (addressDto == null) {
        ProfileItem.EmptyAddress(
            addressLabel,
            R.string.profile_address_empty,
            R.string.profile_address_empty_footer,
            R.string.profile_address_empty_footer_click,
            URL_ADDRESS_CHANGE
        )
    } else {
        ProfileItem.Address(
            addressLabel,
            addressDto.toUiItem(),
            R.string.profile_address_footer,
            R.string.profile_address_footer_click,
            URL_ADDRESS_CHANGE
        )
    }

private fun PatientAddressDto.toUiItem(): String {
    val builder = StringBuilder()
    streetLines.forEach {
        builder.append(it + "\n")
    }
    builder.append("$city, $province $postalCode")
    return builder.toString()
}

data class ProfileUiState(
    val title: Int = R.string.profile,
    val loading: Boolean = false,
    val fullName: String = "",
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val phone: String? = null,
    val isPhoneVerified: Boolean = false,
    val uiList: List<ProfileItem> = listOf(),
    val error: Exception? = null
)

sealed class ProfileItem {
    data class Info(
        @StringRes val label: Int,
        val content: String
    ) : ProfileItem()

    data class EmptyAddress(
        @StringRes val label: Int,
        @StringRes val placeholder: Int,
        @StringRes val footer: Int,
        @StringRes val clickableText: Int,
        val url: String,
    ) : ProfileItem()

    data class Address(
        @StringRes val label: Int,
        val content: String,
        @StringRes val footer: Int,
        @StringRes val clickableText: Int,
        val url: String,
    ) : ProfileItem()
}
