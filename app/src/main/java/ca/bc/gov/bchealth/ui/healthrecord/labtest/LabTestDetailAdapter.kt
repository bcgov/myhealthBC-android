package ca.bc.gov.bchealth.ui.healthrecord.labtest

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemLabTestDetailBinding
import ca.bc.gov.bchealth.utils.makeLinks
import ca.bc.gov.bchealth.utils.redirect

/**
 * @author: Created by Rashmi Bambhania on 02,March,2022
 */
class LabTestDetailAdapter :
    ListAdapter<LabTestDetail, RecyclerView.ViewHolder>(LabTestRecordsDiffCallBacks()) {

    class LabOrderViewHolder(val binding: ItemLabTestDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LabTestViewHolder(val binding: ItemLabTestDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LabTestDetailViewModel.ITEM_VIEW_TYPE_LAB_ORDER -> {
                val binding = ItemLabTestDetailBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LabOrderViewHolder(binding)
            }
            LabTestDetailViewModel.ITEM_VIEW_TYPE_LAB_TEST -> {
                val binding = ItemLabTestDetailBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LabTestViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val labTestDetail = getItem(position)
        when (holder) {
            is LabOrderViewHolder -> {
                holder.binding.apply {
                    tvHeader.visibility = View.GONE
                    tvSummary.visibility = View.GONE
                    tvTitle1.text = labTestDetail.title1
                    tvDesc1.text = labTestDetail.timelineDateTime
                    tvTitle2.text = labTestDetail.title2
                    tvDesc2.text = labTestDetail.orderingProvider
                    tvTitle3.text = labTestDetail.title3
                    tvDesc3.text = labTestDetail.reportingSource
                }
            }
            is LabTestViewHolder -> {
                holder.binding.apply {
                    labTestDetail.header?.let {
                        tvHeader.text = it
                        tvHeader.visibility = View.VISIBLE
                    } ?: run {
                        tvHeader.visibility = View.GONE
                    }
                    labTestDetail.summary?.let {
                        tvSummary.text = it
                        holder.itemView.context.apply {
                            tvSummary.makeLinks(
                                Pair(
                                    getString(R.string.learn_more),
                                    View.OnClickListener {
                                        redirect(getString(R.string.faq_link))
                                    }
                                )
                            )
                        }
                        tvSummary.visibility = View.VISIBLE
                    } ?: run {
                        tvSummary.visibility = View.GONE
                    }
                    tvTitle1.text = labTestDetail.title1
                    tvDesc1.text = labTestDetail.testName

                    tvTitle2.text = labTestDetail.title2
                    val pair = getTestResult(labTestDetail.outOfRange, holder.itemView.context)
                    tvDesc2.text = pair.first
                    tvDesc2.setTextColor(pair.second)
                    val typeface =
                        ResourcesCompat.getFont(holder.itemView.context, R.font.bc_sans_bold)
                    tvDesc2.setTypeface(typeface, Typeface.BOLD)

                    tvTitle3.text = labTestDetail.title3
                    tvDesc3.text = labTestDetail.testStatus
                }
            }
        }
    }

    private fun getTestResult(outOfRange: Boolean?, context: Context): Pair<String, Int> {
        outOfRange?.let {
            return if (outOfRange) {
                Pair(
                    context.resources.getString(R.string.out_of_range),
                    context.resources.getColor(
                        R.color.error, null
                    )
                )
            } else {
                Pair(
                    context.resources.getString(R.string.in_range),
                    context.resources.getColor(
                        R.color.status_green, null
                    )
                )
            }
        } ?: run {
            return Pair(
                context.resources.getString(R.string.pending),
                context.resources.getColor(
                    R.color.text_black, null
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }
}

class LabTestRecordsDiffCallBacks : DiffUtil.ItemCallback<LabTestDetail>() {
    override fun areItemsTheSame(oldItem: LabTestDetail, newItem: LabTestDetail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LabTestDetail, newItem: LabTestDetail): Boolean {
        return oldItem == newItem
    }
}
