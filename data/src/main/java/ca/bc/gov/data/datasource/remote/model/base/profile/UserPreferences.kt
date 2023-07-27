package ca.bc.gov.data.datasource.remote.model.base.profile

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class UserPreferences(
    @SerializedName("quickLinks")
    var quickLinks: QuickLinks?,
)

data class QuickLinks(
    @SerializedName("hdId")
    val hdid: String,

    // json with a List of QuickLinksItems that will be parsed during mapping
    @SerializedName("value")
    val jsonList: String,
) {
    var list: List<QuickLinksItem> = listOf()
}

data class QuickLinksItem(
    @SerializedName("name")
    val name: String,

    @SerializedName("filter")
    val filter: QuickLinksItemFilter,
)

data class QuickLinksItemFilter(
    @SerializedName("modules")
    val modules: List<String>
)
