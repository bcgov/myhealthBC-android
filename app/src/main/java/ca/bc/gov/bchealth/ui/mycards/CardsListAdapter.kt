package ca.bc.gov.bchealth.ui.mycards

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemMycardsCardsListBinding
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus

/**
 * [CardsListAdapter]
 *
 * @author amit metri
 */
class CardsListAdapter(
    var cards: MutableList<HealthCardDto>,
    private val unLinkListener: ((HealthCardDto) -> Unit?)?
) : RecyclerView.Adapter<CardsListAdapter.ViewHolder>() {

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
        holder.binding.txtIssueDate.text = card.issueDate
        setUiState(holder, card.status)

        unLinkListener?.let {
            holder.binding.imgUnlink.setOnClickListener {
                it(card)
            }
        }

        if (unLinkListener == null) {
            if (card.isExpanded) {
                holder.binding.layoutQrCode.visibility = View.VISIBLE
                try {
                    holder.binding.imgQrCode.setImageBitmap(getBarcode(card.uri))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                holder.binding.layoutQrCode.visibility = View.GONE
            }

            holder.binding.layoutQrCode.setOnClickListener {
                val action = MyCardsFragmentDirections
                    .actionMyCardsFragmentToExpandQRFragment(card.uri)
                holder.binding.layoutQrCode.findNavController().navigate(action)
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
        } else {
            holder.binding.imgUnlink.visibility = View.VISIBLE
            holder.binding.icReorder.visibility = View.VISIBLE
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

    private fun getBarcode(data: String): Bitmap {
        val qrcode = QRGEncoder(data, null, QRGContents.Type.TEXT, 1200)
        return qrcode.encodeAsBitmap()
    }
}
