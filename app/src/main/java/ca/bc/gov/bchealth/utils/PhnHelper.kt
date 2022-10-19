package ca.bc.gov.bchealth.utils

import androidx.core.widget.doOnTextChanged
import ca.bc.gov.bchealth.R
import com.google.android.material.textfield.TextInputLayout

/*
* Created by amit_metri on 10,February,2022
*/
class PhnHelper {

    fun validatePhnData(textInputLayout: TextInputLayout): Boolean {
        if (textInputLayout.editText?.text.isNullOrEmpty()) {
            textInputLayout.error = textInputLayout.context.getString(R.string.phn_number_required)
            showErrorState(textInputLayout)
            return false
        }
        return true
    }

    private fun showErrorState(textInputLayout: TextInputLayout) {
        textInputLayout.isErrorEnabled = true
        textInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                textInputLayout.isErrorEnabled = false
                textInputLayout.error = null
            }
        }
    }
}
