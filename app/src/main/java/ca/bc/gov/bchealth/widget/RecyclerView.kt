package ca.bc.gov.bchealth.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

class RecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var emptyView: View? = null
    private var isLoading = false
    private var adaptersExcludedFromCount: MutableList<Class<out Adapter<*>>> = mutableListOf()

    private val observer = object : AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
    }

    fun excludeAdapterFromEmptyCount(adapter: Adapter<*>) {
        adaptersExcludedFromCount.add(adapter.javaClass)
    }

    private fun checkIfEmpty() {
        if (emptyView != null && adapter != null) {

            val excludedCount = getExcludedAdaptersCount()
            val adapterTotalCount = adapter?.itemCount ?: 0

            val emptyViewVisible = adapterTotalCount - excludedCount == 0 && isLoading.not()
            emptyView?.isVisible = emptyViewVisible
        }
    }

    /***
     * Returns the itemCount for adapters that don't relate to empty view
     */
    private fun getExcludedAdaptersCount(): Int {
        var total = 0

        adaptersExcludedFromCount.forEach { type ->
            val excludedAdapter =
                (adapter as? ConcatAdapter)?.adapters?.filterIsInstance(type)?.firstOrNull()

            total += (excludedAdapter?.itemCount ?: 0)
        }

        return total
    }

    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        checkIfEmpty()
    }
}
