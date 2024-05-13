package com.mozhimen.imagek.coil2

import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.mozhimen.imagek.coil2.cons.CCoilBlurCons
import com.mozhimen.imagek.coil2.temps.BlurTransformation
import com.mozhimen.imagek.coil2.temps.ColorFilterTransformation
import com.mozhimen.imagek.coil2.temps.CropTransformation
import com.mozhimen.imagek.coil2.temps.GrayscaleTransformation
import com.mozhimen.basick.utilk.android.util.dp2px

/**
 * @ClassName UtilKImageLoader
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/11/6 0:25
 * @Version 1.0
 */
fun ImageView.loadImage_ofCoil(res: Any) {
    ImageKCoil.loadImage_ofCoil(this, res)
}

fun ImageView.loadImageComplex_ofCoil(
    res: Any, placeholder: Int, error: Int,
    crossFadeEnable: Boolean = true, crossFadeTime: Int = 1500
) {
    ImageKCoil.loadImageComplex_ofCoil(this, res, placeholder, error, crossFadeEnable, crossFadeTime)
}

fun ImageView.loadImageBlur_ofCoil(
    res: Any, placeholder: Int,
    crossFadeEnable: Boolean = true, crossFadeTime: Int = 1500,
    @FloatRange(from = 0.0, to = 25.0) radius: Float = CCoilBlurCons.RADIUS,
    @FloatRange(from = 0.0, to = Double.MAX_VALUE) sampling: Float = CCoilBlurCons.SAMPLING
) {
    ImageKCoil.loadImageBlur_ofCoil(this, res, placeholder, crossFadeEnable, crossFadeTime, radius, sampling)
}

fun ImageView.loadImageGray_ofCoil(res: Any) {
    ImageKCoil.loadImageGray_ofCoil(this, res)
}

fun ImageView.loadImageColorFilter_ofCoil(res: Any, @ColorInt color: Int) {
    ImageKCoil.loadImageColorFilter_ofCoil(this, res, color)
}

fun ImageView.loadImageCircle_ofCoil(res: Any) {
    ImageKCoil.loadImageCircle_ofCoil(this, res)
}

fun ImageView.loadImageCircleComplex_ofCoil(
    res: Any, placeholder: Int, error: Int,
    crossFadeEnable: Boolean = true, crossFadeTime: Int = 1000
) {
    ImageKCoil.loadImageCircleComplex_ofCoil(this, res, placeholder, error, crossFadeEnable, crossFadeTime)
}

fun ImageView.loadImageRoundedCorner_ofCoil(
    res: Any, @Px cornerRadius: Float = 6f.dp2px()
) {
    ImageKCoil.loadImageRoundedCorner_ofCoil(this, res, cornerRadius)
}

fun ImageView.loadImageCrop_ofCoil(
    res: Any, cropType: CropTransformation.ECropType = CropTransformation.ECropType.CENTER
) {
    ImageKCoil.loadImageCrop_ofCoil(this, res, cropType)
}

object ImageKCoil {

    @JvmStatic
    fun loadImage_ofCoil(imageView: ImageView, res: Any) {
        imageView.load(res)
    }

    @JvmStatic
    fun loadImageComplex_ofCoil(
        imageView: ImageView, res: Any, placeholder: Int, error: Int,
        crossFadeEnable: Boolean = true,
        crossFadeTime: Int = 1000
    ) {
        imageView.load(res) {
            crossfade(crossFadeEnable)
            crossfade(crossFadeTime)
            placeholder(placeholder)
            error(error)
        }
    }

    /**
     * 加载高斯模糊图
     */
    @JvmStatic
    fun loadImageBlur_ofCoil(
        imageView: ImageView, res: Any, placeholder: Int,
        crossFadeEnable: Boolean = true, crossFadeTime: Int = 1500,
        @FloatRange(from = 0.0, to = 25.0) radius: Float = CCoilBlurCons.RADIUS,
        @FloatRange(from = 0.0, to = Double.MAX_VALUE) sampling: Float = CCoilBlurCons.SAMPLING
    ) {
        imageView.load(res) {
            crossfade(crossFadeEnable)
            crossfade(crossFadeTime)
            placeholder(placeholder)
            transformations(BlurTransformation(imageView.context, radius, sampling))
        }
    }

    /**
     * 加载灰度图
     */
    @JvmStatic
    fun loadImageGray_ofCoil(imageView: ImageView, res: Any) {
        imageView.load(res) {
            transformations(GrayscaleTransformation())
        }
    }

    /**
     * 加载颜色过滤图片
     */
    @JvmStatic
    fun loadImageColorFilter_ofCoil(imageView: ImageView, res: Any, @ColorInt color: Int) {
        imageView.load(res) {
            transformations(ColorFilterTransformation(color))
        }
    }

    /**
     * 加载圆形图片
     */
    @JvmStatic
    fun loadImageCircle_ofCoil(imageView: ImageView, res: Any) {
        imageView.load(res) {
            transformations(CircleCropTransformation())
        }
    }

    /**
     * 加载圆形图片
     */
    @JvmStatic
    fun loadImageCircleComplex_ofCoil(
        imageView: ImageView, res: Any, placeholder: Int, error: Int,
        crossFadeEnable: Boolean = true, crossFadeTime: Int = 1000
    ) {
        imageView.load(res) {
            transformations(CircleCropTransformation())
            crossfade(crossFadeEnable)
            crossfade(crossFadeTime)
            placeholder(placeholder)
            error(error)
        }
    }


    /**
     * 加载圆角图片
     */
    @JvmStatic
    fun loadImageRoundedCorner_ofCoil(
        imageView: ImageView, res: Any,
        @Px roundedCornerRadius: Float = 6f.dp2px()
    ) {
        imageView.load(res) {
            transformations(RoundedCornersTransformation(roundedCornerRadius))
        }
    }

    /**
     * 加载裁剪图片
     */
    @JvmStatic
    fun loadImageCrop_ofCoil(
        imageView: ImageView, res: Any,
        cropType: CropTransformation.ECropType = CropTransformation.ECropType.CENTER
    ) {
        imageView.load(res) {
            transformations(CropTransformation(cropType))
        }
    }
}