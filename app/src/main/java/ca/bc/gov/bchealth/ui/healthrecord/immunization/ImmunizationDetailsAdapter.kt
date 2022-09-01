package ca.bc.gov.bchealth.ui.healthrecord.immunization

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemImmunizationDetailBinding
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.orPlaceholder
import ca.bc.gov.bchealth.utils.setColorSpannable
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.common.model.immunization.ForecastStatus

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
            tvOccurrenceDate.text = immunizationData.date.orPlaceholder()
            tvProduct.text = immunizationData.productName.orPlaceholder()
            tvImmunizingAgent.text = immunizationData.immunizingAgent.orPlaceholder()
            tvProvider.text = immunizationData.providerOrClinicName.orPlaceholder()
            tvLotNumber.text = immunizationData.lotNumber.orPlaceholder()

            immunizationData.forecast?.let {
                displayForecast(it)
            } ?: viewForecast.hide()
        }
    }

    private fun ItemImmunizationDetailBinding.displayForecast(forecast: ForecastDetailItem) {
        val fullStatus: String
        val statusOrPlaceholder: String = forecast.status?.text.orPlaceholder()
        val date: String
        this.root.context.apply {
            fullStatus = getString(R.string.immnz_forecast_status, statusOrPlaceholder)
            date = getString(R.string.immnz_forecast_due_date, forecast.date)
        }

        val icon: Int
        val colorId: Int

        when (forecast.status) {
            ForecastStatus.Eligible -> {
                icon = R.drawable.ic_forecast_green
                colorId = R.color.status_green
            }

            ForecastStatus.Overdue -> {
                icon = R.drawable.ic_forecast_grey
                colorId = R.color.status_red
            }

            else -> {
                icon = R.drawable.ic_forecast_grey
                colorId = R.color.status_grey
            }
        }

        includeForecast.apply {
            ivIcon.setBackgroundResource(icon)
            tvTitle.text = forecast.name
            tvStatus.setColorSpannable(
                fullStatus,
                statusOrPlaceholder,
                this.root.context.getColor(colorId)
            )
            tvDueDate.text = date
        }
        viewForecast.show()
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
