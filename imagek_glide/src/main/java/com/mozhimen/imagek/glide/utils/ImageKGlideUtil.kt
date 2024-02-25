package com.mozhimen.imagek.glide.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import com.mozhimen.basick.lintk.optins.permission.OPermission_INTERNET
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.imagek.glide.ImageKGlide

/**
 * @ClassName ImageKGlideUtil
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/2/24 23:22
 * @Version 1.0
 */
@RequiresPermission(CPermission.INTERNET)
@OPermission_INTERNET
@WorkerThread
fun String.strUrl2bitmapOfGlide(context: Context?, placeholder: Int, width: Int, height: Int): Bitmap? =
    ImageKGlideUtil.strUrl2bitmapOfGlide(this, context, placeholder, width, height)

@RequiresPermission(CPermission.INTERNET)
@OPermission_INTERNET
@WorkerThread
fun String.strUrl2bitmapOfGlide(context: Context?, placeholder: Int, width: Int, height: Int, cornerRadius: Int): Bitmap? =
    ImageKGlideUtil.strUrl2bitmapOfGlide(this, context, placeholder, width, height, cornerRadius)

object ImageKGlideUtil {

    @JvmStatic
    @RequiresPermission(CPermission.INTERNET)
    @OPermission_INTERNET
    @WorkerThread
    fun strUrl2bitmapOfGlide(strUrl: String, context: Context?, placeholder: Int, width: Int, height: Int): Bitmap? =
        ImageKGlide.obj2Bitmap(strUrl, context, placeholder, width, height)

    @JvmStatic
    @RequiresPermission(CPermission.INTERNET)
    @OPermission_INTERNET
    @WorkerThread
    fun strUrl2bitmapOfGlide(strUrl: String, context: Context?, placeholder: Int, width: Int, height: Int, cornerRadius: Int): Bitmap? =
        ImageKGlide.obj2Bitmap(strUrl, context, placeholder, width, height, cornerRadius)
}