package ca.bc.gov.data.datasource.remote.model.base.dependent

import com.google.gson.annotations.SerializedName

data class DependentInformation(
    @SerializedName("hdid")
    val hdid: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,

    @SerializedName("PHN")
    val phn: String,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String,

    @SerializedName("gender")
    val gender: String,
)
