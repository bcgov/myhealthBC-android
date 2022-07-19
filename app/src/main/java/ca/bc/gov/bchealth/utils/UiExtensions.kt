package ca.bc.gov.bchealth.utils

import android.graphics.Paint
import android.widget.TextView

fun TextView.underlineText() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}
