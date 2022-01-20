package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding

/**
 * @author Pinakin Kansara
 */
class VaccineRecordsAdapter(
    private val itemClickListener: ItemClickListener,
    private val itemDeleteListener: ItemDeleteListener,
    var canDeleteRecord: Boolean = false
) :
    ListAdapter<HealthRecordItem, VaccineRecordsAdapter.ViewHolder>(VaccineRecordDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(record: HealthRecordItem)
    }

    fun interface ItemDeleteListener {
        fun onDeleteClick(record: HealthRecordItem)
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
        val description =
            "${holder.itemView.context.getString(R.string.vaccinated)} • ${record.date}"
        holder.binding.tvVaccineStatus.text = description
        holder.binding.imgIcon.setImageResource(record.icon)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(record)
        }
        holder.binding.ivUnlink.isVisible = canDeleteRecord
        holder.binding.ivUnlink.setOnClickListener {
            itemDeleteListener.onDeleteClick(record)
        }
    }
}

class VaccineRecordDiffCallBacks : DiffUtil.ItemCallback<HealthRecordItem>() {
    override fun areItemsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
