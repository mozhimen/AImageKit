package com.mozhimen.imagek.matisse.mos

import android.content.pm.ActivityInfo
import androidx.annotation.StyleRes
import com.mozhimen.imagek.matisse.cons.EMimeType
import com.mozhimen.imagek.matisse.helpers.MediaMimeTypeHelper
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.commons.IImageEngine
import com.mozhimen.imagek.matisse.bases.BaseMediaFilter
import com.mozhimen.imagek.matisse.commons.INoticeEventListener
import com.mozhimen.imagek.matisse.commons.ILoadStatusBarListener
import com.mozhimen.imagek.matisse.commons.IMediaCheckedListener
import com.mozhimen.imagek.matisse.commons.IMediaSelectedListener
import java.io.File
import com.mozhimen.kotlin.elemk.android.provider.MediaStoreCaptureProxy.CaptureStrategy
import com.mozhimen.imagek.matisse.commons.ILoadToolBarListener

/**
 * Describe : Builder to get config values
 * Created by Leo on 2018/8/29 on 14:54.
 */
class Selection {
    companion object {
        fun getInstance() = INSTANCE.holder

        fun getCleanInstance(): Selection {
            val selectionSpec = getInstance()
            selectionSpec.reset()
            return selectionSpec
        }

        private object INSTANCE {
            val holder = Selection()
        }
    }

    ///////////////////////////////////////////////////////
    @StyleRes
    var themeRes = R.style.ImageKMatisse_Default
    var orientation = 0
    var countable = false
    var mimeTypeSet: Set<EMimeType>? = null
    var mediaTypeExclusive = false                      // 设置单种/多种媒体资源选择 默认支持多种
    var mediaMaxSelectable = 1
    var imageMaxSelectable = 0
    var videoMaxSelectable = 0
    var imageEngine: IImageEngine? = null
    var onNoticeEventListener: INoticeEventListener? = null// 库内提示具体回调
    var onLoadStatusBarListener: ILoadStatusBarListener? = null//
    var onLoadToolbarListener: ILoadToolBarListener? = null
    var lastChooseMediaIdsOrUris: ArrayList<String>? = null   // 上次选中的图片Id
    var hasInited = false                           // 是否初始化完成

    var mediaFilters: MutableList<BaseMediaFilter>? = null
    var mediaCaptureEnable = false
    var mediaCaptureStrategy: CaptureStrategy? = null
    var mediaSelectedListener: IMediaSelectedListener? = null
    var mediaCheckedListener: IMediaCheckedListener? = null

    var imageOriginalEnable = false
    var imageOriginalMaxSize = 0
    var imageThumbnailScale = 0.5f
    var imageCropEnable = false                              // 裁剪
    var imageCropFrameIsCircle = false                        // 裁剪框的形状
    var imageCropFrameCanDrag = true
    var imageCropFrameCanAutoSize = true
    var imageCropFrameRectVisible = true
    var imageCropCacheFolder: File? = null               // 裁剪后文件保存路径

    var gridSpanCount = 3
    var gridExpectedSize = 0

    ///////////////////////////////////////////////////////

    fun reset() {
        themeRes = R.style.ImageKMatisse_Default
        orientation = 0
        countable = false
        mimeTypeSet = null
        imageEngine = null
        onNoticeEventListener = null
        onLoadStatusBarListener = null
        onLoadToolbarListener = null
        lastChooseMediaIdsOrUris = null
        hasInited = true

        mediaTypeExclusive = false
        mediaMaxSelectable = 1
        imageMaxSelectable = 0
        videoMaxSelectable = 0
        mediaFilters = null
        mediaCaptureEnable = false
        mediaCaptureStrategy = null

        imageOriginalEnable = false// return original setting
        imageOriginalMaxSize = Integer.MAX_VALUE
        imageThumbnailScale = 0.5f
        imageCropEnable = false// crop
        imageCropFrameIsCircle = false
        imageCropFrameCanDrag = true
        imageCropFrameCanAutoSize = true
        imageCropFrameRectVisible = true
        imageCropCacheFolder = null

        gridSpanCount = 3
        gridExpectedSize = 0
    }

    // 是否可计数
    fun isCountable() = countable && !isSingleChoose()

    // 是否可单选
    fun isSingleChoose() =
        mediaMaxSelectable == 1 || (imageMaxSelectable == 1 && videoMaxSelectable == 1)

    // 是否可裁剪
    fun openCrop() = imageCropEnable && isSingleChoose()

    fun isSupportCrop(item: Media?) = item != null && item.isImage() && !item.isGif()

    // 是否单一资源选择方式
    fun isMediaTypeExclusive() =
        mediaTypeExclusive && (imageMaxSelectable + videoMaxSelectable == 0)

    fun onlyShowImages() =
        if (mimeTypeSet != null) MediaMimeTypeHelper.ofImage().containsAll(mimeTypeSet!!) else false

    fun onlyShowVideos() =
        if (mimeTypeSet != null) MediaMimeTypeHelper.ofVideo().containsAll(mimeTypeSet!!) else false

    fun singleSelectionModeEnabled() = !countable && isSingleChoose()

    fun needOrientationRestriction() = orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}