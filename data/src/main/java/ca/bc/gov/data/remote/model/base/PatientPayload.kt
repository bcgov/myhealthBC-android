package ca.bc.gov.data.remote.model.base

import com.google.gson.annotations.SerializedName

data class PatientPayload(
    @SerializedName("birthdate")
    val birthDate: String?,
    @SerializedName("firstname")
    val firstName: String?,
    val gender: String?,
    val hdid: String?,
    @SerializedName("lastname")
    val lastName: String?,
    @SerializedName("personalhealthnumber")
    val personalHealthNumber: String?
)
