package ca.bc.gov.bchealth.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT * FROM health_card")
    fun getCards(): Flow<List<HealthCard>>
}
