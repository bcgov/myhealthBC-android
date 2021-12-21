package ca.bc.gov.bchealth.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
import ca.bc.gov.bchealth.ui.mycards.qrgen.QrCode
import ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
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
* For date time to be shown on helath passes
* */
fun Long.getIssueDate(): String {
    return try {
        val date = Date(this * 1000)
        val format = SimpleDateFormat("MMMM-dd-y, HH:mm", Locale.CANADA)
        format.format(date)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        ""
    }
}

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
* Get BitMap of QR
* */
fun String.getBarcode(): Bitmap? {

    try {
        val segments: MutableList<QrSegment> = QrSegment.makeSegments(this)
        val qrCode: QrCode = QrCode.encodeSegments(
            segments,
            QrCode.Ecc.LOW,
            5,
            40,
            2,
            false
        )

        val size = qrCode.size

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (y in 0 until size) {
            for (x in 0 until size) {
                bitmap.setPixel(
                    x, y,
                    if (qrCode.getModule(x, y))
                        Color.BLACK
                    else
                        Color.WHITE
                )
            }
        }

        val scaledBitMap = Bitmap.createScaledBitmap(bitmap, 400, 400, false)

        return addWhiteBorder(scaledBitMap, 10)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

private fun addWhiteBorder(bmp: Bitmap, borderSize: Int): Bitmap? {
    val bmpWithBorder = Bitmap
        .createBitmap(
            bmp.width + borderSize * 2,
            bmp.height + borderSize * 2,
            bmp.config
        )
    val canvas = Canvas(bmpWithBorder)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(bmp, borderSize.toFloat(), borderSize.toFloat(), null)
    return bmpWithBorder
}

/*
* Close keypad
* */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/*
* Generic error dialog
* */
fun Context.showError(title: String, message: String) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

/*
* Show alert dialog with positive button click action
* */
fun Context.showAlertDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    runnable: Runnable
) {

    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            runnable.run()
            dialog.dismiss()
        }.setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

/*
* For showing date on individual covid test record
* */
fun LocalDateTime.getDateForIndividualCovidTestResult(): String {

    return try {
        this.format(DateTimeFormatter.ofPattern("MMM. dd, y")).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/*
* For Showing date on covid test results screen
* */
fun LocalDateTime.getDateForCovidTestResults(): String {
    return try {
        this.format(DateTimeFormatter.ofPattern("MMMM-dd-y, HH:mm")).toString()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        ""
    }
}

/*
* Convert String date time value to LocalDateTime to save in DB table.
* */
fun String.getLocalDateTimeFromAPIResponse(): LocalDateTime? {

    return try {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val formatter = DateTimeFormatter.ofPattern(format)
        LocalDateTime.parse(this, formatter)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }
}

/*
* Get date for individual vaccine record
* */
fun LocalDate.getDateForIndividualVaccineRecord(): String {
    return try {
        this.format(DateTimeFormatter.ofPattern("MMM. dd, yyyy")).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/*
* Get date for individual vaccine record details
* */
fun LocalDate.getDateForVaccineRecordDetails(): String {
    return try {
        this.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/*
* Get collectionDate for retry
* */
fun LocalDateTime.getCollectionDate(): String {
    return try {
        this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
