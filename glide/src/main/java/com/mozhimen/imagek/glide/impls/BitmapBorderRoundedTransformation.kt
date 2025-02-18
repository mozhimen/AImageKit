package com.mozhimen.imagek.glide.impls

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
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
class BitmapBorderRoundedTransformation(@Px private val _cornerRadius: Float, @Px private val _borderWidth: Float, borderColor: Int) : CircleCrop() {
    companion object{
        private const val ID: String = "com.mozhimen.imagek.glide.impls.RoundedBorderTransformation"
        private val ID_BYTES: ByteArray = ID.toByteArray(CHARSET)
    }

    /////////////////////////////////////////////////////////////////////////

    private val _paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
        style = Paint.Style.STROKE
        strokeWidth = _borderWidth
    }
    private val _paintRounded = Paint().apply {
        isAntiAlias = true
    }

    /////////////////////////////////////////////////////////////////////////

    override fun equals(other: Any?): Boolean {
        return other is BitmapBorderRoundedTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val transform = pool[toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888]
//        val canvas = Canvas(transform)
//        _paintRounded.setShader(BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
//        val rectF = RectF(0f, 0f, toTransform.width.toFloat(), toTransform.height.toFloat())
//        canvas.drawRoundRect(rectF, _cornerRadius, _cornerRadius, _paintRounded)
//        // 绘制边框
//        if (_borderWidth > 0) {
//            canvas.drawRoundRect(rectF, _cornerRadius, _cornerRadius, _paintBorder)
//        }
        transform.setHasAlpha(true)

        val canvas = Canvas(transform)

        // 绘制圆角
        val rect = RectF(0f, 0f, toTransform.width.toFloat(), toTransform.height.toFloat())
        val path = Path().apply {
            addRoundRect(rect, _cornerRadius, _cornerRadius, Path.Direction.CCW)
        }
        canvas.clipPath(path)

        // 绘制图片
        canvas.drawBitmap(toTransform, 0f, 0f, _paintRounded)

        // 绘制边框
        if (_borderWidth > 0) {
            canvas.drawRoundRect(rect, _cornerRadius, _cornerRadius, _paintBorder)
        }
        return transform
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }
}

