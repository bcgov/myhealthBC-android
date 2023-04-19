package ca.bc.gov.data.datasource.local.converter

import androidx.room.TypeConverter
import ca.bc.gov.data.datasource.local.entity.PatientNameEntity
import com.google.gson.Gson

/**
 * @author Pinakin Kansara
 */
class PatientNameConverter {

    @TypeConverter
    fun stringToPatientName(jsonString: String): PatientNameEntity? {
        return Gson().fromJson(jsonString, PatientNameEntity::class.java)
    }

    @TypeConverter
    fun patientNameToString(dto: PatientNameEntity?): String? {
        return Gson().toJson(dto)
    }
}