package ca.bc.gov.bchealth.ui.healthpass.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthPassCardBinding
import ca.bc.gov.bchealth.ui.healthpass.HealthPass

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
        val healthPass = healthPasses[position]

        holder.binding.apply {
            layoutQrCode.visibility = View.GONE
            icReorder.visibility = View.VISIBLE
            imgUnlink.visibility = View.VISIBLE
            imgUnlink.setOnClickListener {
                deleteClickListener.onDeleteClicked(healthPass.vaccineRecordId)
            }
            txtFullName.text = healthPass.name
            txtVaccineStatus.setText(healthPass.state.status)
            layoutVaccineStatus.setBackgroundColor(holder.itemView.context.getColor(healthPass.state.color))
            txtVaccineStatus
                .setCompoundDrawablesWithIntrinsicBounds(
                    healthPass.state.icon, 0, 0, 0
                )
        }
    }

    override fun getItemCount(): Int {
        return healthPasses.size
    }
}
