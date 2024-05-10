package com.mozhimen.imagek.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.RequiresPermission
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import com.mozhimen.basick.lintk.optins.permission.OPermission_INTERNET
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.utilk.bases.BaseUtilK

/**
 * @ClassName ImageKCoilUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/1/16
 * @Version 1.0
 */
@RequiresPermission(CPermission.INTERNET)
@OPermission_INTERNET
suspend fun String.strUrl2bitmapAnyOfCoil(): Bitmap? =
    ImageKCoilUtil.strUrl2bitmapAnyOfCoil(this)

object ImageKCoilUtil : BaseUtilK(){
    @JvmStatic
    @RequiresPermission(CPermission.INTERNET)
    @OPermission_INTERNET
    suspend fun strUrl2bitmapAnyOfCoil(strUrl: String): Bitmap? =
        (getImageLoader(_context).execute(ImageRequest.Builder(_context).data(strUrl).build()).drawable as? BitmapDrawable)?.bitmap

    @JvmStatic
    fun getImageLoader(context: Context): ImageLoader =
        context.imageLoader
}