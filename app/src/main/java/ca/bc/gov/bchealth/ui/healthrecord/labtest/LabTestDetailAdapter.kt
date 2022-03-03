package ca.bc.gov.bchealth.ui.healthrecord.labtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemLabTestDetailBinding

/**
 * @author: Created by Rashmi Bambhania on 02,March,2022
 */
class LabTestDetailAdapter :
    ListAdapter<LabTestDetail, RecyclerView.ViewHolder>(LabTestRecordsDiffCallBacks()) {

    class RecordViewHolder(val binding: ItemLabTestDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HeaderViewHolder(val binding: ItemLabTestDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LabTestDetailViewModel.ITEM_VIEW_TYPE_HEADER -> {
                val binding = ItemLabTestDetailBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            LabTestDetailViewModel.ITEM_VIEW_TYPE_RECORD -> {
                val binding = ItemLabTestDetailBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                RecordViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val labTestDetail = getItem(position)
        when (holder) {
            is RecordViewHolder -> {
                holder.binding.apply {
                    tvHeader.visibility = View.GONE
                    tvTitle1.text = labTestDetail.title1
                    tvDesc1.text = labTestDetail.description1
                    tvTitle2.text = labTestDetail.title2
                    tvDesc2.text = labTestDetail.description2
                    tvTitle3.text = labTestDetail.title3
                    tvDesc3.text = labTestDetail.description3
                }
            }
            is HeaderViewHolder -> {
                holder.binding.apply {
                    tvHeader.text = labTestDetail.header
                    tvTitle1.text = labTestDetail.title1
                    tvDesc1.text = labTestDetail.description1
                    tvTitle2.text = labTestDetail.title2
                    tvDesc2.text = labTestDetail.description2
                    tvTitle3.text = labTestDetail.title3
                    tvDesc3.text = labTestDetail.description3
                }
            }
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
