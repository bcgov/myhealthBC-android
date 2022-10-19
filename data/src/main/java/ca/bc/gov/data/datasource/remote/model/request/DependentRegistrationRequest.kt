package ca.bc.gov.data.datasource.remote.model.request

import com.google.gson.annotations.SerializedName

data class DependentRegistrationRequest(
    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String,

    @SerializedName("phn")
    val phn: String
)
