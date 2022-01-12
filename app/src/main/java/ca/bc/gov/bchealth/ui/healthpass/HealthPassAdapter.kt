package ca.bc.gov.bchealth.ui.healthpass

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemMycardsCardsListBinding
import ca.bc.gov.common.model.ImmunizationStatus

/**
 * @author Pinakin Kansara
 */
class HealthPassAdapter(
    var healthPasses: MutableList<HealthPass>,
    private val qrCodeClickListener: QrCodeClickListener
) : RecyclerView.Adapter<HealthPassAdapter.ViewHolder>() {

    fun interface QrCodeClickListener {
        fun onQrCodeClicked(shcUri: String)
    }

    class ViewHolder(val binding: ItemMycardsCardsListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMycardsCardsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val healthPass = healthPasses[position]

        if (position == 0) {
            holder.binding.layoutQrCode.visibility = View.VISIBLE
        } else {
            holder.binding.layoutQrCode.visibility = View.GONE
        }

        holder.binding.txtFullName.text = healthPass.displayName()

        val issueDate = "Issued on ${healthPass.qrIssuedDate}"
        holder.binding.txtIssueDate.text = issueDate

        val passState = getHealthPassStat(healthPass.status, holder.binding.root.context)

        holder.binding.txtVaccineStatus.text = passState.status

        holder.binding.layoutVaccineStatus.setBackgroundColor(passState.color)

        holder.binding.layoutQrCode.setBackgroundColor(passState.color)

        holder.binding.imgQrCode.setImageBitmap(healthPass.qrCode)

        holder.binding.imgQrCode.setOnClickListener {
            qrCodeClickListener.onQrCodeClicked(healthPass.shcUri)
        }

        holder.binding.txtVaccineStatus
            .setCompoundDrawablesWithIntrinsicBounds(
                passState.icon, 0, 0, 0
            )
    }

    override fun getItemCount(): Int {
        return healthPasses.size
    }

    private fun getHealthPassStat(status: ImmunizationStatus?, context: Context): PassState =
        when (status) {
            ImmunizationStatus.FULLY_IMMUNIZED -> {
                PassState(
                    color = context.getColor(R.color.status_green),
                    context.resources
                        .getString(R.string.vaccinated),
                    R.drawable.ic_check_mark
                )
            }
            ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                PassState(
                    color = context.getColor(R.color.blue),
                    context.resources
                        .getString(R.string.partially_vaccinated),
                    0
                )
            }

            ImmunizationStatus.INVALID -> {
                PassState(
                    color = context.getColor(R.color.grey),
                    context.resources
                        .getString(R.string.no_record),
                    0
                )
            }
            else -> {
                throw IllegalStateException()
            }
        }
}

data class PassState(
    val color: Int,
    val status: String,
    val icon: Int
)