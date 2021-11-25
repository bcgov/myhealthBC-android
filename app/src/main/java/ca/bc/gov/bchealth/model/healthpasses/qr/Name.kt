package ca.bc.gov.bchealth.model.healthpasses.qr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [Name]
 *
 * @author Pinakin Kansara
 */
@Parcelize
data class Name(
        val family: String?,
        val given: List<String>
) : Parcelable
