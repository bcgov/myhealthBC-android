package ca.bc.gov.bchealth.model.rss

data class Item(
    val author: Author,
    val category: String,
    val description: String,
    val enclosure: Enclosure,
    val guid: Guid,
    val link: String,
    val pubDate: String,
    val title: String
)
