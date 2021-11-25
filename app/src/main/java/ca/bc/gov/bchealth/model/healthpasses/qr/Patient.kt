package ca.bc.gov.bchealth.model.healthpasses.qr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [Patient]
 *
 * @author Pinakin Kansara
 */
@Parcelize
data class Patient(
        val reference: String
) : Parcelable
