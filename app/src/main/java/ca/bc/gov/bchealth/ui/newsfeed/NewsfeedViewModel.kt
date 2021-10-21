package ca.bc.gov.bchealth.ui.newsfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.rss.Newsfeed
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

/*
* Created by amit_metri on 21,October,2021
*/
class NewsfeedViewModel : ViewModel() {

    private val newsfeedMutableLiveData = MutableLiveData<MutableList<Newsfeed>>()

    val newsfeedLiveData: LiveData<MutableList<Newsfeed>>
        get() = newsfeedMutableLiveData

    suspend fun fetchNewsFeed(urlString: String) {
        try {
            val newsFeeds: MutableList<Newsfeed> = mutableListOf()

            val dbFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val dBuilder: DocumentBuilder = dbFactory.newDocumentBuilder()
            val document: Document =
                dBuilder.parse(downloadUrl(urlString))

            val element: Element = document.documentElement
            element.normalize()

            val nList: NodeList = document.getElementsByTagName("item")

            for (i in 0 until nList.length) {
                val node: Node = nList.item(i)
                if (node.nodeType === Node.ELEMENT_NODE) {
                    val element2 = node as Element
                    newsFeeds.add(
                        Newsfeed(
                            getValue("title", element2),
                            getValue("description", element2),
                            getValue("pubDate", element2)
                        )
                    )
                }
            }

            newsfeedMutableLiveData.postValue(newsFeeds)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getValue(tag: String, element: Element): String? {
        val nodeList = element.getElementsByTagName(tag).item(0).childNodes
        val node = nodeList.item(0)
        return node.nodeValue
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // Starts the query
            connect()
            inputStream
        }
    }
}
