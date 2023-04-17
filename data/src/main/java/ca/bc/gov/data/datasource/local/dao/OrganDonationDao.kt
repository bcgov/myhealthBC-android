package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.services.OrganDonationEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface OrganDonationDao : BaseDao<OrganDonationEntity> {

    @Query("DELETE FROM organ_donation WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int

    @Query("SELECT * FROM organ_donation WHERE patient_id = :patientId")
    suspend fun findOrganDonationById(patientId: Long): OrganDonationEntity?
}
