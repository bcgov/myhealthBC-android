package ca.bc.gov.bchealth.data.local

import androidx.room.TypeConverter
import java.util.Date

/*
* Created by amit_metri on 26,November,2021
*/
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}