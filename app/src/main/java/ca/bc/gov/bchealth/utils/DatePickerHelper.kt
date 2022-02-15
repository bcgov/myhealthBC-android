package ca.bc.gov.bchealth.utils

import android.content.Context
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import ca.bc.gov.bchealth.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Locale

/*
* Created by amit_metri on 10,February,2022
*/
class DatePickerHelper {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

    fun initializeDatePicker(
        textInputLayout: TextInputLayout,
        title: String,
        parentFragmentManager: FragmentManager,
        tag: String
    ) {
        val dateOfBirthPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        textInputLayout.editText?.setOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, tag)
        }
        textInputLayout.setEndIconOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, tag)
        }
        dateOfBirthPicker.addOnPositiveButtonClickListener {
            textInputLayout.editText?.setText(simpleDateFormat.format(it.adjustOffset()))
        }
    }

    fun validateDatePickerData(
        textInputLayout: TextInputLayout,
        context: Context,
        errorMessage: String
    ): Boolean {
        if (textInputLayout.editText?.text.isNullOrEmpty()) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = errorMessage
            textInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    textInputLayout.isErrorEnabled = false
                    textInputLayout.error = null
                }
            }
            return false
        }

        if (!textInputLayout.editText?.text.toString()
            .matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) ||

            !textInputLayout.editText?.text.toString()
                .matches(Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
        ) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = context.getString(R.string.enter_valid_date_format)
            textInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    textInputLayout.isErrorEnabled = false
                    textInputLayout.error = null
                }
            }
            return false
        }
        return true
    }
}
