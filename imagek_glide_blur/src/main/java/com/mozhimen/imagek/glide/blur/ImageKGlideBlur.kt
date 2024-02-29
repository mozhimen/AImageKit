package com.mozhimen.imagek.glide.blur

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mozhimen.imagek.glide.ImageKGlide

/**
 * @ClassName ImageKGlideBlur
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/2/26
 * @Version 1.0
 */
object ImageKGlideBlur {
    @JvmStatic
    fun loadImageGlideBlur(
        imageView: ImageView,
        res: Any?,
        placeholder: Int,
        error: Int,
        radius: Int = BlurTransformation.BLUR_MAX_RADIUS,
        sampling: Int = BlurTransformation.BLUR_DEFAULT_DOWN_SAMPLING
    ) {
        ImageKGlide.contractImageGlide(imageView.context, {
            Glide.with(imageView)
                .load(res)
                .placeholder(placeholder)
                .error(error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(BlurTransformation(radius, sampling))
                .into(imageView)
        })
    }

    @JvmStatic
    fun loadImageGlideBlur(
        imageView: ImageView,
        res: Any?,
        placeholder: Int,
        radius: Int = BlurTransformation.BLUR_MAX_RADIUS,
        sampling: Int = BlurTransformation.BLUR_DEFAULT_DOWN_SAMPLING
    ) {
        ImageKGlide.contractImageGlide(imageView.context, {
            Glide.with(imageView)
                .load(res)
                .placeholder(placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(BlurTransformation(radius, sampling))
                .into(imageView)
        })
    }
}