package ca.bc.gov.bchealth.ui.healthrecords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordMemberBinding
import ca.bc.gov.bchealth.model.HealthCardDto

/*
* Created by amit_metri on 23,November,2021
*/
class HealthRecordsAdapter(
        private var members: MutableList<HealthCardDto>
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
        holder.binding.tvNumberOfHealthRecords.text = "1 record"
        // TODO: 24/11/21 Hard coded value will remain until covid test results are available.

        holder.itemView.setOnClickListener {
            val action = HealthRecordsFragmentDirections
                    .actionHealthRecordsFragmentToIndividualHealthRecordFragment(member)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }
}
