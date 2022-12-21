package ca.bc.gov.bchealth.utils

import android.view.View
import androidx.core.widget.NestedScrollView

fun NestedScrollView?.scrollToBottom() {
    this?.let {
        it.post {
            it.fullScroll(View.FOCUS_DOWN)
        }
    }
}
