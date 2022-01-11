package ca.bc.gov.common.model.patient

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Parcelize
data class Patient(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Instant,
    val phn: String? = null,
): Parcelable
