package ca.bc.gov.bchealth.ui.healthrecords

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordMemberBinding
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord

/*
* Created by amit_metri on 23,November,2021
*/
class HealthRecordsAdapter(
    private var members: MutableList<HealthRecord>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<HealthRecordsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHealthRecordMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthRecordMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]

        holder.binding.tvMemberName.text = member.name

        val memberHealthRecordsCount = getHealthRecordsCount(member)

        holder.binding.tvNumberOfHealthRecords.text = getSubtitle(
            memberHealthRecordsCount, holder.itemView.resources
        )

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClicked(member)
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    private fun getHealthRecordsCount(healthRecord: HealthRecord): Int {
        var count = 0

        if (healthRecord.vaccineDataList.isNotEmpty())
            count++

        count += healthRecord.covidTestResultList.groupBy { it.combinedReportId }.size

        return count
    }

    private fun getSubtitle(memberHealthRecordsCount: Int, resources: Resources): String {
        return if (memberHealthRecordsCount > 1) {
            memberHealthRecordsCount.toString().plus(resources.getString(R.string.space_records))
        } else {
            memberHealthRecordsCount.toString().plus(resources.getString(R.string.space_record))
        }
    }

    fun interface OnItemClickListener {
        fun onItemClicked(healthRecord: HealthRecord)
    }
}
