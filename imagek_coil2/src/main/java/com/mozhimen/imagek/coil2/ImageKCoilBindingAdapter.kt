package com.mozhimen.imagek.coil2

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.mozhimen.basick.utilk.android.util.dp2px

/**
 * @ClassName ImageKCoilBindingAdapter
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/1/16
 * @Version 1.0
 */
object ImageKCoilBindingAdapter {
    @JvmStatic
    @BindingAdapter(value = ["loadImageWhen_ofCoil", "loadImageWhen_ofCoil_statusTrue", "loadImageWhen_ofCoil_statusFalse"], requireAll = true)
    fun loadImageWhen_ofCoil(imageView: ImageView, loadImageWhenCoil: Boolean, loadImageWhen_ofCoil_statusTrue: Any, loadImageWhen_ofCoil_statusFalse: Any) {
        if (loadImageWhenCoil) {
            ImageKCoil.loadImage_ofCoil(imageView, loadImageWhen_ofCoil_statusTrue)
        } else {
            ImageKCoil.loadImage_ofCoil(imageView, loadImageWhen_ofCoil_statusFalse)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["loadImageWhen2_ofCoil", "loadImageWhen2_ofCoil_condition2", "loadImageWhen2_ofCoil_status1", "loadImageWhen2_ofCoil_status2", "loadImageWhen2_ofCoil_status3", "loadImageWhen2_ofCoil_status4"], requireAll = true)
    fun loadImageWhen2_ofCoil(
        imageView: ImageView,
        loadImageWhen2_ofCoil: Boolean,
        loadImageWhen2_ofCoil_condition2: Boolean,
        loadImageWhen2_ofCoil_status1: Any,
        loadImageWhen2_ofCoil_status2: Any,
        loadImageWhen2_ofCoil_status3: Any,
        loadImageWhen2_ofCoil_status4: Any
    ) {
        when {
            loadImageWhen2_ofCoil && loadImageWhen2_ofCoil_condition2 -> ImageKCoil.loadImage_ofCoil(imageView, loadImageWhen2_ofCoil_status1)
            loadImageWhen2_ofCoil && !loadImageWhen2_ofCoil_condition2 -> ImageKCoil.loadImage_ofCoil(imageView, loadImageWhen2_ofCoil_status2)
            !loadImageWhen2_ofCoil && loadImageWhen2_ofCoil_condition2 -> ImageKCoil.loadImage_ofCoil(imageView, loadImageWhen2_ofCoil_status3)
            !loadImageWhen2_ofCoil && !loadImageWhen2_ofCoil_condition2 -> ImageKCoil.loadImage_ofCoil(imageView, loadImageWhen2_ofCoil_status4)
        }
    }

    @JvmStatic
    @BindingAdapter("loadImage_ofCoil")
    fun loadImage_ofCoil(imageView: ImageView, loadImage_ofCoil: Any) {
        ImageKCoil.loadImage_ofCoil(imageView, loadImage_ofCoil)
    }

    @JvmStatic
    @BindingAdapter(value = ["loadImageBlur_ofCoil", "placeholder"], requireAll = true)
    fun loadImageBlur_ofCoil(imageView: ImageView, loadImageBlur_ofCoil: Any, placeholder: Int) {
        ImageKCoil.loadImageBlur_ofCoil(imageView, loadImageBlur_ofCoil, placeholder)
    }

    @JvmStatic
    @BindingAdapter(value = ["loadImageRoundedCorner_ofCoil", "roundedCornerRadius"], requireAll = true)
    fun loadImageRoundedCorner_ofCoil(imageView: ImageView, loadImageRoundedCorner_ofCoil: Any, roundedCornerRadius: Int) {
        ImageKCoil.loadImageRoundedCorner_ofCoil(imageView, loadImageRoundedCorner_ofCoil, roundedCornerRadius.dp2px())
    }

    @JvmStatic
    @BindingAdapter(value = ["loadImageRoundedCorner_ofCoil", "roundedCornerRadius"], requireAll = true)
    fun loadImageRoundedCorner_ofCoil(imageView: ImageView, loadImageRoundedCorner_ofCoil: Any, roundedCornerRadius: Float) {
        ImageKCoil.loadImageRoundedCorner_ofCoil(imageView, loadImageRoundedCorner_ofCoil, roundedCornerRadius.dp2px())
    }
}