package ca.bc.gov.data.datasource.local.converter

import androidx.room.TypeConverter
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
class DateTimeConverter {

    @TypeConverter
    fun longToInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToLong(value: Instant?): Long? {
        return value?.toEpochMilli()
    }
}
