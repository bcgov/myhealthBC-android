package ca.bc.gov.common.model.immunization

import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class ImmunizationRecordDto(
    val id: Long = 0,
    var patientId: Long = 0,
    val immunizationId: String? = null,
    val dateOfImmunization: Instant,
    val status: String? = null,
    val isValid: Boolean,
    val provideOrClinic: String? = null,
    val targetedDisease: String? = null,
    val immunizationName: String? = null,
    val agentCode: String? = null,
    val agentName: String? = null,
    val lotNumber: String? = null,
    val productName: String? = null,
    val dataSorce: DataSource = DataSource.BCSC
)
