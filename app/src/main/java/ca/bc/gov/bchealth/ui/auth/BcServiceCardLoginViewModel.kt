package ca.bc.gov.bchealth.ui.auth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.BcServiceCardLoginInfoType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BcServiceCardLoginViewModel @Inject constructor() : ViewModel() {

    fun getLoginStateInfo(type: BcServiceCardLoginInfoType): LoginStateInfo {
        return when (type) {
            BcServiceCardLoginInfoType.RECORDS -> {
                LoginStateInfo(
                    title = R.string.health_records,
                    description = R.string.health_records_subtitle,
                    icon = R.drawable.ic_health_record,
                    info = R.string.health_records_subtitle_1
                )
            }

            BcServiceCardLoginInfoType.SERVICES -> {
                LoginStateInfo(
                    title = R.string.services,
                    description = R.string.services_subtitle,
                    icon = R.drawable.ic_services_graphic,
                    info = R.string.services_subtitle_1
                )
            }

            BcServiceCardLoginInfoType.DEPENDENTS -> {
                LoginStateInfo(
                    title = R.string.dependent,
                    description = R.string.dependents_body,
                    icon = R.drawable.ic_dependents_log_in,
                    info = R.string.dependents_log_in
                )
            }
        }
    }
}

data class LoginStateInfo(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val icon: Int,
    @StringRes val info: Int
)
