package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*
* Created by amit_metri on 10,February,2022
*/
@HiltViewModel
class TimelineSharedViewModel @Inject constructor() : ViewModel() {

    var isImmunisationChecked: Boolean = false
    var isMedicationChecked: Boolean = false
    var isCovid19Checked: Boolean = false
    var is2022Checked: Boolean = false
    var is2021Checked: Boolean = false
    var is2020Checked: Boolean = false
    var isBefore2020Checked: Boolean = false
}
