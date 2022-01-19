package ca.bc.gov.data.local.converter

import androidx.room.TypeConverter
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
class DateTimeConverter {

    @TypeConverter
    fun longToInstant(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun instantToLong(value: Instant): Long {
        return value.toEpochMilli()
    }
}
