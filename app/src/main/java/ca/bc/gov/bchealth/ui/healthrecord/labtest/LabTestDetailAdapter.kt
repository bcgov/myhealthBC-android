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
import ca.bc.gov.bchealth.databinding.ItemLabTestDetailBannerBinding
import ca.bc.gov.bchealth.databinding.ItemLabTestDetailBinding
import ca.bc.gov.bchealth.utils.makeLinks
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showIfNullOrBlank

/**
 * @author: Created by Rashmi Bambhania on 02,March,2022
 */
class LabTestDetailAdapter :
    ListAdapter<LabTestDetail, RecyclerView.ViewHolder>(LabTestRecordsDiffCallBacks()) {

    class LabTestBannerViewHolder(val binding: ItemLabTestDetailBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LabOrderViewHolder(val binding: ItemLabTestDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LabTestViewHolder(val binding: ItemLabTestDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LabTestDetailViewModel.ITEM_VIEW_TYPE_LAB_TEST_BANNER -> {
                val binding = ItemLabTestDetailBannerBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LabTestBannerViewHolder(binding)
            }
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
            is LabTestBannerViewHolder -> displayBanner(holder, labTestDetail)
            is LabOrderViewHolder -> displayLabOrder(holder, labTestDetail)
            is LabTestViewHolder -> displayLabTest(holder, labTestDetail)
        }
    }

    private fun displayBanner(holder: LabTestBannerViewHolder, labTestDetail: LabTestDetail) {
        holder.binding.apply {
            holder.itemView.context.apply {
                tvTitle.text = labTestDetail.bannerHeader?.let { getString(it) }
                tvInfo.text = labTestDetail.bannerText?.let { getString(it) }
                labTestDetail.bannerClickableText?.let {
                    tvInfo.makeLinks(
                        getString(it) to View.OnClickListener {
                            redirect(getString(R.string.faq_link))
                        },
                    )
                }
            }
        }
    }

    private fun displayLabOrder(holder: LabOrderViewHolder, labTestDetail: LabTestDetail) {
        holder.binding.apply {
            tvHeader.visibility = View.GONE
            tvSummary.visibility = View.GONE
            tvDesc1.text =
                labTestDetail.timelineDateTime.showIfNullOrBlank(holder.itemView.context)
            tvDesc2.text =
                labTestDetail.orderingProvider.showIfNullOrBlank(holder.itemView.context)
            tvDesc3.text =
                labTestDetail.reportingSource.showIfNullOrBlank(holder.itemView.context)
            holder.itemView.context.apply {
                tvTitle1.text = labTestDetail.title1?.let { getString(it) }
                tvTitle2.text = labTestDetail.title2?.let { getString(it) }
                tvTitle3.text = labTestDetail.title3?.let { getString(it) }
            }
        }
    }

    private fun displayLabTest(holder: LabTestViewHolder, labTestDetail: LabTestDetail) {
        holder.binding.apply {
            labTestDetail.header?.let {
                tvHeader.text = holder.itemView.context.getString(it)
                tvHeader.visibility = View.VISIBLE
            } ?: run {
                tvHeader.visibility = View.GONE
            }
            labTestDetail.summary?.let {
                holder.itemView.context.apply {
                    tvSummary.text = getString(it)
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
            holder.itemView.context.apply {
                tvTitle1.text = labTestDetail.title1?.let { getString(it) }
                tvTitle2.text = labTestDetail.title2?.let { getString(it) }
                tvTitle3.text = labTestDetail.title3?.let { getString(it) }
            }

            tvDesc1.text = labTestDetail.testName.showIfNullOrBlank(holder.itemView.context)

            val pair = getTestResult(
                labTestDetail.isOutOfRange,
                labTestDetail.testStatus,
                holder.itemView.context
            )
            tvDesc2.text = pair.first.showIfNullOrBlank(holder.itemView.context)
            tvDesc2.setTextColor(pair.second)
            val typeface =
                ResourcesCompat.getFont(holder.itemView.context, R.font.bc_sans_bold)
            tvDesc2.setTypeface(typeface, Typeface.BOLD)

            tvDesc3.text = labTestDetail.testStatus?.let {
                holder.itemView.context.getString(
                    it
                )
            }
        }
    }

    private fun getTestResult(
        outOfRange: Boolean?,
        testStatus: Int?,
        context: Context
    ): Pair<String, Int> {
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
                testStatus?.let { context.resources.getString(it) } ?: run { "" },
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
