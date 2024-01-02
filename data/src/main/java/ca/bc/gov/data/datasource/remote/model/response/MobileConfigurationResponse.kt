package ca.bc.gov.data.datasource.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * @author: Created by Rashmi Bambhania on 16,May,2022
 */
data class MobileConfigurationResponse(
    @SerializedName("online")
    val online: Boolean?,

    @SerializedName("baseUrl")
    val baseUrl: String?,

    @SerializedName("authentication")
    val authentication: AuthenticationResponse,

    @SerializedName("version")
    val version: Int,

    @SerializedName("datasets")
    val patientDataSets: List<String>,

    @SerializedName("dependentDatasets")
    val dependentDataSets: List<String>,

    @SerializedName("services")
    val service: List<String>
)

data class AuthenticationResponse(
    @SerializedName("endpoint")
    val endpoint: String,

    @SerializedName("identityProviderId")
    val identityProviderId: String,

    @SerializedName("clientId")
    val clientId: String,
)
