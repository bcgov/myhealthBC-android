package ca.bc.gov.bchealth.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper function to read file from asset
 * and return String JSON.
 */
fun Context.readJsonFromAsset(fileName: String) =
    this.assets.open(fileName).bufferedReader().use { it.readText() }

/*
* For displaying Toast
* */
fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

/*
* For converting epoch datetime format to human readable format
* */
fun Long.getDateTime(): String {
    return try {
        val date1 = Date(this)
        val format = SimpleDateFormat("MMMM-dd-y, HH:mm", Locale.ENGLISH)
        format.format(date1)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        ""
    }
}


/*
   * Check is network connection
   * */
fun Context.isOnline(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return true
        }
    }
    return false
}