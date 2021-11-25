package ca.bc.gov.bchealth.model

import android.os.Parcelable
import ca.bc.gov.bchealth.model.healthpasses.qr.Entry

/**
 * [HealthCardDto]
 *
 * @author Pinakin Kansara
 */
@kotlinx.parcelize.Parcelize
data class HealthCardDto(
    val id: Int,
    val uri: String,
    var federalPass: String? = "",
    var name: String,
    val status: ImmunizationStatus,
    var isExpanded: Boolean = false,
    val issueDate: String = "",
    val birthDate: String = "",
    val occurrenceDateTime: String = "",
    val immunizationEntries: List<Entry>?
) : Parcelable
