package ca.bc.gov.data.datasource.remote.model.base.patient

import com.google.gson.annotations.SerializedName

data class PatientPayload(
    @SerializedName("birthdate")
    val birthDate: String?,

    val commonName: PatientName?,

    val legalName: PatientName?,

    var preferredName: PatientName?,

    @SerializedName("firstname")
    val firstName: String?,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("hdid")
    val hdid: String?,

    @SerializedName("lastname")
    val lastName: String?,

    @SerializedName("personalHealthNumber")
    val personalHealthNumber: String?,

    @SerializedName("physicalAddress")
    val physicalAddress: PatientAddress?,

    @SerializedName("postalAddress")
    val mailingAddress: PatientAddress?,

    val responseCode: String?
)

data class PatientAddress(
    @SerializedName("streetLines")
    val streetLines: List<String>,

    @SerializedName("city")
    val city: String,

    @SerializedName("state")
    val province: String,

    @SerializedName("postalCode")
    val postalCode: String,
)
