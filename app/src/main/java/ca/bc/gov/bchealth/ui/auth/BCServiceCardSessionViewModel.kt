package ca.bc.gov.bchealth.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.BcServiceCardSessionInfoType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BCServiceCardSessionViewModel @Inject constructor() : ViewModel() {
    fun getSessionStateInfo(type: BcServiceCardSessionInfoType): SessionStateInfo {
        return when (type) {
            BcServiceCardSessionInfoType.RECORDS -> {
                SessionStateInfo(
                    title = R.string.health_records,
                    sessionInfo = R.string.session_time_out,
                    sessionDesc = R.string.login_to_view_hidden_records_msg,
                )
            }

            BcServiceCardSessionInfoType.SERVICES -> {
                SessionStateInfo(
                    title = R.string.services,
                    sessionInfo = R.string.session_time_out,
                    sessionDesc = R.string.services_session_expired,
                )
            }

            BcServiceCardSessionInfoType.DEPENDENTS -> {
                SessionStateInfo(
                    title = R.string.dependents_title,
                    sessionInfo = R.string.session_time_out,
                    sessionDesc = R.string.dependents_session_expired,
                )
            }
        }
    }
}

data class SessionStateInfo(
    @StringRes val title: Int,
    @StringRes val sessionInfo: Int,
    @StringRes val sessionDesc: Int
)
