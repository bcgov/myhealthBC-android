package ca.bc.gov.data.datasource.remote.model.base.vaccine

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class VaccineResourcePayload(
    val id: String,
    @SerializedName("personalhealthnumber")
    val phn: String?,
    @SerializedName("firstname")
    val firstName: String?,
    @SerializedName("lastname")
    val lastName: String?,
    @SerializedName("birthdate")
    val birthDate: String?,
    @SerializedName("vaccinedate")
    val vaccineDate: String?,
    val doses: Int,
    val state: Int,
    val qrCode: Media?,
    val federalVaccineProof: Media?
)
