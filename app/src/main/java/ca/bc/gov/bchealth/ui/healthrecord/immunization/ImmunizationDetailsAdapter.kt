package ca.bc.gov.bchealth.ui.healthrecord.immunization

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemImmunizationDetailBinding
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.utils.toDate

/**
 * @author: Created by Rashmi Bambhania on 14,April,2022
 */
class ImmunizationDetailsAdapter :
    ListAdapter<VaccineDoseDto, ImmunizationDetailsAdapter.ViewHolder>(ImmunizationRecordsDiffCallBacks()) {

    class ViewHolder(val binding: ItemImmunizationDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            ItemImmunizationDetailBinding.inflate(
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

            tvProduct.text = vaccineData.productName

            tvProvider.text = vaccineData.providerName

            tvLotNumber.text = vaccineData.lotNumber
        }
    }
}

class ImmunizationRecordsDiffCallBacks : DiffUtil.ItemCallback<VaccineDoseDto>() {
    override fun areItemsTheSame(oldItem: VaccineDoseDto, newItem: VaccineDoseDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VaccineDoseDto, newItem: VaccineDoseDto): Boolean {
        return oldItem == newItem
    }
}
