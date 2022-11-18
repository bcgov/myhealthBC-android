package ca.bc.gov.bchealth.utils

import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import ca.bc.gov.bchealth.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout

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

fun TextInputLayout.validateEmptyInputLayout(@StringRes message: Int): Boolean {
    if (editText?.text.isNullOrEmpty()) {
        error = context.getString(message)
        showErrorState(this)
        return false
    }
    return true
}

private fun showErrorState(textInputLayout: TextInputLayout) {
    textInputLayout.isErrorEnabled = true
    textInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
        if (text != null && text.isNotEmpty()) {
            textInputLayout.isErrorEnabled = false
            textInputLayout.error = null
        }
    }
}

fun TextView.setColorSpannable(
    fullContent: String,
    coloredText: String,
    @ColorInt color: Int,
    isBold: Boolean = false
) {
    setSpannable(fullContent, coloredText, color, null, isBold)
}

private fun TextView.setSpannable(
    fullContent: String,
    coloredText: String,
    @ColorInt color: Int,
    url: String? = null,
    isBold: Boolean
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
    if (isBold) {
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.text = spannable
    this.movementMethod = LinkMovementMethod.getInstance() // to ensure invoke onClick
}
