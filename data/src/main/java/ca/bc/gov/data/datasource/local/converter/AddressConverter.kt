package ca.bc.gov.data.datasource.local.converter

import androidx.room.TypeConverter
import ca.bc.gov.data.datasource.local.entity.PatientAddressEntity
import com.google.gson.Gson

class AddressConverter {
    @TypeConverter
    fun stringToAddress(jsonString: String): PatientAddressEntity? {
        return Gson().fromJson(jsonString, PatientAddressEntity::class.java)
    }

    @TypeConverter
    fun addressToString(dto: PatientAddressEntity?): String? {
        return Gson().toJson(dto)
    }
}
