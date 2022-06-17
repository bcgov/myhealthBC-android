package ca.bc.gov.bchealth.ui.healthrecord.medication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemMedicationDetailBinding
import ca.bc.gov.bchealth.databinding.ItemMedicationDetailDirectionsBinding
import ca.bc.gov.bchealth.ui.healthrecord.medication.MedicationDetailsViewModel.Companion.ITEM_VIEW_TYPE_DIRECTIONS
import ca.bc.gov.bchealth.ui.healthrecord.medication.MedicationDetailsViewModel.Companion.ITEM_VIEW_TYPE_RECORD
import ca.bc.gov.bchealth.utils.showIfNullOrBlank

/*
* Created by amit_metri on 16,February,2022
*/
class MedicationDetailAdapter :
    ListAdapter<MedicationDetail, RecyclerView.ViewHolder>(MedicationRecordsDiffCallBacks()) {

    class RecordViewHolder(val binding: ItemMedicationDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DirectionsViewHolder(val binding: ItemMedicationDetailDirectionsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_RECORD -> {
                val binding = ItemMedicationDetailBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                RecordViewHolder(binding)
            }
            ITEM_VIEW_TYPE_DIRECTIONS -> {
                val binding = ItemMedicationDetailDirectionsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                DirectionsViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val medicationDetail = getItem(position)
        when (holder) {
            is RecordViewHolder -> {
                holder.binding.apply {
                    tvTitle.text = medicationDetail.title
                    tvDesc.text =
                        medicationDetail.description.showIfNullOrBlank(holder.itemView.context)
                }
            }
            is DirectionsViewHolder -> {
                holder.binding.apply {
                    tvTitle.text = medicationDetail.title
                    tvDesc.text =
                        medicationDetail.description.showIfNullOrBlank(holder.itemView.context)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }
}

class MedicationRecordsDiffCallBacks : DiffUtil.ItemCallback<MedicationDetail>() {
    override fun areItemsTheSame(oldItem: MedicationDetail, newItem: MedicationDetail): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: MedicationDetail, newItem: MedicationDetail): Boolean {
        return oldItem == newItem
    }
}
