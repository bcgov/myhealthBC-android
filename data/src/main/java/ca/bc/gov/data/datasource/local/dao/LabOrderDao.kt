package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderWithLabTestsAndPatient

/**
 * @author Pinakin Kansara
 */
@Dao
interface LabOrderDao : BaseDao<LabOrderEntity> {

    @Query("SELECT * FROM lab_order WHERE id = :id")
    suspend fun findById(id: String): LabOrderEntity

    @Transaction
    @Query("SELECT * FROM lab_order WHERE id = :id")
    suspend fun findByLabOrderId(id: String): LabOrderWithLabTestsAndPatient?

    @Query("DELETE FROM lab_order WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int
}
