package ca.bc.gov.data.datasource.local.converter

import androidx.room.TypeConverter
import ca.bc.gov.common.model.QuickAccessLinkName

class QuickAccessLinkNameConverter {

    @TypeConverter
    fun stringToEnum(value: String) = QuickAccessLinkName[value]

    @TypeConverter
    fun enumToString(linkName: QuickAccessLinkName) = linkName.value
}
