package ca.bc.gov.bchealth.ui.mycards

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

/**
 * [MyCardsAdapter]
 *
 * @author Pinakin Kansara
 */
class MyCardsAdapter(
    var cards: MutableList<HealthCardDto>,
    var canManage: Boolean = false,
    val unLinkListener: (HealthCardDto) -> Unit
) : RecyclerView.Adapter<MyCardsAdapter.ViewHolder>() {

    var previouslyExpandedPosition: Int = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFullName: MaterialTextView = itemView.findViewById(R.id.txt_full_name)
        val txtStatus: MaterialTextView = itemView.findViewById(R.id.txt_vaccine_status)
        val layoutQrCode: LinearLayoutCompat = itemView.findViewById(R.id.layout_qr_code)
        val layoutVaccineStatus: LinearLayoutCompat =
            itemView.findViewById(R.id.layout_vaccine_status)
        val imgQrCode: ShapeableImageView = itemView.findViewById(R.id.img_qr_code)
        val imgUnLink: ShapeableImageView = itemView.findViewById(R.id.img_unlink)
        val imgReorder: ShapeableImageView = itemView.findViewById(R.id.ic_reorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_card_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val card = cards[position]

        holder.txtFullName.text = card.name
        cards[previouslyExpandedPosition].isExpanded = true
        setUiState(holder, card.status)

        if (card.isExpanded) {
            holder.layoutQrCode.visibility = View.VISIBLE
            try {
                holder.imgQrCode.setImageBitmap(getBarcode(card.uri))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.layoutQrCode.visibility = View.GONE
        }

        holder.layoutQrCode.setOnClickListener {
            val action = MyCardsFragmentDirections
                .actionMyCardsFragmentToExpandQRFragment(card.uri)
            holder.layoutQrCode.findNavController().navigate(action)
        }

        holder.itemView.setOnClickListener {
            cards[previouslyExpandedPosition].isExpanded = false
            notifyItemChanged(previouslyExpandedPosition)

            previouslyExpandedPosition = position
            cards[position].isExpanded = true
            notifyItemChanged(position)
        }

        holder.imgUnLink.setOnClickListener {
            unLinkListener(card)
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
                holder.txtStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
            }

            ImmunizationStatus.FULLY_IMMUNIZED -> {
                color = fullyVaccinatedColor
                statusText = holder.itemView.context.resources
                    .getString(R.string.vaccinated)
                holder.txtStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_mark, 0, 0, 0
                    )
            }

            ImmunizationStatus.INVALID_QR_CODE -> {
                color = inValidColor
                statusText = holder.itemView.context.resources
                    .getString(R.string.no_record)
                holder.txtStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
            }
        }

        holder.txtStatus.text = statusText

        holder.layoutVaccineStatus.setBackgroundColor(color)

        holder.layoutQrCode.setBackgroundColor(color)

        if (canManage) {
            holder.imgReorder.visibility = View.VISIBLE
            holder.imgUnLink.visibility = View.VISIBLE
            cards.forEach { healthCard ->
                healthCard.isExpanded = false
            }
        } else {
            holder.imgReorder.visibility = View.GONE
            holder.imgUnLink.visibility = View.GONE
        }
    }

    private fun getBarcode(data: String): Bitmap {
        val qrcode = QRGEncoder(data, null, QRGContents.Type.TEXT, 1200)
        return qrcode.encodeAsBitmap()
    }

    fun moveItem(from: Int, to: Int) {
        if (from < to) {
            for (i in from until to - 1) {
                cards[i] = cards.set(i + 1, cards[i])
            }
        } else {
            for (i in from..to + 1) {
                cards[i] = cards.set(i - 1, cards[i])
            }
        }

        notifyItemMoved(from, to)
    }
}
