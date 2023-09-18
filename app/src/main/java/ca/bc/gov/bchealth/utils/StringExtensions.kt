package ca.bc.gov.bchealth.utils

import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat

fun String?.orPlaceholder(placeholder: String = "--"): String =
    this ?: placeholder

fun String?.orPlaceholderIfNullOrBlank(placeholder: String = "--"): String =
    if (this.isNullOrBlank()) placeholder else this

fun String.fromHtml(): Spanned =
    Html.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
