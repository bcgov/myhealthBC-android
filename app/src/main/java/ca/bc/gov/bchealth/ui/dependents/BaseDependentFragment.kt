package ca.bc.gov.bchealth.ui.dependents

import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper

abstract class BaseDependentFragment(contentLayoutId: Int) : BaseFragment(contentLayoutId) {

    fun confirmDeletion(patientId: Long, firstName: String) {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.dependents_management_remove_title),
            msg = getString(R.string.dependents_management_remove_body, firstName),
            positiveBtnMsg = getString(R.string.yes),
            negativeBtnMsg = getString(R.string.no),
            positiveBtnCallback = { deleteDependent(patientId) }
        )
    }

    abstract fun deleteDependent(patientId: Long)
}
