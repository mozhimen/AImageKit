package com.mozhimen.imagek.matisse.helpers

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.cons.CImageKMatisse.STATE_COLLECTION_TYPE
import com.mozhimen.imagek.matisse.cons.CImageKMatisse.STATE_SELECTION
import com.mozhimen.imagek.matisse.mos.IncapableCause
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.imagek.matisse.utils.PhotoMetadataUtils
import com.mozhimen.imagek.matisse.utils.getPath
import com.mozhimen.imagek.matisse.widgets.CheckView
import java.util.*
import kotlin.collections.ArrayList

class MediaSelectionProxy(private var _context: Context) {

    companion object {
        /**
         * Empty collection
         */
        const val COLLECTION_UNDEFINED = 0x00

        /**
         * Collection only with images
         */
        const val COLLECTION_IMAGE = 0x01

        /**
         * Collection only with videos
         */
        const val COLLECTION_VIDEO = 0x02

        /**
         * Collection with images and videos.
         */
        const val COLLECTION_MIXED = COLLECTION_IMAGE or COLLECTION_VIDEO
    }

    /////////////////////////////////////////////////////////////////

    private lateinit var _items: LinkedHashSet<Media>
    private var imageItems: LinkedHashSet<Media>? = null
    private var videoItems: LinkedHashSet<Media>? = null
    private var collectionType = COLLECTION_UNDEFINED
    private val spec: Selection = Selection.getInstance()

    /////////////////////////////////////////////////////////////////

    fun onCreate(bundle: Bundle?) {
        if (bundle == null) {
            _items = linkedSetOf()
        } else {
            val saved = bundle.getParcelableArrayList<Media>(STATE_SELECTION)
            _items = LinkedHashSet(saved!!)
            initImageOrVideoItems()
            collectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED)
        }
    }

    fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(STATE_SELECTION, ArrayList(_items))
        outState?.putInt(STATE_COLLECTION_TYPE, collectionType)
    }

    fun getDataWithBundle(): Bundle =
        Bundle().run {
            putParcelableArrayList(STATE_SELECTION, ArrayList(_items))
            putInt(STATE_COLLECTION_TYPE, collectionType)
            this
        }

    fun setDefaultSelection(uris: List<Media>) {
        _items.addAll(uris)
    }

    fun overwrite(items: ArrayList<Media>, collectionType: Int) {
        this.collectionType = if (items.size == 0) COLLECTION_UNDEFINED else collectionType

        this._items.clear()
        this._items.addAll(items)
    }

    fun asList() = ArrayList(_items)

    fun asListOfUri(): List<Uri> {
        val uris = arrayListOf<Uri>()
        for (item in _items) {
            uris.add(item.getContentUri())
        }
        return uris
    }

    fun asListOfString(): List<String> {
        val paths = ArrayList<String>()
        _items.forEach {
            val path = getPath(_context, it.getContentUri())
            if (path != null) paths.add(path)
        }

        return paths
    }

    fun isAcceptable(item: Media?): IncapableCause? {
        if (maxSelectableReached(item)) {
            val maxSelectable = currentMaxSelectable(item)
            val maxSelectableTips = currentMaxSelectableTips(item)

            val cause = try {
                _context.getString(maxSelectableTips, maxSelectable)
            } catch (e: Resources.NotFoundException) {
                _context.getString(maxSelectableTips, maxSelectable)
            } catch (e: NoClassDefFoundError) {
                _context.getString(maxSelectableTips, maxSelectable)
            }

            return IncapableCause(cause)
        } else if (typeConflict(item)) {
            return IncapableCause(_context.getString(R.string.error_type_conflict))
        }

        return PhotoMetadataUtils.isAcceptable(_context, item)
    }

    fun maxSelectableReached(item: Media?): Boolean {
        if (!spec.isMediaTypeExclusive()) {
            if (item?.isImage() == true) {
                return spec.imageMaxSelectable == imageItems?.size
            } else if (item?.isVideo() == true) {
                return spec.videoMaxSelectable == videoItems?.size
            }
        }
        return spec.mediaMaxSelectable == _items.size
    }

    fun getCollectionType() = collectionType

    fun isEmpty() = _items.isEmpty()

    fun isSelected(item: Media?) = _items.contains(item)

    fun count() = _items.size

    fun items() = _items.toList()

    /**
     * 注：
     * 此处取的是item在选中集合中的序号，
     * 所以不需区分混合选择或单独选择
     */
    fun checkedNumOf(item: Media?): Int {
        val index = ArrayList(_items).indexOf(item)
        return if (index == -1) CheckView.UNCHECKED else index + 1
    }

    fun add(item: Media?): Boolean {
        if (typeConflict(item)) {
            throw IllegalArgumentException("Can't select images and videos at the same time.")
        }
        if (item == null) return false

        val added = _items.add(item)
        addImageOrVideoItem(item)
        if (added) {
            when (collectionType) {
                COLLECTION_UNDEFINED -> {
                    if (item.isImage()) {
                        collectionType = COLLECTION_IMAGE
                    } else if (item.isVideo()) {
                        collectionType = COLLECTION_VIDEO
                    }
                }

                COLLECTION_IMAGE, COLLECTION_VIDEO -> {
                    if ((item.isImage() && collectionType == COLLECTION_VIDEO)
                        || item.isVideo() && collectionType == COLLECTION_IMAGE
                    ) {
                        collectionType = COLLECTION_MIXED
                    }
                }
            }
        }

        return added
    }

    fun remove(item: Media?): Boolean {
        if (item == null) return false
        val removed = _items.remove(item)
        removeImageOrVideoItem(item)
        if (removed) resetType()
        return removed
    }

    fun removeAll() {
        _items.clear()
        imageItems?.clear()
        videoItems?.clear()
        resetType()
    }

    /////////////////////////////////////////////////////////////////

    /**
     * 根据混合选择模式，初始化图片与视频集合
     */
    private fun initImageOrVideoItems() {
        if (spec.isMediaTypeExclusive()) return
        _items.forEach {
            addImageOrVideoItem(it)
        }
    }

    private fun resetType() {
        if (_items.size == 0) {
            collectionType = COLLECTION_UNDEFINED
        } else {
            if (collectionType == COLLECTION_MIXED) refineCollectionType()
        }
    }

    private fun currentMaxSelectableTips(item: Media?): Int {
        if (!spec.isMediaTypeExclusive()) {
            if (item?.isImage() == true) {
                return R.string.error_over_count_of_image
            } else if (item?.isVideo() == true) {
                return R.string.error_over_count_of_video
            }
        }

        return R.string.error_over_count
    }

    // depends
    private fun currentMaxSelectable(item: Media?): Int {
        if (!spec.isMediaTypeExclusive()) {
            if (item?.isImage() == true) {
                return spec.imageMaxSelectable
            } else if (item?.isVideo() == true) {
                return spec.videoMaxSelectable
            }
        }

        return spec.mediaMaxSelectable
    }

    /**
     * 根据item集合数据设置collectionType
     */
    private fun refineCollectionType() {
        val hasImage = imageItems != null && imageItems?.size ?: 0 > 0
        val hasVideo = videoItems != null && videoItems?.size ?: 0 > 0

        collectionType = if (hasImage && hasVideo) {
            COLLECTION_MIXED
        } else if (hasImage) {
            COLLECTION_IMAGE
        } else if (hasVideo) {
            COLLECTION_VIDEO
        } else {
            COLLECTION_UNDEFINED
        }
    }

    /**
     * Determine whether there will be conflict media types. A user can only select images and videos at the same time
     * while [Selection.mediaTypeExclusive] is set to false.
     */
    private fun typeConflict(item: Media?) =
        spec.isMediaTypeExclusive()
                && ((item?.isImage() == true && (collectionType == COLLECTION_VIDEO || collectionType == COLLECTION_MIXED))
                || (item?.isVideo() == true && (collectionType == COLLECTION_IMAGE || collectionType == COLLECTION_MIXED)))

    private fun addImageOrVideoItem(item: Media) {
        if (item.isImage()) {
            if (imageItems == null)
                imageItems = linkedSetOf()

            imageItems?.add(item)
        } else if (item.isVideo()) {
            if (videoItems == null)
                videoItems = linkedSetOf()

            videoItems?.add(item)
        }
    }

    private fun removeImageOrVideoItem(item: Media) {
        if (item.isImage()) {
            imageItems?.remove(item)
        } else if (item.isVideo()) {
            videoItems?.remove(item)
        }
    }
}