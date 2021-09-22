package ca.bc.gov.bchealth.ui.mycards

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.HealthCardDto
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
            LayoutInflater.from(parent.context).inflate(R.layout.my_card_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val card = cards[position]

        holder.txtFullName.text = card.name
        holder.txtStatus.text = card.status.name

        try {
            holder.imgQrCode.setImageBitmap(getBarcode(card.uri))
        } catch (e: Exception) {
        }

        holder.layoutVaccineStatus.setOnClickListener {
            if (expandedPosition == position) {
                holder.layoutQrCode.visibility = View.GONE
                expandedPosition = -1
            } else {
                expandedPosition = position
                holder.layoutQrCode.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    private fun getBarcode(data: String): Bitmap {
        val qrcode = QRGEncoder(data, null, QRGContents.Type.TEXT, 400)
        return qrcode.encodeAsBitmap()
    }
}
