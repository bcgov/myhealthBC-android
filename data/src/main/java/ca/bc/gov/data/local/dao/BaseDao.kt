package ca.bc.gov.data.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(t: List<T>): List<Long>
}
