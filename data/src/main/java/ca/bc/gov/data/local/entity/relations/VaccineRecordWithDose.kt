package ca.bc.gov.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity

/**
 * @author Pinakin Kansara
 */
data class VaccineRecordWithDose(
    @Embedded
    val vaccineRecordEntity: VaccineRecordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "vaccine_record_id"
    )
    val vaccineDoses: List<VaccineDoseEntity>
)
