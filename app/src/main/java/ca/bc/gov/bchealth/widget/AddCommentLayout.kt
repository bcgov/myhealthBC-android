package ca.bc.gov.bchealth.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.AddCommentBinding
import ca.bc.gov.bchealth.utils.hideKeyboard
import ca.bc.gov.bchealth.utils.updateCommentEndIcon

/*
* Created by amit_metri on 24,June,2022
*/
class AddCommentLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var callback: AddCommentCallback? = null
    private val binding = AddCommentBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.tipComment.apply {
            updateCommentEndIcon(context)
            setEndIconOnClickListener {
                if (!binding.edComment.text.isNullOrBlank()) {
                    callback?.onSubmitComment(binding.edComment.text.toString())
                    context.hideKeyboard(this)
                }
            }
        }

        binding.edComment.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (after > 1000) {
                        binding.tipComment.apply {
                            isErrorEnabled = true
                            error = context.getString(R.string.error_max_character)
                        }
                    } else {
                        binding.tipComment.apply {
                            isErrorEnabled = false
                            error = null
                        }
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // no implementation required
                }

                override fun afterTextChanged(p0: Editable?) {
                    // no implementation required
                }
            }
        )
    }

    fun clearComment() {
        binding.edComment.setText("")
    }

    fun addCommentListener(listener: AddCommentCallback) {
        callback = listener
    }
}

interface AddCommentCallback {
    fun onSubmitComment(commentText: String)
}
