package ca.bc.gov.data.datasource.local.converter

import androidx.room.TypeConverter
import ca.bc.gov.common.model.SyncStatus

class SyncStatusConverter {
    @TypeConverter
    fun stringToSyncStatus(value: String): SyncStatus = enumValueOf<SyncStatus>(value)

    @TypeConverter
    fun syncStatusToString(value: SyncStatus): String = value.name
}
