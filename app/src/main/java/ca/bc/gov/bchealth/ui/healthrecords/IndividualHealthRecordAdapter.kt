package ca.bc.gov.bchealth.ui.healthrecords

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding
import ca.bc.gov.bchealth.model.healthrecords.IndividualRecord
import ca.bc.gov.bchealth.repository.HealthRecordType

/*
* Created by amit_metri on 25,November,2021
*/
class IndividualHealthRecordAdapter(
    var individualRecords: MutableList<IndividualRecord>,
    var canDeleteRecord: Boolean,
    private val onItemClickListener: OnItemClickListener,
    private val onDeleteListener: OnDeleteListener
) : RecyclerView.Adapter<IndividualHealthRecordAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHealthRecordsAbstractBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthRecordsAbstractBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val individualRecord = individualRecords[position]

        when (individualRecord.healthRecordType) {
            HealthRecordType.VACCINE_RECORD -> {
                holder.binding.imgIcon.setImageResource(R.drawable.ic_health_record_vaccine)
            }
            HealthRecordType.COVID_TEST_RECORD -> {
                holder.binding.imgIcon.setImageResource(R.drawable.ic_health_record_covid_test)
            }
        }

        holder.binding.tvVaccineName.text = individualRecord.title
        holder.binding.tvVaccineStatus.text = individualRecord.subtitle

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClicked(individualRecord)
        }

        if (canDeleteRecord) {
            holder.binding.ivUnlink.visibility = View.VISIBLE
            holder.binding.ivUnlink.setOnClickListener {
                onDeleteListener.onItemDeleted(individualRecord)
            }
        } else {
            holder.binding.ivUnlink.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return individualRecords.size
    }

    fun interface OnItemClickListener {
        fun onItemClicked(individualRecord: IndividualRecord)
    }

    fun interface OnDeleteListener {
        fun onItemDeleted(individualRecord: IndividualRecord)
    }
}
