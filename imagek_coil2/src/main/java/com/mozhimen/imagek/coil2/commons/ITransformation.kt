package com.mozhimen.imagek.coil2.commons

import android.graphics.Bitmap
import com.mozhimen.kotlin.utilk.commons.IUtilK

/**
 * @ClassName ITransformation
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/11/6 19:06
 * @Version 1.0
 */
internal interface ITransformation :IUtilK{
    val Bitmap.safeConfig: Bitmap.Config
        get() = config ?: Bitmap.Config.ARGB_8888
}