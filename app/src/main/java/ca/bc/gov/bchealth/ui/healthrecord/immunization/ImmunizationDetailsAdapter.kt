package ca.bc.gov.bchealth.ui.healthrecord.immunization

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemImmunizationDetailBinding

/**
 * @author: Created by Rashmi Bambhania on 14,April,2022
 */
class ImmunizationDetailsAdapter :
    ListAdapter<ImmunizationDoseDetailItem, ImmunizationDetailsAdapter.ViewHolder>(
        ImmunizationRecordsDiffCallBacks()
    ) {

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
        val immunizationData = getItem(position)

        holder.binding.apply {
            tvDose.text = ""
            tvOccurrenceDate.text = immunizationData.date ?: "--"
            tvProduct.text = immunizationData.productName ?: "--"
            tvProvider.text = immunizationData.providerOrClinicName ?: "--"
            tvLotNumber.text = immunizationData.lotNumber ?: "--"
        }
    }
}

class ImmunizationRecordsDiffCallBacks : DiffUtil.ItemCallback<ImmunizationDoseDetailItem>() {
    override fun areItemsTheSame(
        oldItem: ImmunizationDoseDetailItem,
        newItem: ImmunizationDoseDetailItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ImmunizationDoseDetailItem,
        newItem: ImmunizationDoseDetailItem
    ): Boolean {
        return oldItem == newItem
    }
}
