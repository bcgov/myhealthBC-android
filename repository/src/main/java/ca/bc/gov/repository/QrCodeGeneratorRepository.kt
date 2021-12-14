package ca.bc.gov.repository

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import ca.bc.gov.repository.qr.qrgen.QrCode
import ca.bc.gov.repository.qr.qrgen.QrSegment

/**
 * @author Pinakin Kansara
 */
class QrCodeGeneratorRepository {

    suspend fun generateQRCode(shcUri: String): Bitmap? {
        try {
            val segments: MutableList<QrSegment> = QrSegment.makeSegments(shcUri)
            val qrCode: QrCode = QrCode.encodeSegments(
                segments,
                QrCode.Ecc.LOW,
                5,
                40,
                2,
                false
            )

            val size = qrCode.size

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

            for (y in 0 until size) {
                for (x in 0 until size) {
                    bitmap.setPixel(
                        x, y,
                        if (qrCode.getModule(x, y))
                            Color.BLACK
                        else
                            Color.WHITE
                    )
                }
            }

            val scaledBitMap = Bitmap.createScaledBitmap(bitmap, 400, 400, false)

            return addWhiteBorder(scaledBitMap, 10)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun addWhiteBorder(bmp: Bitmap, borderSize: Int): Bitmap? {
        val bmpWithBorder = Bitmap
            .createBitmap(
                bmp.width + borderSize * 2,
                bmp.height + borderSize * 2,
                bmp.config
            )
        val canvas = Canvas(bmpWithBorder)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bmp, borderSize.toFloat(), borderSize.toFloat(), null)
        return bmpWithBorder
    }
}