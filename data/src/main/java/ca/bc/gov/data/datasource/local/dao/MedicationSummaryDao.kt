package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import ca.bc.gov.data.datasource.local.entity.medication.MedicationSummaryEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface MedicationSummaryDao : BaseDao<MedicationSummaryEntity>
