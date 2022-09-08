package ca.bc.gov.bchealth.utils

import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import ca.bc.gov.bchealth.R
import com.google.android.material.appbar.MaterialToolbar

fun TextView.underlineText() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun MaterialToolbar.inflateHelpButton(action: () -> Unit) {
    inflateMenu(R.menu.help_menu)
    setOnMenuItemClickListener { menu ->
        when (menu.itemId) {
            R.id.menu_help -> action.invoke()
        }
        return@setOnMenuItemClickListener true
    }
}

fun TextView.setColorSpannable(
    fullContent: String,
    coloredText: String,
    @ColorInt color: Int,
) {
    setSpannable(fullContent, coloredText, color)
}

fun TextView.setLinkSpannable(
    fullContent: String,
    clickableText: String,
    url : String,
) {
    val color = this.context.getColor(R.color.blue)
    setSpannable(fullContent, clickableText, color, url)
}


private fun TextView.setSpannable(
    fullContent: String,
    coloredText: String,
    @ColorInt color: Int,
    url: String? = null
) {
    val start = fullContent.indexOf(coloredText) // inclusive
    val end = start + coloredText.length // exclusive

    val spannable = SpannableString(fullContent)

    if (url != null) {
        spannable.setSpan(URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    spannable.setSpan(
        ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = spannable
    this.movementMethod = LinkMovementMethod.getInstance() //to ensure invoke onClick
}
