package ca.bc.gov.bchealth.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import ca.bc.gov.bchealth.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

/*
* Created by amit_metri on 13,October,2021
*/
class ResourceCustomView @JvmOverloads
constructor(
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    //Default values
    var textDescription: String = ""
    @DrawableRes
    var imgSrc: Int = R.drawable.ic_resources_icon_1

    init {
        // get the inflater service from the android system
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // the array of attributes
        val attributes = ctx.obtainStyledAttributes(attributeSet, R.styleable.ResourceCustomView)

        readCustomAttributes(attributes)

        attributes.recycle()

        // inflate the layout into "this" component
        val view = inflater.inflate(R.layout.layout_resource_custom_view, this)

        initializeViews(view)

    }

    private fun initializeViews(view: View) {
        val textView: MaterialTextView = view.findViewById(R.id.txt_label);
        textView.text = textDescription

        val imageView: ShapeableImageView = view.findViewById(R.id.img_icon);
        imageView.setImageResource(imgSrc)
    }

    private fun readCustomAttributes(attributes: TypedArray) {
        textDescription = attributes.getText(R.styleable.ResourceCustomView_textSource).toString()
        imgSrc = attributes.getResourceId(R.styleable.ResourceCustomView_imageSource, imgSrc)
    }
}