package com.mozhimen.imagek.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import android.widget.ImageView
import androidx.annotation.WorkerThread
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.Transition
import com.mozhimen.basick.elemk.commons.I_AListener
import com.mozhimen.basick.elemk.commons.I_Listener
import com.mozhimen.imagek.glide.commons.ICustomTarget
import com.mozhimen.imagek.glide.impls.RoundedBorderTransformation
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.kotlinx.coroutines.safeResume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * @ClassName ImageKGlide
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/6/10 16:53
 * @Version 1.0
 */
suspend fun String.isImageHorizontal(context: Context?): Boolean =
    ImageKGlide.isImageHorizontal(this, context)

suspend fun String.isImageVertical(context: Context?): Boolean =
    ImageKGlide.isImageVertical(this, context)

//////////////////////////////////////////////////////////////////

fun ImageView.loadImage_ofGlide(res: Any) {
    ImageKGlide.loadImage_ofGlide(this, res)
}

fun ImageView.loadImageRoundedCorner_ofGlide(res: Any, radius: Int) {
    ImageKGlide.loadImageRoundedCorner_ofGlide(this, res, radius)
}

fun ImageView.loadImageComplex_ofGlide(
    res: Any, placeholder: Int, error: Int
) {
    ImageKGlide.loadImageComplex_ofGlide(this, res, placeholder, error)
}

//////////////////////////////////////////////////////////////////

object ImageKGlide : IUtilK {

    @JvmStatic
    suspend fun getImageWidthAndHeight(res: Any?, context: Context?): Pair<Int, Int> = suspendCancellableCoroutine { coroutine ->
        contractImage_ofGlide(context, {
            Glide.with(context!!).asBitmap().load(res).into(object : ICustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    UtilKLogWrapper.d(TAG, "onResourceReady: res $res resource width ${resource.width} height ${resource.height}")
                    coroutine.safeResume(resource.width to resource.height)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    UtilKLogWrapper.d(TAG, "onLoadFailed: resource width 0 height 0")
                    coroutine.safeResume(0 to 0)
                }
            })
        }, {
            UtilKLogWrapper.d(TAG, "onLoadFailed: onError of glide")
            coroutine.safeResume(0 to 0)
        })
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
     * 是否是横图
     */
    @JvmStatic
    suspend fun isImageHorizontal(res: Any?, context: Context?): Boolean {
        val imageSize = getImageWidthAndHeight(res, context)
        return imageSize.first > imageSize.second
    }

    /**
     * 是否是竖图
     */
    @JvmStatic
    suspend fun isImageVertical(res: Any?, context: Context?): Boolean {
        val imageSize = getImageWidthAndHeight(res, context)
        return imageSize.first < imageSize.second
    }

    //////////////////////////////////////////////////////////////////////////////////

    @JvmStatic
    @WorkerThread
    fun obj2Bitmap(obj: Any, context: Context?, placeholder: Int, width: Int, height: Int): Bitmap? {
        return contractImageRes_ofGlide(context) {
            Glide.with(context!!).asBitmap().load(obj)
                .centerCrop()
                .placeholder(placeholder)
                .error(placeholder)
                .into(width, height)
                .get()
        }
    }

    @JvmStatic
    @WorkerThread
    fun obj2Bitmap(obj: Any, context: Context?, placeholder: Int, width: Int, height: Int, cornerRadius: Int): Bitmap? {
        return contractImageRes_ofGlide(context) {
            Glide.with(context!!).asBitmap().load(obj)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .placeholder(placeholder)
                .error(placeholder)
                .into(width, height)
                .get()
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    @JvmStatic
    fun loadImage_ofGlide(
        imageView: ImageView, res: Any?
    ) {
        contractImage_ofGlide(imageView.context, {
            Glide.with(imageView).load(res)
                .into(imageView)
        })
    }

    @JvmStatic
    fun loadImageComplex_ofGlide(
        imageView: ImageView,
        res: Any?,
        placeholder: Int,
        error: Int
    ) {
        contractImage_ofGlide(imageView.context, {
            Glide.with(imageView).load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(error)
                .placeholder(placeholder)
                .into(imageView)
        })
    }

    /**
     * 加载圆形图片
     */
    @JvmStatic
    fun loadImageCircle_ofGlide(
        imageView: ImageView,
        res: Any?,
        placeholder: Int,
        error: Int
    ) {
        contractImage_ofGlide(imageView.context, {
            Glide.with(imageView).load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CircleCrop())
                .placeholder(placeholder)
                .error(error)
                .into(imageView)
        })
    }

    /**
     * 加载带边框的圆角图片
     */
    @JvmStatic
    fun loadImageBorderRoundedCorner_ofGlide(
        imageView: ImageView,
        res: Any?,
        placeholder: Int,
        error: Int,
        borderWidth: Float,
        borderColor: Int
    ) {
        contractImage_ofGlide(imageView.context,{
            Glide.with(imageView).load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(RoundedBorderTransformation(borderWidth, borderColor))
                .placeholder(placeholder)
                .error(error)
                .into(imageView)
        })
    }

    /**
     * 加载圆角图片
     */
    @JvmStatic
    fun loadImageRoundedCorner_ofGlide(
        imageView: ImageView,
        res: Any?,
        placeholder: Int,
        error: Int,
        cornerRadius: Int
    ) {
        contractImage_ofGlide(imageView.context,{
            Glide.with(imageView).load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CenterCrop(), RoundedCorners(cornerRadius))
                .placeholder(placeholder)
                .error(error)
                .into(imageView)
        })
    }

    @JvmStatic
    fun loadImageRoundedCorner_ofGlide(
        imageView: ImageView,
        res: Any?,
        cornerRadius: Int
    ) {
        contractImage_ofGlide(imageView.context,{
            Glide.with(imageView).load(res)
//            .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CenterCrop(), RoundedCorners(cornerRadius))
                .into(imageView)
        })
    }

    @JvmStatic
    fun contractImage_ofGlide(context: Context?, onContinue: I_Listener, onError: I_Listener? = null) {
        if (context != null /*&& context is Activity && !context.isFinishingOrDestroyed()*/) {
            try {
                onContinue.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke()
            }
        } else {
            onError?.invoke()
        }
    }

    @JvmStatic
    fun <T> contractImageRes_ofGlide(context: Context?, onContinue: I_AListener<T>): T? {
        if (context != null /*&& context is Activity && !context.isFinishingOrDestroyed()*/) {
            try {
                return onContinue.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}