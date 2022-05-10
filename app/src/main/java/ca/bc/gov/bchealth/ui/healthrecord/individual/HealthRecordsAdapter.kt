package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding
import ca.bc.gov.common.utils.toDate

/**
 * @author Pinakin Kansara
 */
class HealthRecordsAdapter(
    private val itemClickListener: ItemClickListener
) :
    ListAdapter<HealthRecordItem, HealthRecordsAdapter.ViewHolder>(HealthRecordDiffCallBacks()) {

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
        holder.binding.tvTitle.text = record.title
        var description = ""
        holder.binding.imgIcon.setImageResource(record.icon)
        when (record.healthRecordType) {
            HealthRecordType.VACCINE_RECORD -> {
                description = record.date.toDate()
            }
            HealthRecordType.COVID_TEST_RECORD -> {
                description = "${record.testOutcome} • ${record.date.toDate()}"
            }
            HealthRecordType.MEDICATION_RECORD -> {
                description = "${record.description} • ${record.date.toDate()}"
            }
            HealthRecordType.LAB_TEST -> {
                description = record.description
            }
        }

        holder.binding.tvDesc.text = description
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(record)
        }
    }
}

class HealthRecordDiffCallBacks : DiffUtil.ItemCallback<HealthRecordItem>() {
    override fun areItemsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
