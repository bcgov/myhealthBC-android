package ca.bc.gov.bchealth.ui.dependents.records.filter

import androidx.fragment.app.activityViewModels
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.filter.FilterFragment
import ca.bc.gov.common.BuildConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentFilterFragment : FilterFragment() {

    override val filterSharedViewModel: DependentFilterViewModel by activityViewModels()

    override val availableFilters = mutableListOf(
        R.id.chip_covid_test,
        R.id.chip_immunizations,
    ).apply {
        if (BuildConfig.FLAG_GUARDIAN_CLINICAL_DOCS) {
            this.add(R.id.chip_clinical_document)
        }
        if (BuildConfig.FLAG_GUARDIAN_LABS) {
            this.add(R.id.chip_lab_results)
        }
    }
}
