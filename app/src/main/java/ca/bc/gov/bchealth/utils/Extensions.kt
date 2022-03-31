package ca.bc.gov.bchealth.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import ca.bc.gov.bchealth.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

/*
* For displaying Toast
* */
fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

/*
* For date to be shown under every news feed
* */
fun Long.getNewsFeedDate(): String {
    return try {
        val date = Date(this)
        val format = SimpleDateFormat("y-MM-d", Locale.CANADA)
        format.format(date)
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

/*
* Redirect to external URL
* */
fun Context.redirect(url: String) {

    try {

        val customTabColorSchemeParams: CustomTabColorSchemeParams =
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(resources.getColor(R.color.white, null))
                .setSecondaryToolbarColor(resources.getColor(R.color.white, null))
                .build()

        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabIntent: CustomTabsIntent = builder
            .setDefaultColorSchemeParams(customTabColorSchemeParams)
            .setCloseButtonIcon(
                resources.getDrawable(R.drawable.ic_action_back, null)
                    .toBitmap()
            )
            .build()

        customTabIntent.launchUrl(
            this,
            Uri.parse(url)
        )
    } catch (e: Exception) {
        e.printStackTrace()
        showURLFallBack(this, url)
    }
}

private fun showURLFallBack(context: Context, url: String) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        context.toast(context.getString(R.string.no_app_found))
    }
}

/*
* Adjust offset to get the correct date from Date Picker in all the timezones
* */
fun Long.adjustOffset(): Date {

    var adjustedEpoch = this

    /*
    * Get the Date object out of epoch time.
    * Date object will be attached with timezone offset depending on devices timezone
    * */
    val date = Date(this)

    // Calender instance
    val calendar = Calendar.getInstance()
    calendar.time = date

    val offsetInMilliSeconds =
        (((calendar as GregorianCalendar).toZonedDateTime().offset).totalSeconds * 1000)

    adjustedEpoch = if (offsetInMilliSeconds < 0)
        adjustedEpoch.plus(offsetInMilliSeconds * -1)
    else // adding +1 to get correct date from formatter
        adjustedEpoch.minus(offsetInMilliSeconds) + 1

    return Date(adjustedEpoch)
}

/*
* Close keypad
* */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Makes View visible
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

/**
 * Makes view invisible, and it doesn't take any space for layout purposes.
 */
fun View.hide() {
    this.visibility = View.GONE
}
