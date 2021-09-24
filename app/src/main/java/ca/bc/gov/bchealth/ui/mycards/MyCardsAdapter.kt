package ca.bc.gov.bchealth.ui.mycards

import android.annotation.SuppressLint
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
    var cards: List<HealthCardDto>
) : RecyclerView.Adapter<MyCardsAdapter.ViewHolder>() {

    private var expandedPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFullName: MaterialTextView = itemView.findViewById(R.id.txt_full_name)
        val txtStatus: MaterialTextView = itemView.findViewById(R.id.txt_vaccine_status)
        val layoutQrCode: LinearLayoutCompat = itemView.findViewById(R.id.layout_qr_code)
        val layoutVaccineStatus: LinearLayoutCompat =
            itemView.findViewById(R.id.layout_vaccine_status)
        val imgQrCode: ShapeableImageView = itemView.findViewById(R.id.img_qr_code)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_card_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val card = cards[position]

        /*
        * Default view settings
        * */
        holder.txtFullName.text = card.name
        when (card.status) {
            ImmunizationStatus.FULLY_IMMUNIZED -> {
                holder.layoutVaccineStatus
                    .setBackgroundColor(
                        holder.layoutVaccineStatus.context.getColor(R.color.status_green)
                    )
                holder.txtStatus.text =
                    holder.layoutVaccineStatus.context.resources
                        .getString(R.string.vaccinated)
                holder.txtStatus
                    .setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_mark, 0, 0, 0
                    )
            }

            ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                holder.layoutVaccineStatus
                    .setBackgroundColor(
                        holder.layoutVaccineStatus.context.getColor(R.color.status_blue)
                    )
                holder.txtStatus.text =
                    holder.layoutVaccineStatus.context.resources
                        .getString(R.string.partially_vaccinated)
            }

            else -> {
                holder.layoutVaccineStatus
                    .setBackgroundColor(holder.layoutVaccineStatus.context.getColor(R.color.grey))
                holder.txtStatus.text =
                    holder.layoutVaccineStatus.context.resources
                        .getString(R.string.no_record)
            }
        }

        /*
        * Expand/Collapse logic
        * */
        if (card.isExpanded) {

            holder.layoutQrCode.visibility = View.VISIBLE
            try {
                holder.imgQrCode.setImageBitmap(getBarcode(card.uri))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            when (card.status) {
                ImmunizationStatus.FULLY_IMMUNIZED -> {
                    holder.layoutQrCode
                        .setBackgroundColor(
                            holder.layoutQrCode.context.getColor(R.color.status_green)
                        )
                }
                ImmunizationStatus.PARTIALLY_IMMUNIZED ->
                    holder.layoutQrCode
                        .setBackgroundColor(
                            holder.layoutQrCode.context.getColor(R.color.status_blue)
                        )
                else ->
                    holder.layoutQrCode
                        .setBackgroundColor(
                            holder.layoutQrCode.context.getColor(R.color.grey)
                        )
            }

            holder.layoutQrCode.setOnClickListener {
                val action = MyCardsFragmentDirections
                    .actionMyCardsFragmentToExpandQRFragment(card.uri)
                holder.layoutQrCode.findNavController().navigate(action)
            }
        } else {
            holder.layoutQrCode.visibility = View.GONE
        }

        holder.layoutVaccineStatus.setOnClickListener {
            if (expandedPosition != -1) {
                cards[expandedPosition].isExpanded = false
                notifyItemChanged(expandedPosition)
            }

            card.isExpanded = true
            notifyItemChanged(position)
            expandedPosition = position
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    private fun getBarcode(data: String): Bitmap {
        val qrcode = QRGEncoder(data, null, QRGContents.Type.TEXT, 1200)
        return qrcode.encodeAsBitmap()
    }
}
