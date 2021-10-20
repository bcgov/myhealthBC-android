package ca.bc.gov.bchealth.model.rss

data class Channel(
    val description: String,
    val id: Id,
    val item: List<Item>,
    val link: List<Any>,
    val title: String
)