package ca.bc.gov.bchealth.ui.healthrecord.cancer

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.utils.URL_BC_CANCER_SCREENING
import ca.bc.gov.bchealth.utils.URL_BC_CERVIX_SCREENING
import ca.bc.gov.bchealth.utils.URL_BC_CLINICAL_OFFERS_SCREENING
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.services.BcCancerScreeningRepository
import ca.bc.gov.repository.services.PatientServicesRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2024-01-19 at 11:47 a.m.
 */
@HiltViewModel
class BcCancerScreeningDetailViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository,
    private val patientServicesRepository: PatientServicesRepository,
    private val bcCancerScreeningRepository: BcCancerScreeningRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(BcCancerScreeningDataDetailUiState())
    val uiState: StateFlow<BcCancerScreeningDataDetailUiState> = _uiState.asStateFlow()

    fun getBcCancerScreeningData(id: Long) = viewModelScope.launch {
        try {
            val data = bcCancerScreeningRepository.getBcCancerScreeningDataDetails(id)

            _uiState.update {
                it.copy(
                    onLoading = false,
                    toolbarTitle = if (data.eventType == "Result") { "BC Cancer screening result" } else { "BC Cancer screening" },
                    fileId = data.fileId,
                    links = if (data.eventType == "Result") {
                        listOf(ExternalLink(name = "BC Cancer", link = URL_BC_CANCER_SCREENING))
                    } else {
                        listOf(
                            ExternalLink(name = "What is Cervix screening", link = URL_BC_CERVIX_SCREENING),
                            ExternalLink(name = "Find clinics offers screening", link = URL_BC_CLINICAL_OFFERS_SCREENING)
                        )
                    },
                    description = if (data.eventType == "Result") { R.string.bc_cancer_screening_result_description } else { R.string.bc_cancer_screening_recall_desc },
                    id = data.id,
                    pdfButtonTitle = if (data.eventType == "Result") { "View PDF" } else { "View letter" }
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    onLoading = false
                )
            }
        }
    }

    fun onClickDownload() = viewModelScope.launch {
        _uiState.update { it.copy(onLoading = true) }

        try {
            mobileConfigRepository.refreshMobileConfiguration()
            val authParams = bcscAuthRepo.getAuthParametersDto()
            mobileConfigRepository.refreshMobileConfiguration()
            _uiState.value.fileId?.let { id ->
                val fileString =
                    patientServicesRepository.fetchPatientDataFile(authParams.hdid, id)

                _uiState.update { it.copy(pdfData = fileString, onLoading = false) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(onLoading = false) }
            handleBaseException(e)
        }
    }

    fun resetPdfState() {
        _uiState.update { it.copy(pdfData = null) }
    }
}

data class BcCancerScreeningDataDetailUiState(
    val onLoading: Boolean = false,
    val toolbarTitle: String = "BC Cancer screening",
    val links: List<ExternalLink> = emptyList(),
    @StringRes val description: Int = R.string.bc_cancer_screening_result_description,
    val fileId: String? = null,
    val pdfData: String? = null,
    val id: String? = null,
    val pdfButtonTitle: String = ""
)

data class ExternalLink(
    val name: String,
    val link: String
)
