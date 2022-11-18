package ca.bc.gov.data.datasource.remote.model.response

/**
 * @author: Created by Rashmi Bambhania on 16,May,2022
 */
data class MobileConfigurationResponse(
    val online: Boolean?,
    val baseUrl: String?,
    val version: Int,
)
