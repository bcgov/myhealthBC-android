package ca.bc.gov.bchealth.ui.healthrecords

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordMemberBinding
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord

/*
* Created by amit_metri on 23,November,2021
*/
class HealthRecordsAdapter(
    private var members: MutableList<HealthRecord>
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
            val action = HealthRecordsFragmentDirections
                .actionHealthRecordsFragmentToIndividualHealthRecordFragment(member)
            holder.itemView.findNavController().navigate(action)
        }
    }

    private fun getHealthRecordsCount(healthRecord: HealthRecord): Int {
        var count = 0

        if (healthRecord.vaccineDataList.isNotEmpty())
            count++

        count += healthRecord.covidTestResultList.size

        return count
    }

    override fun getItemCount(): Int {
        return members.size
    }

    private fun getSubtitle(memberHealthRecordsCount: Int, resources: Resources): String {
        return if (memberHealthRecordsCount > 1) {
            memberHealthRecordsCount.toString().plus(resources.getString(R.string.space_records))
        } else {
            memberHealthRecordsCount.toString().plus(resources.getString(R.string.space_record))
        }
    }
}
