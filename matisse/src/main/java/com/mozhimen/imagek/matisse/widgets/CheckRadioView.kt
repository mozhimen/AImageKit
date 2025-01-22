package com.mozhimen.imagek.matisse.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.mozhimen.imagek.matisse.R

class CheckRadioView : AppCompatImageView {

    private var mDrawable: Drawable? = null
    private lateinit var selectedColorFilter: PorterDuffColorFilter
    private lateinit var unSelectUdColorFilter: PorterDuffColorFilter


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        val typedArray: TypedArray = context?.theme?.obtainStyledAttributes(intArrayOf(R.attr.ItemCheckRadioView_Theme)) ?: return
        val selectedColor =
            typedArray.getColor(0, ResourcesCompat.getColor(resources, R.color.matisse_selector_color_text_gray, context.theme)
        )
        val unSelectUdColor =
            ResourcesCompat.getColor(resources, com.mozhimen.xmlk.R.color.cok_gray_808080, context.theme)
        typedArray.recycle()

        selectedColorFilter = PorterDuffColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
        unSelectUdColorFilter = PorterDuffColorFilter(unSelectUdColor, PorterDuff.Mode.SRC_IN)
        setChecked(false)
    }

    fun setChecked(enable: Boolean) {
        if (enable) {
            setImageResource(R.drawable.matisse_ic_preview_radio_on)
            mDrawable = drawable
            mDrawable?.colorFilter = selectedColorFilter
        } else {
            setImageResource(R.drawable.matisse_ic_preview_radio_off)
            mDrawable = drawable
            mDrawable?.colorFilter = unSelectUdColorFilter
        }
    }

    fun setColor(color: Int) {
        if (mDrawable == null) {
            mDrawable = drawable
        }
        mDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}
