package ca.bc.gov.bchealth.utils

import android.graphics.Paint
import android.widget.TextView
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
