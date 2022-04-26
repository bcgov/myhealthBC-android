package ca.bc.gov.bchealth.ui.healthrecord

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordHeaderBinding
import ca.bc.gov.bchealth.databinding.ItemHealthRecordMemberBinding
import ca.bc.gov.common.model.AuthenticationStatus

/**
 * @author Pinakin Kansara
 */

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_MEMBER = 1

class HealthRecordsAdapter(
    private val itemClickListener: ItemClickListener,
) : ListAdapter<PatientHealthRecord, RecyclerView.ViewHolder>(
    PatientHealthRecordDiffCallBack()
) {

    class MemberViewHolder(val binding: ItemHealthRecordMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HeaderViewHolder(val binding: ItemHealthRecordHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                val binding = ItemHealthRecordHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            ITEM_VIEW_TYPE_MEMBER -> {
                val binding = ItemHealthRecordMemberBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MemberViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MemberViewHolder -> {
                val record = getItem(position)
                val totalRecords: String = if (record.totalRecord == 1)
                    "${record.totalRecord} record"
                else
                    "${record.totalRecord} records"

                holder.binding.tvNumberOfHealthRecords.text = totalRecords
                holder.binding.tvMemberName.text = record.name

                holder.itemView.setOnClickListener {
                    itemClickListener.onItemClick(record)
                }
            }
            is HeaderViewHolder -> {
                val record = getItem(position)
                val totalRecords: String = if (record.totalRecord == 1)
                    "${record.totalRecord} record"
                else
                    "${record.totalRecord} records"

                holder.binding.tvNumberOfHealthRecords.text = totalRecords
                holder.binding.tvMemberName.text = record.name

                holder.itemView.setOnClickListener {
                    itemClickListener.onItemClick(record)
                }
                if (record.authStatus == AuthenticationStatus.AUTHENTICATION_EXPIRED)
                    holder.binding.ivHealthRecord.setColorFilter(
                        holder.itemView.resources.getColor(R.color.primary_blue_disabled, null),
                        PorterDuff.Mode.SRC_IN
                    )
                else
                    holder.binding.ivHealthRecord.setColorFilter(
                        holder.itemView.resources.getColor(R.color.blue, null),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                return if (getItem(position).authStatus == AuthenticationStatus.AUTHENTICATED ||
                    getItem(position).authStatus == AuthenticationStatus.AUTHENTICATION_EXPIRED
                )
                    ITEM_VIEW_TYPE_HEADER
                else {
                    ITEM_VIEW_TYPE_MEMBER
                }
            }
            else -> ITEM_VIEW_TYPE_MEMBER
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
