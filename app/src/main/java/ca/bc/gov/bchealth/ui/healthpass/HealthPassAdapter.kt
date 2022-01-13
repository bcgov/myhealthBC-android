package ca.bc.gov.bchealth.ui.healthpass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemMycardsCardsListBinding

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

        holder.binding.apply {
            if (position == 0) {
                layoutQrCode.visibility = View.VISIBLE
            } else {
                layoutQrCode.visibility = View.GONE
            }
            txtFullName.text = healthPass.displayName()
            val issueDate = "Issued on ${healthPass.qrIssuedDate}"
            txtIssueDate.text = issueDate

            healthPass.status?.let {
                val passState = it.getHealthPassStatus(root.context)
                txtVaccineStatus.text = passState.status
                layoutVaccineStatus.setBackgroundColor(passState.color)
                layoutQrCode.setBackgroundColor(passState.color)
                imgQrCode.setImageBitmap(healthPass.qrCode)
                imgQrCode.setOnClickListener {
                    qrCodeClickListener.onQrCodeClicked(healthPass.shcUri)
                }
                txtVaccineStatus.setCompoundDrawablesWithIntrinsicBounds(
                    passState.icon, 0, 0, 0
                )
            }

        }
    }

    override fun getItemCount(): Int {
        return healthPasses.size
    }
}