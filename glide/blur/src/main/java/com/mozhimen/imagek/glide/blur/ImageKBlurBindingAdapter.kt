package com.mozhimen.imagek.glide.blur

import android.widget.ImageView
import androidx.databinding.BindingAdapter

/**
 * @ClassName ImageKBlurBindingAdapter
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/2/26
 * @Version 1.0
 */
object ImageKBlurBindingAdapter {
    @JvmStatic
    @BindingAdapter(value = ["loadImageBlur_ofGlide", "placeholder"], requireAll = true)
    fun loadImageBlur_ofGlide(imageView: ImageView, loadImageBlur_ofGlide: Any, placeholder: Int) {
        ImageKGlideBlur.loadImageBlur_ofGlide(imageView, loadImageBlur_ofGlide, placeholder)
    }
}