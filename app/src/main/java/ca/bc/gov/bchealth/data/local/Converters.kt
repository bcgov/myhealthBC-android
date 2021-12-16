package ca.bc.gov.bchealth.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime

/*
* Created by amit_metri on 26,November,2021
*/
class Converters {

    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}
