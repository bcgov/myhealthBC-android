package ca.bc.gov.bchealth.ui.recommendations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemRecommendationBinding
import ca.bc.gov.bchealth.utils.orPlaceholder
import ca.bc.gov.bchealth.utils.setColorSpannable
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.common.model.immunization.ForecastStatus

class RecommendationAdapter : ListAdapter<RecommendationDetailItem, RecommendationAdapter.ViewHolder>(
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

        val icon = if (recommendation.status is ForecastStatus.Completed) {
            R.drawable.ic_recommendation_checked
        } else {
            R.drawable.ic_recommendation
        }
        ivIcon.setImageDrawable(
            AppCompatResources.getDrawable(this.root.context, icon)
        )

        val fullStatus: String
        val statusOrPlaceholder: String = recommendation.status?.text.orPlaceholder()
        val date: String
        this.root.context.apply {
            fullStatus = getString(R.string.immnz_forecast_status, statusOrPlaceholder)
            date = getString(R.string.immnz_forecast_due_date, recommendation.date)
        }

        val colorId: Int = when (recommendation.status) {
            is ForecastStatus.Eligible -> R.color.status_green
            is ForecastStatus.Overdue -> R.color.status_red
            else -> R.color.status_grey
        }

        tvStatus.setColorSpannable(
            fullStatus,
            statusOrPlaceholder,
            this.root.context.getColor(colorId),
            true
        )
        tvDueDate.text = date

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

class RecommendationDiffCallBacks : DiffUtil.ItemCallback<RecommendationDetailItem>() {
    override fun areItemsTheSame(oldItem: RecommendationDetailItem, newItem: RecommendationDetailItem) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: RecommendationDetailItem, newItem: RecommendationDetailItem) =
        oldItem.title == newItem.title
}
