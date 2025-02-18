package com.mozhimen.imagek.glide.impls

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.annotation.Px
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * @ClassName GlideRoundTransform
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/6/20
 * @Version 1.0
 */
/**
 * @ClassName GlideRoundTransform
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/6/20
 * @Version 1.0
 */
/***
 * glide 图片设置圆角
 *
 * //第一个是上下文，第二个是圆角的弧度
 * RequestOptions myOptions = new RequestOptions()
 * .transform(new GlideRoundTransform(this,30));
 */
class BitmapCornerRoundedTransformation(@Px private val _cornerRadius: Float) : BitmapTransformation() {
    companion object{
        private const val ID: String = "com.mozhimen.imagek.glide.impls.RoundedBitmapTransformation"
        private val ID_BYTES: ByteArray = ID.toByteArray(CHARSET)
    }

    /////////////////////////////////////////////////////////////////////////

    private val _paintRounded = Paint().apply {
        isAntiAlias = true
    }

    /////////////////////////////////////////////////////////////////////////

    override fun equals(other: Any?): Boolean {
        return other is BitmapCornerRoundedTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val transform = pool[toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888]
        val canvas = Canvas(transform)
        _paintRounded.setShader(BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
        val rectF = RectF(0f, 0f, toTransform.width.toFloat(), toTransform.height.toFloat())
        canvas.drawRoundRect(rectF, _cornerRadius, _cornerRadius, _paintRounded)
        return transform
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }
}