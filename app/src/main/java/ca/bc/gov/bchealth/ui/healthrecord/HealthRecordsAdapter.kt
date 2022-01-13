package ca.bc.gov.bchealth.ui.healthrecord

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordMemberBinding
import ca.bc.gov.repository.PatientHealthRecord
import ca.bc.gov.repository.name

/**
 * @author Pinakin Kansara
 */
class HealthRecordsAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<PatientHealthRecord, HealthRecordsAdapter.ViewHolder>(
    PatientHealthRecordDiffCallBack()
) {

    class ViewHolder(val binding: ItemHealthRecordMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemHealthRecordMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        val totalRecords: String
        if(record.totalRecord == 1)
            totalRecords = "${record.totalRecord} record"
        else
            totalRecords = "${record.totalRecord} records"

        holder.binding.tvNumberOfHealthRecords.text = totalRecords
        holder.binding.tvMemberName.text = record.name()

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(record)
        }
    }
}

class PatientHealthRecordDiffCallBack : DiffUtil.ItemCallback<PatientHealthRecord>() {
    override fun areItemsTheSame(
        oldItem: PatientHealthRecord,
        newItem: PatientHealthRecord
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: PatientHealthRecord,
        newItem: PatientHealthRecord
    ): Boolean {
        return oldItem.patientId == newItem.patientId
    }
}

fun interface ItemClickListener {
    fun onItemClick(record: PatientHealthRecord)
}