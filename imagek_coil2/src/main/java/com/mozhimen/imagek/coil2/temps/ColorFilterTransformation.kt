package com.mozhimen.imagek.coil2.temps

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.annotation.ColorInt
import androidx.core.graphics.createBitmap
import coil.size.Size
import coil.transform.Transformation
import com.mozhimen.imagek.coil2.commons.ITransformation

/**
 * @ClassName ColorFilterTransformation
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/11/6 19:05
 * @Version 1.0
 */
class ColorFilterTransformation(
    @ColorInt private val _color: Int
) : Transformation, ITransformation {

    override val cacheKey: String = "${this::class.java.name}-$_color"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val output = createBitmap(input.width, input.height, input.safeConfig)

        val canvas = Canvas(output)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(_color, PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(input, 0f, 0f, paint)

        return output
    }
}