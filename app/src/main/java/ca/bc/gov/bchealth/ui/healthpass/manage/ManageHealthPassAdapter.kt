package ca.bc.gov.bchealth.ui.healthpass.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemMycardsCardsListBinding
import ca.bc.gov.bchealth.ui.healthpass.HealthPass
import ca.bc.gov.bchealth.ui.healthpass.displayName
import ca.bc.gov.bchealth.utils.getHealthPassStatus

/*
* Created by amit_metri on 12,January,2022
*/
class ManageHealthPassAdapter(
    var healthPasses: List<HealthPass>,
    private val deleteClickListener: DeleteClickListener
) : RecyclerView.Adapter<ManageHealthPassAdapter.ViewHolder>() {

    fun interface DeleteClickListener {
        fun onDeleteClicked(vaccineRecordId: Long)
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
            layoutQrCode.visibility = View.GONE
            icReorder.visibility = View.VISIBLE
            imgUnlink.visibility = View.VISIBLE
            imgUnlink.setOnClickListener {
                deleteClickListener.onDeleteClicked(healthPass.vaccineRecordId)
            }

            txtFullName.text = healthPass.displayName()

            healthPass.status?.let {
                val passState = it.getHealthPassStatus(root.context)
                txtVaccineStatus.text = passState.status
                layoutVaccineStatus.setBackgroundColor(passState.color)
                txtVaccineStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        passState.icon, 0, 0, 0
                    )
            }
        }
    }

    override fun getItemCount(): Int {
        return healthPasses.size
    }
}

