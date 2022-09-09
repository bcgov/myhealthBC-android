package ca.bc.gov.bchealth.ui.recommendations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemRecommendationBinding
import ca.bc.gov.bchealth.utils.toggleVisibility

class RecommendationAdapter() : ListAdapter<Recommendation, RecommendationAdapter.ViewHolder>(
    RecommendationDiffCallBacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemRecommendationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val recommendation = getItem(position)

        tvTitle.text = recommendation.title
        tvStatus.text = recommendation.status
        tvDueDate.text = recommendation.date
        renderContentState(recommendation.fullContent)

        holder.itemView.setOnClickListener {
            recommendation.fullContent = recommendation.fullContent.not()
            renderContentState(recommendation.fullContent)
        }
    }

    private fun ItemRecommendationBinding.renderContentState(displayFullContent: Boolean) {
        groupFullContent.toggleVisibility(displayFullContent)
        ivContentState.isSelected = displayFullContent
    }

    class ViewHolder(val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root)
}

class RecommendationDiffCallBacks : DiffUtil.ItemCallback<Recommendation>() {
    override fun areItemsTheSame(oldItem: Recommendation, newItem: Recommendation) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Recommendation, newItem: Recommendation) =
        oldItem.title == newItem.title
}
