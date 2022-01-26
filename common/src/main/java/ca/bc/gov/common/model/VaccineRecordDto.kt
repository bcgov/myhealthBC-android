package ca.bc.gov.common.model

import android.graphics.Bitmap
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class VaccineRecordDto(
    var id: Long = 0,
    var patientId: Long = 0,
    val qrIssueDate: Instant,
    val status: ImmunizationStatus,
    var qrCodeImage: Bitmap?,
    var shcUri: String,
    var federalPass: String?,
    var mode: DataSource,
    var doseDtos: List<VaccineDoseDto> = emptyList()
)
