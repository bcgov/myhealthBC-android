package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.labtest.LabTestEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface LabTestDao : BaseDao<LabTestEntity> {

    @Query("SELECT * FROM lab_test WHERE lab_order_id = :labOrderId")
    suspend fun findByLabOrderId(labOrderId: String): LabTestEntity?

    @Query("DELETE FROM lab_test WHERE lab_order_id = :labOrderId")
    suspend fun delete(labOrderId: String): Int
}
