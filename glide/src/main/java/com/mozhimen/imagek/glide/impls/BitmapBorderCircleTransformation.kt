package com.mozhimen.imagek.glide.impls

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.Px
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import java.security.MessageDigest

/**
 * @ClassName CircleBorderTransform
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Version 1.0
 */
class BitmapBorderCircleTransformation(@Px private val _borderWidth: Float, borderColor: Int) : CircleCrop() {
    companion object{
        private const val ID: String = "com.mozhimen.imagek.glide.impls.CircleBorderTransformation"
        private val ID_BYTES: ByteArray = ID.toByteArray(CHARSET)
    }

    /////////////////////////////////////////////////////////////////////////

    private val _paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
        style = Paint.Style.STROKE
        strokeWidth = _borderWidth
    }

    /////////////////////////////////////////////////////////////////////////

    override fun equals(other: Any?): Boolean {
        return other is BitmapBorderCircleTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val transform = super.transform(pool, toTransform, outWidth, outHeight)
        val canvas = Canvas(transform)
        val radiusWidth = outWidth / 2f
        val radiusHeight = outHeight / 2f
        canvas.drawCircle(
            radiusWidth,
            radiusHeight,
            radiusWidth.coerceAtMost(radiusHeight) - _borderWidth / 2f,
            _paintBorder
        )
        canvas.setBitmap(null)
        return transform
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }
}

