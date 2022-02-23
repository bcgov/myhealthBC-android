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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.barcodeanalyzer.BarcodeAnalyzer
import ca.bc.gov.bchealth.barcodeanalyzer.ScanningResultListener
import ca.bc.gov.bchealth.databinding.FragmentBarcodeScannerBinding
import ca.bc.gov.bchealth.ui.healthpass.add.AddOrUpdateCardViewModel
import ca.bc.gov.bchealth.ui.healthpass.add.Status
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.repository.model.PatientVaccineRecord
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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

    private val viewModel: AddOrUpdateCardViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUpCamera()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                initCamera()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    if (state.state == Status.CAN_INSERT) {
                        viewModel.insert(state.vaccineRecord!!)
                        viewModel.resetStatus()
                    }

                    if (state.state == Status.CAN_UPDATE) {
                        showCardReplacementDialog(state.vaccineRecord!!)
                        viewModel.resetStatus()
                    }

                    if (state.state == Status.DUPLICATE) {
                        showDuplicateRecordDialog()
                        viewModel.resetStatus()
                    }

                    if (state.state == Status.UPDATED || state.state == Status.INSERTED) {
                        sharedViewModel.setModifiedRecordId(state.modifiedRecordId)
                        viewModel.resetStatus()
                        navigateToHealthPass()
                    }

                    if (state.state == Status.ERROR) {
                        showError(
                            getString(R.string.error_invalid_qr_code_title),
                            getString(R.string.error_invalid_qr_code_message)
                        )
                        viewModel.resetStatus()
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
            AlertDialogHelper.showAlertDialog(
                context = requireContext(),
                title = getString(R.string.bc_no_rear_camera_title),
                msg = getString(R.string.bc_nor_rear_camera_message),
                positiveBtnMsg = getString(R.string.exit),
                positiveBtnCallback = {
                    if (!findNavController().popBackStack() || !findNavController().navigateUp()) {
                        requireActivity().finish()
                    }
                }
            )
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

            viewModel.processQRCode(shcUri)
        }

        override fun onFailure() {

            // Since camera is constantly analysing
            // Its good to clear analyzer to avoid duplicate dialogs
            // When barcode is not supported
            imageAnalysis.clearAnalyzer()

            showError(
                getString(R.string.error_invalid_qr_code_title),
                getString(R.string.error_invalid_qr_code_message)
            )
        }

        private fun showError(title: String, message: String) {
            AlertDialogHelper.showAlertDialog(
                context = requireContext(),
                title = title,
                msg = message,
                positiveBtnMsg = getString(R.string.scan_next),
                positiveBtnCallback = {
                    // Attach analyzer again to start analysis.
                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer(this))
                }
            )
        }

        private fun showDuplicateRecordDialog() {
            AlertDialogHelper.showAlertDialog(
                context = requireContext(),
                title = getString(R.string.error_duplicate_title),
                msg = getString(R.string.error_duplicate_message),
                positiveBtnMsg = getString(R.string.btn_ok),
                positiveBtnCallback = {
                    // Attach analyzer again to start analysis.
                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer(this))
                }
            )
        }

        private fun showCardReplacementDialog(vaccineRecord: PatientVaccineRecord) {
            AlertDialogHelper.showAlertDialog(
                context = requireContext(),
                title = getString(R.string.replace_health_pass_title),
                msg = getString(R.string.replace_health_pass_message),
                positiveBtnMsg = getString(R.string.replace),
                negativeBtnMsg = getString(R.string.not_now),
                positiveBtnCallback = {
                    viewModel.update(vaccineRecord)
                },
                negativeBtnCallback = {
                    // Attach analyzer again to start analysis.
                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer(this))
                }
            )
        }

        private fun navigateToHealthPass() {
            // Snowplow event
            analyticsFeatureViewModel.track(AnalyticsAction.ADD_QR, AnalyticsActionData.SCAN)
            findNavController().navigate(R.id.action_barcodeScannerFragment_to_healthPassFragment)
        }
    }
