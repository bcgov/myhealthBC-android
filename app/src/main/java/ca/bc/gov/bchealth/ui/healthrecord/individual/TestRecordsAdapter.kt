package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.View
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
class TestRecordsAdapter(
    private val itemClickListener: ItemClickListener,
    private val itemDeleteListener: ItemDeleteListener,
    private val itemUpdateListener: ItemUpdateListener,
    var canDeleteRecord: Boolean = false,
    var isUpdateRequested: Boolean
) :
    ListAdapter<HealthRecordItem, TestRecordsAdapter.ViewHolder>(TestRecordsDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(result: HealthRecordItem)
    }

    fun interface ItemDeleteListener {
        fun onDeleteClick(record: HealthRecordItem)
    }

    fun interface ItemUpdateListener {
        fun onUpdateRequested(patientId: Long, testResultId: Long)
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
        val detail = "${record.testOutcome} • ${record.date}"
        holder.binding.tvVaccineStatus.text = detail
        holder.binding.imgIcon.setImageResource(record.icon)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(record)
        }
        holder.binding.ivUnlink.isVisible = canDeleteRecord
        holder.binding.ivUnlink.setOnClickListener {
            itemDeleteListener.onDeleteClick(record)
        }
        /*
        * Retry fetching the covid test result for pending record
        * */
        if (record.testOutcome == holder.itemView.resources.getString(R.string.pending) &&
            isUpdateRequested
        ) {
            holder.binding.progressBar.visibility = View.VISIBLE
            holder.binding.ivRightArrow.visibility = View.INVISIBLE
            itemUpdateListener.onUpdateRequested(record.patientId, record.testResultId)
            isUpdateRequested = false
        } else {
            holder.binding.progressBar.visibility = View.INVISIBLE
            holder.binding.ivRightArrow.visibility = View.VISIBLE
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
