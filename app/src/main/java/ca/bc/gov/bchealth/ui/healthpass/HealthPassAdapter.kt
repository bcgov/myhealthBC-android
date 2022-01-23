package ca.bc.gov.bchealth.ui.healthpass

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthPassCardBinding

/**
 * @author Pinakin Kansara
 */
class HealthPassAdapter(
    private val qrCodeClickListener: QrCodeClickListener,
    private val federalPassClickListener: FederalPassClickListener,
    private val itemClickListener: ItemClickListener
) : ListAdapter<HealthPass, HealthPassAdapter.ViewHolder>(HealthPassDiffCallBack()) {

    fun interface QrCodeClickListener {
        fun onQrCodeClicked(shcUri: String)
    }

    fun interface ItemClickListener {
        fun onItemClicked(pass: HealthPass)
    }

    fun interface FederalPassClickListener {
        fun onFederalPassClicked(patientId: Long, federalPass: String?)
    }

    class ViewHolder(val binding: ItemHealthPassCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthPassCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val healthPass = getItem(position)

        holder.binding.apply {

            layoutQrCode.isVisible = healthPass.isExpanded

            holder.itemView.setOnClickListener {
                itemClickListener.onItemClicked(healthPass)
            }

            txtFullName.text = healthPass.name
            val issueDate = healthPass.qrIssuedDate
            txtIssueDate.text = issueDate

            txtVaccineStatus.setText(healthPass.state.status)
            layoutVaccineStatus.setBackgroundColor(holder.itemView.context.getColor(healthPass.state.color))
            layoutQrCode.setBackgroundColor(holder.itemView.context.getColor(healthPass.state.color))
            imgQrCode.setImageBitmap(healthPass.qrCode)
            imgQrCode.setOnClickListener {
                qrCodeClickListener.onQrCodeClicked(healthPass.shcUri)
            }
            txtVaccineStatus.setCompoundDrawablesWithIntrinsicBounds(
                healthPass.state.icon, 0, 0, 0
            )

            tvFederalPassTitle.setText(healthPass.federalTravelPassState.title)
            ivFederalPassAction.setImageResource(healthPass.federalTravelPassState.icon)

            viewFederalProof.setOnClickListener {
                federalPassClickListener.onFederalPassClicked(
                    healthPass.patientId,
                    healthPass.federalTravelPassState.pdf
                )
            }
        }
    }

    class HealthPassDiffCallBack : DiffUtil.ItemCallback<HealthPass>() {
        override fun areItemsTheSame(
            oldItem: HealthPass,
            newItem: HealthPass
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HealthPass,
            newItem: HealthPass
        ): Boolean {
            return oldItem.patientId == newItem.patientId
        }
    }
}
