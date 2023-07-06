package ca.bc.gov.data.datasource.local.converter

import androidx.room.TypeConverter
import ca.bc.gov.common.model.AppFeatureName

class AppFeatureNameConverter {

    @TypeConverter
    fun stringToEnum(name: String) = AppFeatureName[name]

    @TypeConverter
    fun enumToString(appFeatureName: AppFeatureName) = appFeatureName.value
}
