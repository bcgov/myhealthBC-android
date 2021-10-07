package ca.bc.gov.bchealth.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import kotlinx.coroutines.flow.Flow

/**
 * [HealthCardDao]
 *
 * @author Pinakin Kansara
 */
@Dao
interface HealthCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(healthCard: HealthCard)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(vararg healthCard: HealthCard)

    @Delete
    suspend fun delete(healthCard: HealthCard)

    @Query("SELECT * FROM health_card")
    fun getCards(): Flow<List<HealthCard>>

    @Query("DELETE from health_card")
    suspend fun deleteAllCards(): Int

    @Insert
    suspend fun insertAllCards(healthCards: List<HealthCard>)

    @Transaction
    suspend fun rearrange(healthCards: List<HealthCard>) {
        deleteAllCards()
        insertAllCards(healthCards)
    }
}
