package ca.bc.gov.bchealth.ui.pdf

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentPdfRendererBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class PdfRendererFragment : BaseFragment(R.layout.fragment_pdf_renderer) {
    private val binding by viewBindings(FragmentPdfRendererBinding::bind)
    private lateinit var pdfRendererAdapter: PdfRendererAdapter
    private lateinit var pdfRenderer: PdfRenderer
    private val args: PdfRendererFragmentArgs by navArgs()
    private var fileForSharing: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            val byteArray = Base64.decode(args.base64pdf, Base64.DEFAULT)
            fileForSharing = File.createTempFile("file", ".pdf", requireContext().filesDir)
            if (fileForSharing != null) {
                requireContext().openFileOutput(fileForSharing?.name, Context.MODE_PRIVATE).use {
                    it.write(byteArray)
                }
                val parcelFileDescriptor =
                    ParcelFileDescriptor.open(
                        fileForSharing,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                pdfRenderer = PdfRenderer(parcelFileDescriptor)

                setRecyclerView(pdfRenderer)
            }
        } catch (e: Exception) {
            // no implementation required
        }
    }

    private fun setRecyclerView(pdfRenderer: PdfRenderer) {
        pdfRendererAdapter = PdfRendererAdapter(pdfRenderer)
        binding.rvPdfPages.adapter = pdfRendererAdapter
        binding.rvPdfPages.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = args.title
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fileForSharing?.delete()
        pdfRenderer.close()
    }
}
