package ca.bc.gov.bchealth.utils

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.scrollToBottom() {
    this.adapter?.let {
        this.smoothScrollToPosition(it.itemCount - 1)
    }
}
