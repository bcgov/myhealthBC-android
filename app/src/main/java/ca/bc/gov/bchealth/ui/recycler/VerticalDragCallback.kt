package ca.bc.gov.bchealth.ui.recycler

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class VerticalDragCallback(
    private val onMoveAction: (Int, Int) -> Unit,
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        onMoveAction(
            viewHolder.absoluteAdapterPosition,
            target.absoluteAdapterPosition
        )
        viewHolder.bindingAdapter!!.notifyItemMoved(
            viewHolder.absoluteAdapterPosition,
            target.absoluteAdapterPosition
        )
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //do nothing
    }
}