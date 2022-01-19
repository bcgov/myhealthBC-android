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
class TestRecordsAdapter(
    private val itemClickListener: ItemClickListener
) :
    ListAdapter<HealthRecordItem, TestRecordsAdapter.ViewHolder>(TestRecordsDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(result: HealthRecordItem)
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
        holder.binding.tvVaccineName.setText(record.title)
        holder.binding.tvVaccineStatus.text = record.date
        holder.binding.imgIcon.setImageResource(record.icon)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(record)
        }
    }
}

class TestRecordsDiffCallBacks : DiffUtil.ItemCallback<HealthRecordItem>() {
    override fun areItemsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
