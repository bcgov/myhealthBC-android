package ca.bc.gov.bchealth.ui.scanner

import android.content.Context
import android.os.Bundle
import android.util.Size
import android.view.View
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.barcodeanalyzer.BarcodeAnalyzer
import ca.bc.gov.bchealth.barcodeanalyzer.ScanningResultListener
import ca.bc.gov.bchealth.data.local.entity.CardType
import ca.bc.gov.bchealth.databinding.FragmentBarcodeScannerBinding
import ca.bc.gov.bchealth.ui.mycards.MyCardsViewModel
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.launch

/**
 * [BarcodeScannerFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class BarcodeScannerFragment : Fragment(R.layout.fragment_barcode_scanner), ScanningResultListener {

    private val binding by viewBindings(FragmentBarcodeScannerBinding::bind)

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var imageAnalysis: ImageAnalysis

    private lateinit var camera: Camera

    private val myCardsViewModel: MyCardsViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUpCamera()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    initCamera()
                }
            }
        }
    }

    private fun initCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.overlay.post {
            binding.overlay.setViewFinder()
        }
    }

    override fun onDestroyView() {

        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }

        super.onDestroyView()
    }

    private fun setUpCamera() {

        val cameraProviderFeature = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFeature.addListener({

            cameraProvider = cameraProviderFeature.get()

            bindBarcodeScannerUseCase()

            enableFlashControl()
        }, ContextCompat.getMainExecutor(requireContext()))
        }

        private fun bindBarcodeScannerUseCase() {

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            val hasCamera = cameraProvider.hasCamera(cameraSelector)

            if (hasCamera) {

                val resolution = Size(
                    binding.scannerPreview.width,
                    binding.scannerPreview.height
                )
                val preview = Preview.Builder()
                    .apply {
                        setTargetResolution(resolution)
                    }.build()

                imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(resolution)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer(this))

                cameraProvider.unbindAll()

                camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageAnalysis
                )

                preview.setSurfaceProvider(binding.scannerPreview.surfaceProvider)
            } else {
                showNoCameraAlertDialog()
            }
        }

        private fun showNoCameraAlertDialog() {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.bc_no_rear_camera_title))
                .setCancelable(false)
                .setMessage(getString(R.string.bc_nor_rear_camera_message))
                .setNegativeButton(getString(R.string.exit)) { dialog, which ->
                    if (!findNavController().popBackStack() || !findNavController().navigateUp()) {
                        requireActivity().finish()
                    }
                    dialog.dismiss()
                }
                .show()
        }

        private fun enableFlashControl() {
            if (camera.cameraInfo.hasFlashUnit()) {
                binding.checkboxFlashLight.visibility = View.VISIBLE

                binding.checkboxFlashLight.setOnCheckedChangeListener { buttonView, isChecked ->

                    if (buttonView.isPressed) {
                        camera.cameraControl.enableTorch(isChecked)
                    }
                }

                camera.cameraInfo.torchState.observe(viewLifecycleOwner) {
                    it?.let { torchState ->
                        binding.checkboxFlashLight.isChecked = torchState == TorchState.ON
                    }
                }
            }
        }

        override fun onScanned(shcUri: String) {

            // Since camera is constantly analysing
            // Its good to clear analyzer to avoid duplicate dialogs
            // When barcode is not supported
            imageAnalysis.clearAnalyzer()

            myCardsViewModel.saveCard(shcUri, CardType.QR)

            findNavController().popBackStack(R.id.myCardsFragment, false)
        }

        override fun onFailure() {

            // Since camera is constantly analysing
            // Its good to clear analyzer to avoid duplicate dialogs
            // When barcode is not supported
            imageAnalysis.clearAnalyzer()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.bc_invalid_barcode_title))
                .setCancelable(false)
                .setMessage(getString(R.string.bc_invalid_barcode_message))
                .setPositiveButton(getString(R.string.scan_next)) { dialog, which ->

                    // Attach analyzer again to start analysis.
                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer(this))

                    dialog.dismiss()
                }
                .show()
        }
    }
