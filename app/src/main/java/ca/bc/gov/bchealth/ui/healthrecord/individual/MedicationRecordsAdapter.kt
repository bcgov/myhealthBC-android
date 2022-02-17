package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding

class MedicationRecordsAdapter(
    private val itemClickListener: ItemClickListener,
) :
    ListAdapter<HealthRecordItem, MedicationRecordsAdapter.ViewHolder>(MedicationRecordDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(record: HealthRecordItem)
    }

    class ViewHolder(val binding: ItemHealthRecordsAbstractBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemHealthRecordsAbstractBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        holder.binding.tvVaccineName.text = record.title
        val description = "${record.description} • ${record.date}"
        holder.binding.tvVaccineStatus.text = description
        holder.binding.imgIcon.setImageResource(record.icon)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(record)
        }
    }
}

class MedicationRecordDiffCallBacks : DiffUtil.ItemCallback<HealthRecordItem>() {
    override fun areItemsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
