package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto

/**
 * @author Pinakin Kansara
 */
data class MedicationWithSummaryAndPharmacyDto(
    val medicationRecord: MedicationRecordDto,
    val medicationSummary: MedicationSummaryDto,
    val dispensingPharmacy: DispensingPharmacyDto
)
