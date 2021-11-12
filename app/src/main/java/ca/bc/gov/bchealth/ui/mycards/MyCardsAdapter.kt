package ca.bc.gov.bchealth.ui.mycards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemMycardsCardsListBinding
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.utils.getBarcode
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * [MyCardsAdapter]
 *
 * @author Pinakin Kansara
 */
class MyCardsAdapter(
    var cards: MutableList<HealthCardDto>,
    private var canManage: Boolean = false,
    var clickListener: ((HealthCardDto) -> Unit)? = null
) : RecyclerView.Adapter<MyCardsAdapter.ViewHolder>() {

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
        val card = cards[position]
        holder.binding.txtFullName.text = card.name
        holder.binding.txtIssueDate.text = holder.itemView.resources
            .getString(R.string.issued_on).plus(" ").plus(card.issueDate)
        setUiState(holder, card.status)

        if (canManage) {
            holder.binding.imgUnlink.visibility = View.VISIBLE
            holder.binding.imgUnlink.setOnClickListener {
                clickListener?.invoke(card)
            }
            holder.binding.icReorder.visibility = View.VISIBLE
        } else {
            if (card.isExpanded) {
                holder.binding.layoutQrCode.visibility = View.VISIBLE

                runBlocking {
                    try {
                        val bitmap = async {
                            card.uri.getBarcode()
                        }
                        holder.binding.imgQrCode.setImageBitmap(bitmap.await())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                holder.binding.layoutQrCode.visibility = View.GONE
            }

            holder.binding.imgQrCode.setOnClickListener {
                val action = MyCardsFragmentDirections
                    .actionMyCardsFragmentToExpandQRFragment(card.uri)
                holder.binding.imgQrCode.findNavController().navigate(action)
            }

            holder.itemView.setOnClickListener {
                cards.forEach {
                    it.isExpanded = false
                }
                cards[position].isExpanded = true
                notifyItemRangeChanged(0, itemCount)
            }

            holder.binding.imgUnlink.visibility = View.GONE
            holder.binding.icReorder.visibility = View.GONE

            if (card.federalPass.isNullOrEmpty()) {
                holder.binding.tvFederalPassTitle.text = holder.itemView.resources
                    .getString(R.string.get_federal_proof_of_vaccination)
                holder.binding.ivFederalPassAction
                    .setImageResource(R.drawable.ic_federal_pass_add)
                holder.binding.viewFederalProof.setOnClickListener {
                    val action = MyCardsFragmentDirections
                        .actionMyCardsFragmentToFetchTravelPassFragment(card)
                    holder.itemView.findNavController().navigate(action)
                }
            } else {
                holder.binding.tvFederalPassTitle.text = holder.itemView.resources
                    .getString(R.string.show_federal_proof_of_vaccination)
                holder.binding.ivFederalPassAction
                    .setImageResource(R.drawable.ic_federal_pass_forward_arrow)
                holder.binding.viewFederalProof.setOnClickListener {
                    clickListener?.invoke(card)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    private fun setUiState(holder: ViewHolder, status: ImmunizationStatus) {

        val partiallyVaccinatedColor = holder.itemView.context.getColor(R.color.status_blue)
        val fullyVaccinatedColor = holder.itemView.context.getColor(R.color.status_green)
        val inValidColor = holder.itemView.context.getColor(R.color.grey)

        var color = inValidColor
        var statusText = ""
        when (status) {

            ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                color = partiallyVaccinatedColor
                statusText = holder.itemView.context.resources
                    .getString(R.string.partially_vaccinated)
                holder.binding.txtVaccineStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
            }

            ImmunizationStatus.FULLY_IMMUNIZED -> {
                color = fullyVaccinatedColor
                statusText = holder.itemView.context.resources
                    .getString(R.string.vaccinated)
                holder.binding.txtVaccineStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_mark, 0, 0, 0
                    )
            }

            ImmunizationStatus.INVALID_QR_CODE -> {
                color = inValidColor
                statusText = holder.itemView.context.resources
                    .getString(R.string.no_record)
                holder.binding.txtVaccineStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
            }
        }

        holder.binding.txtVaccineStatus.text = statusText

        holder.binding.layoutVaccineStatus.setBackgroundColor(color)

        holder.binding.layoutQrCode.setBackgroundColor(color)
    }
}
