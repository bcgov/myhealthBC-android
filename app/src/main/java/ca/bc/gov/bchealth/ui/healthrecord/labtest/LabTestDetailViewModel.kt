package ca.bc.gov.bchealth.ui.healthrecord.labtest

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestsAndPatientDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabTestDetailViewModel @Inject constructor(
    private val labOrderRepository: LabOrderRepository,
    private val mobileConfigRepository: MobileConfigRepository
) : ViewModel() {

    private var labPdfId: String? = null

    private val _uiState = MutableStateFlow(LabTestDetailUiState())
    val uiState: StateFlow<LabTestDetailUiState> = _uiState.asStateFlow()

    fun getLabTestDetails(labOrderId: Long) = viewModelScope.launch {
        try {
            _uiState.update { it.copy(onLoading = true) }
            val labOrderWithLabTestsAndPatientDto = labOrderRepository.findByLabOrderId(labOrderId)

            updateLabPdfId(labOrderWithLabTestsAndPatientDto)

            _uiState.update {
                it.copy(
                    onLoading = false,
                    labTestDetails = prepareLabTestDetailsData(labOrderWithLabTestsAndPatientDto.labOrderWithLabTest),
                    toolbarTitle = labOrderWithLabTestsAndPatientDto.labOrderWithLabTest.labOrder.commonName,
                    showDownloadOption = labOrderWithLabTestsAndPatientDto.labOrderWithLabTest.labOrder.reportingAvailable,
                    reportId = labOrderWithLabTestsAndPatientDto.labOrderWithLabTest.labOrder.reportId
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(onError = true) }
        }
    }

    private fun updateLabPdfId(dto: LabOrderWithLabTestsAndPatientDto) {
        val id = dto.labOrderWithLabTest.labOrder.labPdfId
        if (!id.isNullOrBlank()) {
            labPdfId = id
        }
    }

    private fun prepareLabTestDetailsData(labOrderWithLabTestDto: LabOrderWithLabTestDto): List<LabTestDetail> {
        val labTestDetails = mutableListOf<LabTestDetail>()
        if (labOrderWithLabTestDto.labOrder.reportingAvailable) {
            labTestDetails.add(
                LabTestDetail(
                    viewType = ITEM_VIEW_PDF
                )
            )
        }
        labTestDetails.add(
            LabTestDetail(
                title1 = R.string.collection_date,
                collectionDateTime = labOrderWithLabTestDto.labOrder.collectionDateTime?.toDate(),
                timelineDateTime = labOrderWithLabTestDto.labOrder.timelineDateTime.toDateTimeString(),
                title2 = R.string.ordering_provider,
                orderingProvider = labOrderWithLabTestDto.labOrder.orderingProvider,
                title3 = R.string.reporting_lab,
                reportingSource = labOrderWithLabTestDto.labOrder.reportingSource
            )
        )

        var testStatus = 0
        labOrderWithLabTestDto.labTests.forEachIndexed { index, labTest ->
            var result: Boolean? = null
            when {
                labTest.testStatus.equals("Active", true) -> {
                    testStatus = R.string.pending
                }
                labTest.testStatus.equals("cancelled", true) -> {
                    testStatus = R.string.cancelled
                }
                labTest.testStatus.equals("corrected", true) -> {
                    testStatus = R.string.corrected
                    result = labTest.outOfRange
                }
                else -> {
                    testStatus = R.string.completed
                    result = labTest.outOfRange
                }
            }

            labTestDetails.add(
                LabTestDetail(
                    id = labTest.id,
                    header = if (index == 0) R.string.test_summary else null,
                    summary = if (index == 0) R.string.summary_desc else null,
                    title1 = R.string.test_name,
                    testName = labTest.batteryType,
                    title2 = R.string.result,
                    isOutOfRange = result,
                    title3 = R.string.lab_test_status,
                    testStatus = testStatus,
                    viewType = ITEM_VIEW_TYPE_LAB_TEST
                )
            )
        }

        handleBanner(labOrderWithLabTestDto, testStatus, labTestDetails)

        return labTestDetails
    }

    private fun handleBanner(
        dto: LabOrderWithLabTestDto,
        testStatus: Int,
        labTestDetails: MutableList<LabTestDetail>
    ) {
        when {
            dto.labTests.isEmpty() && testStatus == 0 -> {
                labTestDetails.add(
                    0,
                    LabTestDetail(
                        bannerHeader = R.string.lab_test_banner_pending_title,
                        bannerText = R.string.lab_test_banner_pending_message_1,
                        bannerClickableText = R.string.lab_test_banner_pending_clickable_text,
                        viewType = ITEM_VIEW_TYPE_LAB_TEST_BANNER
                    )
                )
            }

            testStatus == R.string.pending -> {
                labTestDetails.add(
                    0,
                    LabTestDetail(
                        bannerHeader = R.string.lab_test_banner_pending_title,
                        bannerText = R.string.lab_test_banner_pending_message_2,
                        viewType = ITEM_VIEW_TYPE_LAB_TEST_BANNER
                    )
                )
            }

            testStatus == R.string.cancelled -> {
                labTestDetails.add(
                    0,
                    LabTestDetail(
                        bannerHeader = R.string.lab_test_banner_cancelled_title,
                        bannerText = R.string.lab_test_banner_cancelled_message,
                        bannerClickableText = R.string.lab_test_banner_cancelled_message_clickable_text,
                        viewType = ITEM_VIEW_TYPE_LAB_TEST_BANNER
                    )
                )
            }
        }
    }

    fun getLabTestPdf() = viewModelScope.launch {
        _uiState.update { it.copy(onLoading = true) }
        try {
            val isHgServicesUp = mobileConfigRepository.refreshMobileConfiguration()
            if (isHgServicesUp) {
                labPdfId?.apply {
                    val pdfData = labOrderRepository.fetchLabTestPdf(this, false)
                    _uiState.update { it.copy(pdfData = pdfData) }
                }
            } else {
                _uiState.update {
                    it.copy(
                        onLoading = false,
                        isHgServicesUp = false
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _uiState.update {
                        it.copy(
                            onLoading = false,
                            isConnected = false
                        )
                    }
                }
                else -> {
                    _uiState.update {
                        it.copy(
                            onLoading = false,
                            onError = true
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val ITEM_VIEW_TYPE_LAB_ORDER = 0
        const val ITEM_VIEW_TYPE_LAB_TEST = 1
        const val ITEM_VIEW_TYPE_LAB_TEST_BANNER = 2
        const val ITEM_VIEW_PDF = 3
    }

    fun resetUiState() {
        _uiState.update {
            it.copy(
                onLoading = false,
                onError = false,
                labTestDetails = null,
                toolbarTitle = null,
                pdfData = null,
                isConnected = true,
                isHgServicesUp = true
            )
        }
    }
}

data class LabTestDetailUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val labTestDetails: List<LabTestDetail>? = null,
    val toolbarTitle: String? = null,
    val showDownloadOption: Boolean = false,
    val pdfData: String? = null,
    val isHgServicesUp: Boolean = true,
    val isConnected: Boolean = true,
    val reportId: String? = null
)

data class LabTestDetail(
    var id: Long = -1L,
    @StringRes var header: Int? = null,
    @StringRes var summary: Int? = null,
    @StringRes val title1: Int? = null,
    @StringRes val title2: Int? = null,
    @StringRes val title3: Int? = null,
    val collectionDateTime: String? = "N/A",
    val timelineDateTime: String? = null,
    val orderingProvider: String? = "N/A",
    val reportingSource: String? = "N/A",
    val testName: String? = "N/A",
    val isOutOfRange: Boolean? = null,
    val testStatus: Int? = null,
    val bannerHeader: Int? = null,
    val bannerText: Int? = null,
    val bannerClickableText: Int? = null,
    val viewType: Int = LabTestDetailViewModel.ITEM_VIEW_TYPE_LAB_ORDER
)
