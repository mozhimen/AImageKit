package com.mozhimen.imagek.matisse.helpers

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.collection.ArraySet
import com.mozhimen.imagek.matisse.cons.EMimeType
import com.mozhimen.imagek.matisse.utils.getRealFilePath
import java.util.*

/**
 * Describe : Define MediaType
 * Created by Leo on 2018/8/29 on 15:02.
 */
object MediaMimeTypeHelper {
    @JvmStatic
    fun ofAll(): EnumSet<EMimeType> =
        EnumSet.allOf(EMimeType::class.java)

    @JvmStatic
    fun of(first: EMimeType, others: Array<EMimeType>): EnumSet<EMimeType> =
        EnumSet.of(first, *others)

    @JvmStatic
    fun ofImage(): EnumSet<EMimeType> =
        EnumSet.of(EMimeType.JPEG, EMimeType.JPG, EMimeType.PNG, EMimeType.GIF, EMimeType.BMP, EMimeType.WEBP)

    // 静态图
    @JvmStatic
    fun ofMotionlessImage(): EnumSet<EMimeType> =
        EnumSet.of(EMimeType.JPEG, EMimeType.JPG, EMimeType.PNG, EMimeType.BMP)

    @JvmStatic
    fun ofVideo(): EnumSet<EMimeType> =
        EnumSet.of(EMimeType.MPEG, EMimeType.MP4, EMimeType.QUICKTIME, EMimeType.THREEGPP, EMimeType.THREEGPP2, EMimeType.MKV, EMimeType.WEBM, EMimeType.TS, EMimeType.AVI)

    ////////////////////////////////////////////////////////////////////////

    @JvmStatic
    fun isImage(mimeType: String?): Boolean =
        isMotionlessImage(mimeType)
                || EMimeType.GIF.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.WEBP.getKey().contains(lowerCaseMimeType(mimeType))

    @JvmStatic
    fun isVideo(mimeType: String): Boolean =
        EMimeType.MPEG.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.MP4.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.QUICKTIME.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.THREEGPP.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.THREEGPP2.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.MKV.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.WEBM.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.TS.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.AVI.getKey().contains(lowerCaseMimeType(mimeType))

    @JvmStatic
    fun isGif(mimeType: String) = EMimeType.GIF.getKey().contains(lowerCaseMimeType(mimeType))

    ////////////////////////////////////////////////////////////////////////

    @JvmStatic
    fun arraySetOf(vararg suffixes: String): ArraySet<String> =
        ArraySet(mutableListOf(*suffixes))

    @JvmStatic
    fun checkType(context: Context, uri: Uri?, mExtensions: Set<String>): Boolean {
        val map = MimeTypeMap.getSingleton()
        if (uri == null) return false

        val type = map.getExtensionFromMimeType(context.contentResolver.getType(uri))
        var path: String? = null
        // lazy load the path and prevent resolve for multiple times
        var pathParsed = false
        mExtensions.forEach {
            if (it == type) return true

            if (!pathParsed) {
                // we only resolve the path for one time
                path = getRealFilePath(context, uri)
                if (!TextUtils.isEmpty(path)) path = path?.toLowerCase(Locale.US)
                pathParsed = true
            }
            if (path != null && path?.endsWith(it) == true) return true
        }
        return false
    }

    ////////////////////////////////////////////////////////////////////////

    private fun isMotionlessImage(mimeType: String?): Boolean =
        EMimeType.JPEG.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.JPG.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.PNG.getKey().contains(lowerCaseMimeType(mimeType))
                || EMimeType.BMP.getKey().contains(lowerCaseMimeType(mimeType))

    private fun lowerCaseMimeType(mimeType: String?): String =
        mimeType?.toLowerCase() ?: ""
}