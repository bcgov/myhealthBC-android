package ca.bc.gov.data.datasource.remote.model.base.dependent

import com.google.gson.annotations.SerializedName

data class DependentPayload(
    @SerializedName("dependentInformation")
    val dependentInformation: DependentInformation
)
