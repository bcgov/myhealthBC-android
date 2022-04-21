package ca.bc.gov.bchealth.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemCommentWithOptionsBinding
import ca.bc.gov.common.utils.toDateTimeString

/*
* Created by amit_metri on 18,April,2022
*/
class CommentsAdapter :
    ListAdapter<Comment, CommentsAdapter.CommentsViewHolder>(CommentsDiffCallBacks()) {

    class CommentsViewHolder(val binding: ItemCommentWithOptionsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val binding = ItemCommentWithOptionsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val comment = getItem(position)
        holder.binding.apply {
            tvComment.text = comment.text
            tvDateTime.text = comment.date?.toDateTimeString()
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
