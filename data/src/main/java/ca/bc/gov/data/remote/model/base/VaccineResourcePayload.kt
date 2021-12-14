package ca.bc.gov.data.remote.model.base

import com.google.gson.annotations.SerializedName
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class VaccineResourcePayload(
    val id: String,
    val loaded: Boolean,
    @SerializedName("retryin")
    val retryInMilli: Long,
    @SerializedName("personalhealthnumber")
    val phn: String,
    @SerializedName("firstname")
    val firstName: String,
    @SerializedName("lastname")
    val lastName: String,
    @SerializedName("birthdate")
    val birthDate: Instant,
    @SerializedName("vaccinedate")
    val vaccineDate: Instant,
    val doses: Int,
    val state: Int,
    val qrCode: Media,
    val federalVaccineProof: Media
)