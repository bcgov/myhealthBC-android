package ca.bc.gov.bchealth.ui.healthrecords.vaccinerecords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemVaccineDetailsBinding
import ca.bc.gov.bchealth.model.healthrecords.VaccineData
import ca.bc.gov.bchealth.utils.getDateForIndividualHealthRecord
import javax.inject.Inject

/*
* @author amit_metri on 09,December,2021
*/
class VaccineDetailsAdapter @Inject constructor(
    var vaccineDataList: List<VaccineData?>
) : RecyclerView.Adapter<VaccineDetailsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemVaccineDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVaccineDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val vaccineData = vaccineDataList[position]

        holder.binding.apply {
            tvDose.text = holder.itemView.resources
                .getString(R.string.dose).plus(" ").plus(vaccineData?.doseNumber)

            tvOccurrenceDate.text = vaccineData?.occurrenceDate?.getDateForIndividualHealthRecord()

            tvProduct.text = vaccineData?.product

            tvProvider.text = vaccineData?.provider

            tvLotNumber.text = vaccineData?.lotNumber
        }
    }

    override fun getItemCount(): Int {
        return vaccineDataList.size
    }
}
