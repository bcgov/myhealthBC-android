package ca.bc.gov.bchealth.ui.healthrecord.vaccine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemVaccineDetailsBinding
import ca.bc.gov.common.model.VaccineDose
import ca.bc.gov.common.utils.toDateTimeString

/*
* @author amit_metri on 09,December,2021
*/
class VaccineDetailsAdapter :
    ListAdapter<VaccineDose, VaccineDetailsAdapter.ViewHolder>(VaccineRecordsDiffCallBacks()) {

    class ViewHolder(val binding: ItemVaccineDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            ItemVaccineDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaccineData = getItem(position)

        holder.binding.apply {
            tvDose.text = holder.itemView.resources.getString(R.string.dose)
                .plus(" ")
                .plus(position + 1)

            tvOccurrenceDate.text = vaccineData.date.toDateTimeString()

            tvProduct.text = vaccineData.productName

            tvProvider.text = vaccineData.providerName

            tvLotNumber.text = vaccineData.lotNumber
        }
    }
}

class VaccineRecordsDiffCallBacks : DiffUtil.ItemCallback<VaccineDose>() {
    override fun areItemsTheSame(oldItem: VaccineDose, newItem: VaccineDose): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VaccineDose, newItem: VaccineDose): Boolean {
        return oldItem == newItem
    }
}
