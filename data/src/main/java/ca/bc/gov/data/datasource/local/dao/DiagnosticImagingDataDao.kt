package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.services.DiagnosticImagingDataEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface DiagnosticImagingDataDao : BaseDao<DiagnosticImagingDataEntity> {

    @Query("SELECT * FROM diagnostic_imaging where id = :id")
    suspend fun getDiagnosticImagingDataDetails(id: Long): DiagnosticImagingDataEntity?
}
