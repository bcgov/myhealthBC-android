package ca.bc.gov.bchealth.ui.travelpass

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemTravelPassPageBinding

/**
 * @author Pinakin Kansara
 */
class TravelPassAdapter(private val pdfRenderer: PdfRenderer) :
    RecyclerView.Adapter<TravelPassAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTravelPassPageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTravelPassPageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val page: PdfRenderer.Page = pdfRenderer.openPage(position)

        val displayMetrics = holder.itemView.resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels

        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

        val scaledBitMap = resize(bitmap, displayWidth, displayHeight)

        page.render(scaledBitMap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        holder.binding.ivPdfPage.setImageBitmap(scaledBitMap)

        page.close()
    }

    override fun getItemCount(): Int {
        return pdfRenderer.pageCount
    }

    private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
        } else {
            image
        }
    }
}
