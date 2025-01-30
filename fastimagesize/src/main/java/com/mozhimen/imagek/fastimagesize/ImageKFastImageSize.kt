package com.mozhimen.imagek.fastimagesize

import android.util.Log
import com.mozhimen.kotlin.utilk.commons.IUtilK
import kotlinx.coroutines.suspendCancellableCoroutine
import q.rorbin.fastimagesize.FastImageSize
import q.rorbin.fastimagesize.cons.ImageType
import q.rorbin.fastimagesize.request.ImageSizeCallback
import kotlin.coroutines.resume
import kotlin.jvm.Throws

/**
 * @ClassName ImageKFastImageSize
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/28 10:58
 * @Version 1.0
 */
suspend fun String.isImageHorizontal(): Boolean =
    ImageKFastImageSize.isImageHorizontal(this)

suspend fun String.isImageVertical(): Boolean =
    ImageKFastImageSize.isImageVertical(this)

object ImageKFastImageSize : IUtilK {
    /**
     * 是否是横图
     */
    @JvmStatic
    @Throws(NullPointerException::class)
    suspend fun isImageHorizontal(strUrl: String): Boolean {
        val imageSize = getImageWidthAndHeight(strUrl)
        return imageSize.first > imageSize.second
    }

    /**
     * 是否是竖图
     */
    @JvmStatic
    @Throws(NullPointerException::class)
    suspend fun isImageVertical(strUrl: String): Boolean =
        !isImageHorizontal(strUrl)

    @JvmStatic
    @Throws(NullPointerException::class)
    suspend fun getImageWidthAndHeight(strUrl: String): Triple<Int, Int, Int> = suspendCancellableCoroutine { coroutine ->
        val startL = System.currentTimeMillis()
        FastImageSize
            .with(strUrl)[
            ImageSizeCallback { size: IntArray? ->
                val res = "图片尺寸: " + size.contentToString() + "   用时 : " + (System.currentTimeMillis() - startL) + "毫秒"
                Log.d(TAG, "getImageWidthAndHeight: res $res url $strUrl")
                size?.let {
                    coroutine.resume(Triple(size[0], size[1], size[2]))

                } ?: run {
                    coroutine.resume(Triple(0, 0, ImageType.NULL))
                }
            }]
    }
}