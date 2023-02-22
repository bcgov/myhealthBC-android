package ca.bc.gov.data.datasource.remote.model.base

import com.google.gson.annotations.SerializedName

data class PatientPayload(
    @SerializedName("birthdate")
    val birthDate: String?,

    @SerializedName("firstname")
    val firstName: String?,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("hdid")
    val hdid: String?,

    @SerializedName("lastname")
    val lastName: String?,

    @SerializedName("personalhealthnumber")
    val personalHealthNumber: String?,

    @SerializedName("physicalAddress")
    val physicalAddress: PatientAddress?,

    @SerializedName("postalAddress")
    val mailingAddress: PatientAddress?,
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
