package com.mozhimen.imagek.matisse

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.*
import androidx.annotation.StyleRes
import com.mozhimen.kotlin.elemk.android.provider.MediaStoreCaptureProxy
import com.mozhimen.imagek.matisse.annors.AScreenOrientation
import com.mozhimen.imagek.matisse.commons.IImageEngine
import com.mozhimen.imagek.matisse.bases.BaseMediaFilter
import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.imagek.matisse.commons.IMediaCheckedListener
import com.mozhimen.imagek.matisse.commons.IMediaSelectedListener
import com.mozhimen.imagek.matisse.cons.EMimeType
import com.mozhimen.imagek.matisse.commons.ILoadStatusBarListener
import com.mozhimen.imagek.matisse.commons.ILoadToolBarListener
import com.mozhimen.imagek.matisse.commons.INoticeEventListener
import com.mozhimen.imagek.matisse.uis.activities.MatisseActivity
import java.io.File

/**
 * Fluent API for building media select specification.
 * Constructs a new specification builder on the context.
 *
 * @param _imageKMatisse   a requester context wrapper.
 * @param mimeTypes MIME type set to select.
 */
class ImageKMatisseSelectionBuilder(
    private val _imageKMatisse: ImageKMatisse,
    mimeTypes: Set<EMimeType>,
    mediaTypeExclusive: Boolean
) {
    private val _selection: Selection = Selection.getCleanInstance()

    /////////////////////////////////////////////////////////////////////////////////

    init {
        _selection.run {
            this.mimeTypeSet = mimeTypes
            this.mediaTypeExclusive = mediaTypeExclusive
            this.orientation = SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Theme for media selecting Activity.
     *  外部设置主题样式
     * There are two built-in themes:
     * you can define a custom theme derived from the above ones or other themes.
     *
     * @param themeRes theme resource id. Default value is R.style.Matisse_Zhihu.
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setThemeRes(@StyleRes themeRes: Int): ImageKMatisseSelectionBuilder =
        this.apply { _selection.themeRes = themeRes }

    /**
     * Set the desired orientation of this activity.
     * 设置此活动所需的方向。
     * 强制屏幕方向
     * @param orientation An orientation constant as used in [AScreenOrientation].
     * Default value is [android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT].
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     * @see Activity.setRequestedOrientation
     */
    fun setOrientation(@AScreenOrientation orientation: Int): ImageKMatisseSelectionBuilder =
        this.apply { _selection.orientation = orientation }

    /**
     * Show a auto-increased number or a check mark when user select media.
     * 设置选中计数方式
     * @param countable true for a auto-increased number from 1, false for a check mark. Default
     * 对于从1开始自动增加的数字为True，对于复选标记为false。默认的
     * value is false.
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setCountable(countable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.countable = countable }

    /**
     * 单一选择下
     * Maximum selectable count.
     * mediaTypeExclusive true
     *      use maxSelectable
     * mediaTypeExclusive false
     *      use maxImageSelectable and maxVideoSelectable
     * @param maxSelectable Maximum selectable count. Default value is 1.
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setMaxSelectable(maxSelectable: Int): ImageKMatisseSelectionBuilder =
        this.apply {
            if (!_selection.mediaTypeExclusive) return this
            require(maxSelectable >= 1) { "maxSelectable must be greater than or equal to one" }
            check(!(_selection.imageMaxSelectable > 0 || _selection.videoMaxSelectable > 0)) {
                "already set maxImageSelectable and maxVideoSelectable"
            }
            _selection.mediaMaxSelectable = maxSelectable
        }

    /**
     * Only useful when [Selection.mediaTypeExclusive] set true and you want to set different maximum
     * selectable files for image and video media types.
     *
     * @param maxImageSelectable Maximum selectable count for image.
     * @param maxVideoSelectable Maximum selectable count for video.
     * @return
     */
    fun setMaxSelectablePerMediaType(maxImageSelectable: Int, maxVideoSelectable: Int): ImageKMatisseSelectionBuilder =
        this.apply {
            if (_selection.mediaTypeExclusive) return this
            require(!(maxImageSelectable < 1 || maxVideoSelectable < 1)) {
                "mediaTypeExclusive must be false and max selectable must be greater than or equal to one"
            }
            _selection.mediaMaxSelectable = -1
            _selection.imageMaxSelectable = maxImageSelectable
            _selection.videoMaxSelectable = maxVideoSelectable
        }

    /**
     * Provide an image engine.
     * There are two built-in image engines:
     * And you can implement your own image engine.
     * 提供一个图像引擎。有两个内置的图像引擎:你可以实现你自己的图像引擎。
     * @param imageEngine [IImageEngine]
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setImageEngine(imageEngine: IImageEngine): ImageKMatisseSelectionBuilder =
        this.apply {
            _selection.imageEngine = imageEngine
            _selection.imageEngine?.init(_imageKMatisse.activity?.applicationContext!!)
        }

    /**
     * set notice type for matisse
     */
    fun setOnNoticeEventListener(listener: INoticeEventListener?): ImageKMatisseSelectionBuilder =
        this.apply { _selection.onNoticeEventListener = listener }

    /**
     * set Status Bar
     */
    fun setOnLoadStatusBarListener(listener: ILoadStatusBarListener?): ImageKMatisseSelectionBuilder =
        this.apply { _selection.onLoadStatusBarListener = listener }

    /**
     * set ToolBar
     */
    fun setOnLoadToolbarListener(listener: ILoadToolBarListener?): ImageKMatisseSelectionBuilder =
        this.apply { _selection.onLoadToolbarListener = listener }

    /**
     * set last choose pictures ids
     * id is cursor id. not support crop picture
     * 预选中上次带回的图片
     * 注：暂时无法保持预选中图片的顺序
     */
    fun setLastChooseMediaIdsOrUris(list: ArrayList<String>?): ImageKMatisseSelectionBuilder =
        this.apply { _selection.lastChooseMediaIdsOrUris = list }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Add filter to filter each selecting item.
     *
     * @param mediaFilter [BaseMediaFilter]
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun addMediaFilter(mediaFilter: BaseMediaFilter): ImageKMatisseSelectionBuilder =
        this.apply {
            if (_selection.mediaFilters == null) _selection.mediaFilters = mutableListOf()
            _selection.mediaFilters?.add(mediaFilter)
        }

    /**
     * Determines whether the photo capturing is enabled or not on the media grid view.
     * If this value is set true, photo capturing entry will appear only on All Media's page.
     * 是否开启内部拍摄
     * @param enable Whether to enable capturing or not. Default value is false;
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setMediaCaptureEnable(enable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.mediaCaptureEnable = enable }

    /**
     * 为保存照片的位置提供捕获策略，包括内部和外部存储，以及[androidx.core.content.FileProvider]的权限。
     * Capture strategy provided for the location to save photos including internal and external
     * storage and also a authority for [androidx.core.content.FileProvider].
     * 拍照设置Strategy
     * @param captureStrategy [CaptureStrategy], needed only when capturing is enabled.
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setMediaCaptureStrategy(captureStrategy: MediaStoreCaptureProxy.CaptureStrategy): ImageKMatisseSelectionBuilder =
        this.apply { _selection.mediaCaptureStrategy = captureStrategy }

    /**
     * Set listener for callback immediately when user select or unselect something.
     *
     * It's a redundant API with [ImageKMatisse.obtainResult],
     * we only suggest you to use this API when you need to do something immediately.
     *
     * @param listener [IMediaSelectedListener]
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setMediaSelectedListener(listener: IMediaSelectedListener?): ImageKMatisseSelectionBuilder =
        this.apply { _selection.mediaSelectedListener = listener }

    /**
     * Set listener for callback immediately when user check or uncheck original.
     *
     * @param listener [IMediaSelectedListener]
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setMediaCheckedListener(listener: IMediaCheckedListener?): ImageKMatisseSelectionBuilder =
        this.apply { _selection.mediaCheckedListener = listener }

    /**
     * Show a original photo check options.Let users decide whether use original photo after select
     * 显示原始照片检查选项。让用户选择后决定是否使用原图
     * @param enable Whether to enable original photo or not
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setImageOriginalEnable(enable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageOriginalEnable = enable }

    /**
     * Maximum original size,the unit is MB. Only useful when {link@originalEnable} set true
     * 最大原始大小，单位为MB。仅当flink@originalEnable)设置为true时有效
     * @param size Maximum original size. Default value is Integer.MAX_VALUE
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setImageOriginalMaxSize(size: Int): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageOriginalMaxSize = size }

    /**
     * Photo thumbnail's scale compared to the View's size. It should be a float value in (0.0,1.0].
     * 图片显示压缩比
     * @param scale Thumbnail's scale in (0.0, 1.0]. Default value is 0.5.
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setImageThumbnailScale(scale: Float): ImageKMatisseSelectionBuilder =
        this.apply {
            require(!(scale <= 0f || scale > 1f)) { "Thumbnail scale must be between (0.0, 1.0]" }
            _selection.imageThumbnailScale = scale
        }

    /**
     * 设置开启裁剪
     * Whether to support crop
     * If this value is set true, it will support function crop.
     * @param enable Whether to support crop or not. Default value is false;
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setImageCropEnable(enable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageCropEnable = enable }

    /**
     * isCircleCrop
     * default is RECTANGLE CROP
     */
    fun setImageCropFrameIsCircle(enable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageCropFrameIsCircle = enable }

    fun setImageCropFrameCanDrag(enable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageCropFrameCanDrag = enable }

    fun setImageCropFrameCanAutoSize(enable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageCropFrameCanAutoSize = enable }

    fun setImageCropFrameRectVisible(enable: Boolean): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageCropFrameRectVisible = enable }

    /**
     * provide file to save image after crop
     */
    fun setImageCropCacheFolder(cropCacheFolder: File): ImageKMatisseSelectionBuilder =
        this.apply { _selection.imageCropCacheFolder = cropCacheFolder }

    ///////////////////////////////////////////////////////

    /**
     * Set a fixed span count for the media grid. Same for different screen orientations.
     * This will be ignored when [.gridExpectedSize] is set.
     * [get gridExpectedSize first]
     * 为媒体网格设置一个固定的跨度计数。对于不同的屏幕方向也是一样。
     * @param spanCount Requested span count.
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setGridSpanCount(spanCount: Int): ImageKMatisseSelectionBuilder =
        this.apply {
            if (_selection.gridExpectedSize > 0) return this
            _selection.gridSpanCount = spanCount
        }

    /**
     * Set expected size for media grid to adapt to different screen sizes. This won't necessarily
     * be applied cause the media grid should fill the view container. The measured media grid's
     * size will be as close to this value as possible.
     * 设置媒体网格的预期大小，以适应不同的屏幕尺寸。这并不一定会被应用，因为媒体网格应该填充视图容器。被测量的媒体网格的大小将尽可能接近这个值。
     *
     * @param sizePx Expected media grid size in pixel.
     * @return [ImageKMatisseSelectionBuilder] for fluent API.
     */
    fun setGridExpectedSize(sizePx: Int): ImageKMatisseSelectionBuilder =
        this.apply { _selection.gridExpectedSize = sizePx }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    fun forResult(requestCode: Int) {
        val activity = _imageKMatisse.activity ?: return

        val intent = Intent(activity, MatisseActivity::class.java)

        val fragment = _imageKMatisse.fragment
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode)
        } else {
            activity.startActivityForResult(intent, requestCode)
        }
    }
}
