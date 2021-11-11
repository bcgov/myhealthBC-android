package ca.bc.gov.bchealth.model

import android.os.Parcelable

/**
 * [HealthCardDto]
 *
 * @author Pinakin Kansara
 */
@kotlinx.parcelize.Parcelize
data class HealthCardDto(
    val id: Int,
    val name: String,
    val status: ImmunizationStatus,
    val uri: String,
    var isExpanded: Boolean = false,
    val issueDate: String = "",
    val federalPass : String? = ""
) : Parcelable
