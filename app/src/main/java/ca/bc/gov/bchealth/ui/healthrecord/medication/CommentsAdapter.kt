package ca.bc.gov.bchealth.ui.healthrecord.medication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemCommentBinding
import ca.bc.gov.bchealth.databinding.ItemCommentsCountBinding
import ca.bc.gov.common.utils.toDateTimeString

/**
 * @author Pinakin Kansara
 */
class CommentsAdapter(
    private val itemClickListener: ItemClickListener
) :
    ListAdapter<Comment, RecyclerView.ViewHolder>(CommentsDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(parentEntryId: String)
    }

    class CommentsCountViewHolder(val binding: ItemCommentsCountBinding) :
        RecyclerView.ViewHolder(binding.root)

    class CommentsViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MedicationDetailsViewModel.ITEM_VIEW_TYPE_COMMENTS_COUNT -> {
                val binding = ItemCommentsCountBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CommentsCountViewHolder(binding)
            }
            MedicationDetailsViewModel.ITEM_VIEW_TYPE_COMMENTS -> {
                val binding = ItemCommentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CommentsViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comment = getItem(position)
        when (holder) {
            is CommentsCountViewHolder -> {
                holder.binding.tvCommentsCount.setTextColor(
                    holder.itemView.resources.getColor(R.color.blue, null)
                )
                holder.binding.tvCommentsCount.text =
                    comment.text?.let {
                        holder.itemView.context.resources.getQuantityString(
                            R.plurals.comments, it.toInt(), it.toInt()
                        )
                    }

                holder.itemView.setOnClickListener {
                    comment.parentEntryId?.let { it1 -> itemClickListener.onItemClick(it1) }
                }
            }

            is CommentsViewHolder -> {
                holder.binding.tvComment.text = comment.text
                holder.binding.tvDateTime.text = comment.date?.toDateTimeString()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                MedicationDetailsViewModel.ITEM_VIEW_TYPE_COMMENTS_COUNT
            }
            else -> {
                MedicationDetailsViewModel.ITEM_VIEW_TYPE_COMMENTS
            }
        }
    }
}

class CommentsDiffCallBacks : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}
