package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding
import ca.bc.gov.bchealth.ui.healthpass.getHealthPassStatus
import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.common.utils.toDateTimeString

/**
 * @author Pinakin Kansara
 */
class VaccineRecordsAdapter(
    private val itemClickListener: ItemClickListener
) :
    ListAdapter<VaccineRecord, VaccineRecordsAdapter.ViewHolder>(HealthPassDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(record: VaccineRecord)
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
        val pass = getItem(position)
        val name = holder.itemView.resources.getString(R.string.covid_19_vaccination)
        holder.binding.tvVaccineName.text = name
        holder.binding.tvVaccineStatus.text =
            pass.status.getHealthPassStatus(holder.itemView.context).status
            .plus(IndividualHealthRecordViewModel.bulletPoint)
            .plus(pass.qrIssueDate.toDateTimeString())

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(pass)
        }
    }
}



class HealthPassDiffCallBacks : DiffUtil.ItemCallback<VaccineRecord>() {
    override fun areItemsTheSame(oldItem: VaccineRecord, newItem: VaccineRecord): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: VaccineRecord, newItem: VaccineRecord): Boolean {
        return oldItem.status == newItem.status
    }
}