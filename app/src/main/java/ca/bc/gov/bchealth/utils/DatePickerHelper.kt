package ca.bc.gov.bchealth.utils

import android.content.Context
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.utils.toDate
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
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
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        textInputLayout.editText?.setOnClickListener {
            datePicker.show(parentFragmentManager, tag)
        }
        textInputLayout.setEndIconOnClickListener {
            datePicker.show(parentFragmentManager, tag)
        }
        datePicker.addOnPositiveButtonClickListener {
            textInputLayout.editText?.setText(simpleDateFormat.format(it.adjustOffset()))
        }
    }

    fun initFilterDatePicker(
        textInputLayout: TextInputLayout,
        title: String,
        parentFragmentManager: FragmentManager,
        tag: String,
        selectedDate: String?
    ) {
        val selectedTimeInMillis = selectedDate?.let { it.toDate().toEpochMilli() }
            ?: run { MaterialDatePicker.todayInUtcMilliseconds() }

        val constraints: CalendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .setOpenAt(selectedTimeInMillis)
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(selectedTimeInMillis)
            .setCalendarConstraints(constraints)
            .build()
        textInputLayout.editText?.setOnClickListener {
            datePicker.show(parentFragmentManager, tag)
        }
        textInputLayout.setEndIconOnClickListener {
            datePicker.show(parentFragmentManager, tag)
        }
        datePicker.addOnPositiveButtonClickListener {
            textInputLayout.editText?.setText(simpleDateFormat.format(it.adjustOffset()))
        }
    }

    fun validateDatePickerData(
        textInputLayout: TextInputLayout,
        context: Context,
        errorMessage: String,
        isBlankAllowed: Boolean = false
    ): Boolean {
        if (isBlankAllowed) {
            return true
        } else {
            if (textInputLayout.editText?.text.isNullOrEmpty()) {
                updateErrorMessage(textInputLayout, errorMessage)
                return false
            }

            if (!textInputLayout.editText?.text.toString()
                    .matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) ||
                !textInputLayout.editText?.text.toString()
                    .matches(Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
            ) {
                updateErrorMessage(
                    textInputLayout,
                    context.getString(R.string.enter_valid_date_format)
                )
                return false
            }
        }
        return true
    }

    private fun updateErrorMessage(
        textInputLayout: TextInputLayout,
        errorMessage: String
    ) {
        textInputLayout.isErrorEnabled = true
        textInputLayout.error = errorMessage
        textInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                textInputLayout.isErrorEnabled = false
                textInputLayout.error = null
            }
        }
    }
}
