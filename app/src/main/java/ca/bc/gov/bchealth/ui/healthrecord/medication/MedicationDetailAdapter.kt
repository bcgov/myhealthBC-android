package ca.bc.gov.bchealth.ui.healthrecord.medication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemMedicationDetailBinding

/*
* Created by amit_metri on 16,February,2022
*/
class MedicationDetailAdapter() :
    ListAdapter<MedicationDetail, MedicationDetailAdapter.ViewHolder>(MedicationRecordsDiffCallBacks()) {

    class ViewHolder(val binding: ItemMedicationDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            ItemMedicationDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicationDetail = getItem(position)

        holder.binding.apply {
            tvTitle.text = medicationDetail.title
            tvDesc.text = medicationDetail.description
        }
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
