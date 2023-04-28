package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.bc.gov.data.datasource.local.entity.services.OrganDonorEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface OrganDonorDao : BaseDao<OrganDonorEntity> {

    @Query("DELETE FROM organ_donation WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int

    @Query("SELECT * FROM organ_donation WHERE patient_id = :patientId")
    suspend fun findOrganDonorById(patientId: Long): OrganDonorEntity?

    @Update(entity = OrganDonorEntity::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun update(organDonorEntity: OrganDonorEntity)
}
