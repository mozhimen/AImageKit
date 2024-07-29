package com.mozhimen.imagek.coil2.temps

import android.graphics.Bitmap
import androidx.annotation.FloatRange
import coil.size.Size
import coil.transform.Transformation
import com.mozhimen.basick.utilk.android.graphics.applyBitmapAnyRotate
import com.mozhimen.basick.utilk.commons.IUtilK
import kotlin.math.abs

/**
 * @ClassName RotateTransformation
 * @Description TODO
 * @Author Daisy
 * @Date 2024/7/29 20:10
 * @Version 1.0
 */
class RotateTransformation(@FloatRange(from = -360.0, to = 360.0)private val _rotate:Float) : Transformation,IUtilK {
    override val cacheKey: String
        get() = "${this::class.java.name}-${if (_rotate<0) "_"+abs(_rotate) else _rotate}"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
//        val matrix = Matrix()
//        matrix.postRotate(-90f) // 逆时针旋转 90 度
        val rotatedBitmap = input.applyBitmapAnyRotate(_rotate)//Bitmap.createBitmap(input, 0, 0, input.width, input.height, matrix, true)
        if (rotatedBitmap != input) {
            input.recycle()
        }
        return rotatedBitmap
    }
}