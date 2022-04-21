package ca.bc.gov.bchealth.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doOnTextChanged
import ca.bc.gov.bchealth.R
import com.google.android.material.textfield.TextInputLayout
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

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextInputLayout.updateCommentEndIcon(context: Context) {
    this.editText?.doOnTextChanged { text, _, _, _ ->
        if (text.isNullOrBlank()) {
            this.setEndIconDrawable(R.drawable.ic_add_comment)
        } else {
            this.apply {
                setEndIconDrawable(R.drawable.ic_add_comment_press)
                setEndIconTintList(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.blue
                        )
                    )
                )
            }
        }
    }
}