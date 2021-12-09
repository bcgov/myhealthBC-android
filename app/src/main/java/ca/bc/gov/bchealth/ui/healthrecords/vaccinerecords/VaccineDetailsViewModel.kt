package ca.bc.gov.bchealth.ui.healthrecords.vaccinerecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.LocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
* @author amit_metri on 09,December,2021
*/
@HiltViewModel
class VaccineDetailsViewModel @Inject constructor(
    private val dataSource: LocalDataSource
) : ViewModel() {

    fun deleteVaccineRecord(healthPassId: Int) = viewModelScope.launch {
        dataSource.deleteVaccineData(healthPassId)
        delay(1000)
    }
}
