package ca.bc.gov.bchealth.ui.healthrecord.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemCommentsCountBinding
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.common.utils.toDateTimeString

/**
 * @author Pinakin Kansara
 */
class RecordCommentsAdapter(
    private val onItemClick: (CommentsSummary) -> Unit
) : ListAdapter<CommentsSummary, RecordCommentsAdapter.CommentsCountViewHolder>(
    CommentsDiffCallBacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsCountViewHolder {
        val binding = ItemCommentsCountBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommentsCountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsCountViewHolder, position: Int) {
        val summary = getItem(position)

        holder.itemView.setOnClickListener {
            onItemClick(summary)
        }

        holder.binding.apply {
            tvCommentsCount.text = holder.itemView.context.resources.getQuantityString(
                R.plurals.plurals_comments, summary.count, summary.count
            )

            tvComment.text = summary.text
            tvDateTime.text = if (summary.isUploaded && summary.date != null) {
                summary.date.toDateTimeString()
            } else {
                holder.itemView.context.getString(R.string.posting)
            }
        }
    }

    class CommentsCountViewHolder(val binding: ItemCommentsCountBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class CommentsDiffCallBacks : DiffUtil.ItemCallback<CommentsSummary>() {
    override fun areItemsTheSame(oldItem: CommentsSummary, newItem: CommentsSummary): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: CommentsSummary, newItem: CommentsSummary): Boolean {
        return oldItem == newItem
    }
}
