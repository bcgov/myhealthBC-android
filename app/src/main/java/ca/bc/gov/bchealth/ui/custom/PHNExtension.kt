package ca.bc.gov.bchealth.ui.custom

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import com.google.android.material.textfield.TextInputLayout

/*
* Created by amit_metri on 13,January,2022
*/

fun Fragment.validatePhnNumber(tipPhn: TextInputLayout, errorMessage: String): Boolean {
    if (tipPhn.editText?.text.isNullOrEmpty()) {
        tipPhn.isErrorEnabled = true
        tipPhn.error = getString(R.string.phn_number_required)
        tipPhn.editText?.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                tipPhn.isErrorEnabled = false
                tipPhn.error = null
            }
        }
        return false
    }

    if (tipPhn.editText?.text?.length != 10) {
        tipPhn.isErrorEnabled = true
        tipPhn.error = getString(R.string.phn_should_be_10_digit)
        tipPhn.editText?.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                tipPhn.isErrorEnabled = false
                tipPhn.error = null
            }
        }
        return false
    }

    return true
}
