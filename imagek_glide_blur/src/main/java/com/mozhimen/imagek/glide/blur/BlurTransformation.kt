package com.mozhimen.imagek.glide.blur

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.IntRange
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.mozhimen.kotlin.utilk.android.app.UtilKApplicationWrapper
import com.mozhimen.blurk.BlurKFast
import com.mozhimen.blurk.BlurKRenderScript
import java.security.MessageDigest

/**
 * @ClassName BlurTransformation
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
class BlurTransformation @JvmOverloads constructor(
    @IntRange(from = 1, to = 25)
    private val radius: Int = BLUR_MAX_RADIUS,
    private val sampling: Int = BLUR_DEFAULT_DOWN_SAMPLING
) : BitmapTransformation() {

    private val _context by lazy { UtilKApplicationWrapper.instance.get() }

    companion object {
        private const val VERSION = 1
        private const val ID = "com.mozhimen.basick.imagek.glide.temps.BlurTransformation.$VERSION"
        const val BLUR_MAX_RADIUS = 25
        const val BLUR_DEFAULT_DOWN_SAMPLING = 1
    }

    ///////////////////////////////////////////////////////////////////////////////

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width: Int = toTransform.width
        val height: Int = toTransform.height
        val scaledWidth: Int = width / sampling
        val scaledHeight: Int = height / sampling

        var bitmap = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)

        setCanvasBitmapDensity(toTransform, bitmap)

        val canvas = Canvas(bitmap)
        canvas.scale(1f / sampling.toFloat(), 1f / sampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        bitmap = try {
            BlurKRenderScript.blurBitmapOfAndroid2(_context, bitmap, radius.toFloat())
        } catch (e: Exception) {
            try {
                BlurKRenderScript.blurBitmapOfAndroid1(_context, bitmap, radius.toFloat())
            } catch (e: Exception) {
                BlurKFast.blurBitmap(bitmap, radius, true)
            }
        }
        return bitmap
    }

    //////////////////////////////////////////////////////////////////////////////

    override fun toString(): String {
        return "BlurTransformation(radius=$radius, sampling=$sampling)"
    }

    override fun equals(other: Any?): Boolean {
        return other is BlurTransformation && other.radius == radius && other.sampling == sampling
    }

    override fun hashCode(): Int {
        return ID.hashCode() + radius * 1000 + sampling * 10
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + radius + sampling).toByteArray(CHARSET))
    }

    //////////////////////////////////////////////////////////////////////////////

    private fun setCanvasBitmapDensity(toTransform: Bitmap, canvasBitmap: Bitmap) {
        canvasBitmap.density = toTransform.density
    }
}