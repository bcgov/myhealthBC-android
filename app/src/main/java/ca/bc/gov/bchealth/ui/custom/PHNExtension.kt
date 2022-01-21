package ca.bc.gov.bchealth.ui.custom

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import com.google.android.material.textfield.TextInputLayout

/*
* Created by amit_metri on 13,January,2022
*/

// TODO: 21/01/22 Remove extension in Fragment
fun Fragment.validatePhnNumber(tipPhn: TextInputLayout): Boolean {
    if (tipPhn.editText?.text.isNullOrEmpty()) {
        tipPhn.error = getString(R.string.phn_number_required)
        showErrorState(tipPhn)
        return false
    }

    if (tipPhn.editText?.text?.matches(Regex("^9[0-9]{9}\$")) == false) {
        tipPhn.error = getString(R.string.invalid_phn)
        showErrorState(tipPhn)
        return false
    }

    return true
}

private fun showErrorState(tipPhn: TextInputLayout) {
    tipPhn.isErrorEnabled = true
    tipPhn.editText?.doOnTextChanged { text, _, _, _ ->
        if (text != null && text.isNotEmpty()) {
            tipPhn.isErrorEnabled = false
            tipPhn.error = null
        }
    }
}
