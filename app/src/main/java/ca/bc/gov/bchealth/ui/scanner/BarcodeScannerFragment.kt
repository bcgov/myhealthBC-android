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
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.AnalyticsText
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.barcodeanalyzer.BarcodeAnalyzer
import ca.bc.gov.bchealth.barcodeanalyzer.ScanningResultListener
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.databinding.FragmentBarcodeScannerBinding
import ca.bc.gov.bchealth.ui.mycards.MyCardsViewModel
import ca.bc.gov.bchealth.utils.ErrorData
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
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

    @Inject
    lateinit var shcDecoder: SHCDecoder

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

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCardsViewModel.responseFlow.collect {
                    when (it) {
                        is Response.Success -> {

                            if (it.data == null) {
                                navigateToCardsList()
                            } else {
                                showCardReplacementDialog(it.data as HealthCard)
                            }
                        }
                        is Response.Error -> {
                            showError(
                                it.errorData?.errorTitle.toString(),
                                it.errorData?.errorMessage.toString()
                            )
                        }
                        is Response.Loading -> {
                        }
                    }
                }
            }
        }
    }

    private fun initCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.overlay.postDelayed({ binding.overlay.setViewFinder() }, 500)
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

            binding.ivClose.setOnClickListener {
                findNavController().popBackStack()
            }
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

            try {
                shcDecoder.getImmunizationStatus(shcUri)
                myCardsViewModel.saveCard(shcUri)
            } catch (e: Exception) {
                showError(
                    ErrorData.INVALID_QR.errorTitle.toString(),
                    ErrorData.INVALID_QR.errorMessage.toString()
                )
            }
        }

        override fun onFailure() {

            // Since camera is constantly analysing
            // Its good to clear analyzer to avoid duplicate dialogs
            // When barcode is not supported
            imageAnalysis.clearAnalyzer()

            showError(
                ErrorData.INVALID_QR.errorTitle.toString(),
                ErrorData.INVALID_QR.errorMessage.toString()
            )
        }

        private fun showError(title: String, message: String) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(getString(R.string.scan_next)) { dialog, which ->

                    // Attach analyzer again to start analysis.
                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer(this))

                    dialog.dismiss()
                }
                .show()
        }

        private fun showCardReplacementDialog(healthCard: HealthCard) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.replace_health_pass_title))
                .setCancelable(false)
                .setMessage(getString(R.string.replace_health_pass_message))
                .setPositiveButton(getString(R.string.replace)) { dialog, which ->

                    myCardsViewModel.replaceExitingHealthPass(healthCard).invokeOnCompletion {
                        dialog.dismiss()
                        navigateToCardsList()
                    }
                }.setNegativeButton(getString(R.string.not_now)) { dialog, which ->

                    // Attach analyzer again to start analysis.
                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer(this))

                    dialog.dismiss()
                }
                .show()
        }

        private fun navigateToCardsList() {
            // Snowplow event
            Snowplow.getDefaultTracker()?.track(
                SelfDescribingEvent
                    .get(AnalyticsAction.AddQR.value, AnalyticsText.Scan.value)
            )

            findNavController().popBackStack(R.id.myCardsFragment, false)
        }
    }
