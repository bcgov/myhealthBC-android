package ca.bc.gov.bchealth.ui.healthrecords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord

/*
* Created by amit_metri on 25,November,2021
*/
class IndividualHealthRecordAdapter(
        private var healthRecord: HealthRecord,
        var clickListener: ((HealthCardDto) -> Unit)? = null
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

        if (healthRecord.vaccineDataList.isNotEmpty()) {

            holder.binding.imgIcon.setImageResource(R.drawable.ic_health_record_vaccine)

            holder.binding.tvVaccineName.text = healthRecord.vaccineDataList.last()?.immunizingAgent

            when (healthRecord.immunizationStatus) {
                ImmunizationStatus.FULLY_IMMUNIZED -> {
                    holder.binding.tvVaccineStatus.text =
                            holder.itemView.resources.getString(R.string.vaccinated)
                                    .plus(" ")
                                    .plus(healthRecord.vaccineDataList.last()?.occurrenceDate)
                }
                ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                    holder.binding.tvVaccineStatus.text =
                            holder.itemView.resources.getString(R.string.partially_vaccinated)
                                    .plus(" ")
                                    .plus(healthRecord.vaccineDataList.last()?.occurrenceDate)
                }
                ImmunizationStatus.INVALID_QR_CODE -> {
                    holder.binding.tvVaccineStatus.text =
                            holder.itemView.resources.getString(R.string.no_record)
                }
            }
        }



        holder.itemView.setOnClickListener {
            // TODO: 25/11/21 to be implemented
        }
    }

    override fun getItemCount(): Int {
        if (healthRecord.vaccineDataList.isNotEmpty()) {
            return 1 + healthRecord.covidTestResultDataList.size
        } else {
            return healthRecord.covidTestResultDataList.size
        }

    }
}