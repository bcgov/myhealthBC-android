package ca.bc.gov.bchealth.ui.healthrecord.filter

import androidx.fragment.app.activityViewModels
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.filter.FilterFragment
import ca.bc.gov.common.BuildConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientFilterFragment : FilterFragment() {

    override val filterSharedViewModel: PatientFilterViewModel by activityViewModels()

    override val availableFilters = mutableListOf(
        R.id.chip_date,
        R.id.chip_medication,
        R.id.chip_lab_test,
        R.id.chip_covid_test,
        R.id.chip_immunizations,
        R.id.chip_health_visit,
        R.id.chip_special_authority,
    ).apply {
        if (BuildConfig.FLAG_HOSPITAL_VISITS) {
            this.add(R.id.chip_hospital_visits)
        }
    }
}
