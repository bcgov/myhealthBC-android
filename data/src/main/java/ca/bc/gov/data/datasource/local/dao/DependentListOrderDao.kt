package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.dependent.DependentListOrder

@Dao
interface DependentListOrderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dependentListOrder: DependentListOrder)

    @Query("DELETE FROM dependent_list_order ")
    suspend fun deleteAll(): Int
}
