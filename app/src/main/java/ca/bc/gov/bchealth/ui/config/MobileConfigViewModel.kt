package ca.bc.gov.bchealth.ui.config

import androidx.lifecycle.ViewModel
import ca.bc.gov.common.model.config.DataSetFeatureFlag
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2023-12-05 at 11:47 a.m.
 */
@HiltViewModel
class MobileConfigViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository
) : ViewModel() {

    fun patientFeatureFlag(): DataSetFeatureFlag = runBlocking {
        mobileConfigRepository.getPatientDataSetFeatureFlags()
    }
}
