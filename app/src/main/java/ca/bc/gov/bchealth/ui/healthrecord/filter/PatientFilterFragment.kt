package ca.bc.gov.bchealth.ui.healthrecord.filter

import androidx.fragment.app.activityViewModels
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.filter.FilterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientFilterFragment : FilterFragment() {

    override val filterSharedViewModel: PatientFilterViewModel by activityViewModels()

    override val availableFilters = listOf(
        R.id.chip_date,
        R.id.chip_medication,
        R.id.chip_lab_test,
        R.id.chip_covid_test,
        R.id.chip_immunizations,
        R.id.chip_health_visits,
        R.id.chip_special_authority
    )
}
