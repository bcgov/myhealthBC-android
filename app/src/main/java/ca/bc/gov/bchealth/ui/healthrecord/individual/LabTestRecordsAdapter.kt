package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding

/**
 * @author Pinakin Kansara
 */
class LabTestRecordsAdapter(
    private val labTestRecordClickListener: LabTestRecordClickListener
) :
    ListAdapter<HealthRecordItem, LabTestRecordsAdapter.LabTestRecordsViewHolder>(LabTestRecordDiffCallBacks()) {

    fun interface LabTestRecordClickListener {
        fun onItemClick(record: HealthRecordItem)
    }

    class LabTestRecordsViewHolder(val binding: ItemHealthRecordsAbstractBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabTestRecordsViewHolder {
        val view = ItemHealthRecordsAbstractBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return LabTestRecordsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabTestRecordsViewHolder, position: Int) {
        val record = getItem(position)
        holder.binding.tvTitle.text = record.title
        holder.binding.tvDesc.text = record.description
        holder.binding.imgIcon.setImageResource(record.icon)
        holder.itemView.setOnClickListener {
            labTestRecordClickListener.onItemClick(record)
        }
    }
}

class LabTestRecordDiffCallBacks : DiffUtil.ItemCallback<HealthRecordItem>() {
    override fun areItemsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
