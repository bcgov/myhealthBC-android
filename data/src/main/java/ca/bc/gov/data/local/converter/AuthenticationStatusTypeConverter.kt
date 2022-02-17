package ca.bc.gov.data.local.converter

import androidx.room.TypeConverter
import ca.bc.gov.common.model.AuthenticationStatus

/*
* Created by amit_metri on 17,February,2022
*/
class AuthenticationStatusTypeConverter {
    @TypeConverter
    fun intToAuthenticationStatus(value: String) = enumValueOf<AuthenticationStatus>(value)

    @TypeConverter
    fun authenticationStatusToInt(value: AuthenticationStatus) = value.source
}
