package ca.bc.gov.bchealth.analytics

/*
* Created by amit_metri on 25,October,2021
*/
enum class AnalyticsAction(val value: String) {

    AddQR("add_qr"),
    RemoveCard("remove_card"),
    ResourceLinkSelected("resource_click"),
    NewsLinkSelected("news_feed_selected")

}