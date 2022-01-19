package ca.bc.gov.bchealth.ui.custom

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.utils.adjustOffset
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Locale

/*
* @author amit_metri on 13,January,2022
*/

val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

fun Fragment.setUpDatePickerUi(tipDatePicker: TextInputLayout, tag: String) {

    val dateOfBirthPicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
    tipDatePicker.editText?.setOnClickListener {
        dateOfBirthPicker.show(parentFragmentManager, tag)
    }
    tipDatePicker.setEndIconOnClickListener {
        dateOfBirthPicker.show(parentFragmentManager, tag)
    }
    dateOfBirthPicker.addOnPositiveButtonClickListener {
        tipDatePicker.editText?.setText(simpleDateFormat.format(it.adjustOffset()))
    }
}

fun Fragment.validateDatePickerData(
    tipDatePicker: TextInputLayout,
    errorMessage: String
): Boolean {
    if (tipDatePicker.editText?.text.isNullOrEmpty()) {
        tipDatePicker.isErrorEnabled = true
        tipDatePicker.error = errorMessage
        tipDatePicker.editText?.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                tipDatePicker.isErrorEnabled = false
                tipDatePicker.error = null
            }
        }
        return false
    }

    if (!tipDatePicker.editText?.text.toString()
        .matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) ||

        !tipDatePicker.editText?.text.toString()
            .matches(Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
    ) {
        tipDatePicker.isErrorEnabled = true
        tipDatePicker.error = getString(R.string.enter_valid_date_format)
        tipDatePicker.editText?.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                tipDatePicker.isErrorEnabled = false
                tipDatePicker.error = null
            }
        }
        return false
    }

    return true
}
