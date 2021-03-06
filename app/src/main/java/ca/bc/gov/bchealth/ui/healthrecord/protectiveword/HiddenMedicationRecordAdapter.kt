package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHiddenMedicationRecordsBinding
import ca.bc.gov.bchealth.ui.healthrecord.individual.HiddenMedicationRecordItem

class HiddenMedicationRecordAdapter(private val accessClickListener: ItemClickListener) :
    ListAdapter<HiddenMedicationRecordItem, HiddenMedicationRecordAdapter.HiddenMedicationRecordViewHolder>(
        HiddenMedicationRecordDiffCallBacks()
    ) {

    fun interface ItemClickListener {
        fun onItemClick(patientId: Long)
    }

    class HiddenMedicationRecordViewHolder(val binding: ItemHiddenMedicationRecordsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HiddenMedicationRecordViewHolder {
        return HiddenMedicationRecordViewHolder(
            ItemHiddenMedicationRecordsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HiddenMedicationRecordViewHolder, position: Int) {
        val record = getItem(position)
        holder.binding.tvTitle.text = record.title
        holder.binding.tvDesc.text = record.desc
        holder.binding.btnAccess.setOnClickListener {
            accessClickListener.onItemClick(record.patientId)
        }
    }
}

class HiddenMedicationRecordDiffCallBacks : DiffUtil.ItemCallback<HiddenMedicationRecordItem>() {
    override fun areItemsTheSame(oldItem: HiddenMedicationRecordItem, newItem: HiddenMedicationRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HiddenMedicationRecordItem, newItem: HiddenMedicationRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
