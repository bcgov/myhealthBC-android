package ca.bc.gov.bchealth.ui.travelpass

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTravelPassBinding
import ca.bc.gov.bchealth.utils.toast
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class TravelPassFragment : Fragment(R.layout.fragment_travel_pass) {
    private val binding by viewBindings(FragmentTravelPassBinding::bind)
    private lateinit var travelPassAdapter: TravelPassAdapter
    private lateinit var pdfRenderer: PdfRenderer
    private val args: TravelPassFragmentArgs by navArgs()
    private lateinit var fileForSharing: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setToolBar()

        try {
            val byteArray = Base64.decode(args.travelPass, Base64.DEFAULT)
            fileForSharing = File.createTempFile("travelPass", ".pdf", requireContext().filesDir)
            requireContext().openFileOutput(fileForSharing.name, Context.MODE_PRIVATE).use {
                it.write(byteArray)
            }
            val parcelFileDescriptor =
                ParcelFileDescriptor.open(
                    fileForSharing,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            setRecyclerView(pdfRenderer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRecyclerView(pdfRenderer: PdfRenderer) {

        travelPassAdapter = TravelPassAdapter(pdfRenderer)

        binding.rvPdfPages.adapter = travelPassAdapter

        binding.rvPdfPages.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            tvLeftOption.visibility = View.VISIBLE
            tvLeftOption.setOnClickListener { findNavController().popBackStack() }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.travel_pass)

            ivRightOption.visibility = View.VISIBLE
            ivRightOption.setImageResource(R.drawable.ic_travel_pass_share)
            ivRightOption.setOnClickListener {

                if (fileForSharing.exists()) {
                    try {
                        val authority =
                            requireActivity().applicationContext.packageName.toString() +
                                ".fileprovider"
                        val uriToFile: Uri =
                            FileProvider.getUriForFile(requireActivity(), authority, fileForSharing)

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/pdf"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToFile)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Travel Pass"
                        )
                        shareIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Travel Pass"
                        )
                        requireActivity().startActivity(shareIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        requireContext().toast(requireContext().getString(R.string.no_app_found))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer.close()
    }
}