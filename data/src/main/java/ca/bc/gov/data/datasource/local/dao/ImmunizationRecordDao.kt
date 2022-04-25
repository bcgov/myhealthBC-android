package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordWithForecastAndPatient

/**
 * @author Pinakin kansara
 */
@Dao
interface ImmunizationRecordDao : BaseDao<ImmunizationRecordEntity> {

    @Query("SELECT * FROM immunization_record WHERE id = :id")
    suspend fun findById(id: Long): ImmunizationRecordEntity

    @Query("SELECT * FROM immunization_record WHERE id = :id")
    suspend fun findByImmunizationId(id: Long): ImmunizationRecordWithForecastAndPatient?

    @Query("DELETE FROM immunization_record WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int
}
