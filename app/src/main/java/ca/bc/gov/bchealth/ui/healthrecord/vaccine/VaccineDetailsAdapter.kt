package ca.bc.gov.bchealth.ui.healthrecord.vaccine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemVaccineDetailsBinding
import ca.bc.gov.bchealth.utils.showIfNullOrBlank
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.utils.toDate

/*
* @author amit_metri on 09,December,2021
*/
class VaccineDetailsAdapter :
    ListAdapter<VaccineDoseDto, VaccineDetailsAdapter.ViewHolder>(VaccineRecordsDiffCallBacks()) {

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

            tvOccurrenceDate.text = vaccineData.date.toDate()

            tvProduct.text = vaccineData.productName.showIfNullOrBlank(holder.itemView.context)

            tvProvider.text = vaccineData.providerName.showIfNullOrBlank(holder.itemView.context)

            tvLotNumber.text = vaccineData.lotNumber.showIfNullOrBlank(holder.itemView.context)
        }
    }
}

class VaccineRecordsDiffCallBacks : DiffUtil.ItemCallback<VaccineDoseDto>() {
    override fun areItemsTheSame(oldItem: VaccineDoseDto, newItem: VaccineDoseDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VaccineDoseDto, newItem: VaccineDoseDto): Boolean {
        return oldItem == newItem
    }
}
