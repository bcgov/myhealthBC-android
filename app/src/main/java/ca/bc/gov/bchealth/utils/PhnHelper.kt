package ca.bc.gov.bchealth.utils

import android.content.Context
import androidx.core.widget.doOnTextChanged
import ca.bc.gov.bchealth.R
import com.google.android.material.textfield.TextInputLayout

/*
* Created by amit_metri on 10,February,2022
*/
class PhnHelper {

    fun validatePhnData(
        textInputLayout: TextInputLayout,
        context: Context
    ): Boolean {
        if (textInputLayout.editText?.text.isNullOrEmpty()) {
            textInputLayout.error = context.getString(R.string.phn_number_required)
            showErrorState(textInputLayout)
            return false
        }

        if (textInputLayout.editText?.text?.matches(Regex("^9[0-9]{9}\$")) == false) {
            textInputLayout.error = context.getString(R.string.invalid_phn)
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
