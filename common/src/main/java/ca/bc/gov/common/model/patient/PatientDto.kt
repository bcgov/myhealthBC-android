package ca.bc.gov.common.model.patient

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Parcelize
data class PatientDto(
    var id: Long = 0,
    val fullName: String,
    val dateOfBirth: Instant,
    var phn: String? = null,
    val patientOrder: Long = Long.MAX_VALUE
) : Parcelable
