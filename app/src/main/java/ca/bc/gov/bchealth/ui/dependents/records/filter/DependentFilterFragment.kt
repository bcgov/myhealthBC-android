package ca.bc.gov.bchealth.ui.dependents.records.filter

import androidx.fragment.app.activityViewModels
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.filter.FilterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentFilterFragment : FilterFragment() {

    override val filterSharedViewModel: DependentFilterViewModel by activityViewModels()

    override val availableFilters = listOf(
        R.id.chip_covid_test,
        R.id.chip_immunizations,
        R.id.chip_lab_test
    )
}
