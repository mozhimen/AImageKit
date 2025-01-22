package com.mozhimen.imagek.matisse.uis.adapters

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.bases.BaseCursorRecyclerAdapter
import com.mozhimen.imagek.matisse.commons.IMediaCheckSelectSateListener
import com.mozhimen.imagek.matisse.commons.IMediaClickListener
import com.mozhimen.imagek.matisse.commons.IMediaPhotoCapture
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.imagek.matisse.helpers.MediaSelectionProxy
import com.mozhimen.imagek.matisse.utils.handleCause
import com.mozhimen.imagek.matisse.utils.setTextDrawable
import com.mozhimen.imagek.matisse.widgets.CheckView
import com.mozhimen.imagek.matisse.widgets.MediaGrid

class MediaSelectionAdapter(
    private var _context: Context,
    private var _mediaSelectionProxy: MediaSelectionProxy,
    private var _recyclerView: RecyclerView
) : BaseCursorRecyclerAdapter<RecyclerView.ViewHolder>(null),
    MediaGrid.IOnMediaGridClickListener {

    companion object {
        const val VIEW_TYPE_CAPTURE = 0X01
        const val VIEW_TYPE_MEDIA = 0X02
    }

    ////////////////////////////////////////////////////////////////////////////

    private var _drawablePlaceholder: Drawable? = null
    private var _selection: Selection = Selection.getInstance()
    private var _imageResize = 0
    private var _layoutInflater: LayoutInflater

    var onMediaCheckSelectSateListener: IMediaCheckSelectSateListener? = null
    var onMediaClickListener: IMediaClickListener? = null

    ////////////////////////////////////////////////////////////////////////////

    init {
        val typedArray = _context.theme.obtainStyledAttributes(intArrayOf(R.attr.ItemImage_ResPlaceholder))
        _drawablePlaceholder = typedArray.getDrawable(0)
        typedArray.recycle()

        _layoutInflater = LayoutInflater.from(_context)
    }

    ////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CAPTURE -> {
                val v = _layoutInflater.inflate(R.layout.item_media_capture_photo, parent, false)
                CaptureViewHolder(v).run {
                    itemView.setOnClickListener {
                        if (it.context is IMediaPhotoCapture)
                            (it.context as IMediaPhotoCapture).onCapture()
                    }
                    this
                }
            }

            else -> {
                val v = _layoutInflater.inflate(R.layout.item_media_grid, parent, false)
                MediaViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, cursor: Cursor, position: Int) {
        holder.apply {
            when (this) {
                is CaptureViewHolder ->
                    setTextDrawable(itemView.context, hint, R.attr.ItemPhoto_TextColor)

                is MediaViewHolder -> {
                    val item = Media.valueOf(cursor, position)
                    mediaGrid.preBindMedia(
                        MediaGrid.PreBindInfo(
                            getImageResize(mediaGrid.context), _drawablePlaceholder,
                            _selection.isCountable(), holder
                        )
                    )
                    item?.let {
                        mediaGrid.bindMedia(it)
                        mediaGrid.listener = this@MediaSelectionAdapter
                        setCheckStatus(it, mediaGrid)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int, cursor: Cursor) =
        if (Media.valueOf(cursor)?.isCapture() == true) VIEW_TYPE_CAPTURE else VIEW_TYPE_MEDIA

    override fun onThumbnailClicked(thumbnail: ImageView, item: Media, holder: RecyclerView.ViewHolder) {
        onMediaClickListener?.onMediaClick(null, item, holder.adapterPosition)
    }

    /**
     * 单选：
     *     a.选中：刷新当前项与上次选择项
     *     b.取消选中：刷新当前项与上次选择项
     *
     * 多选：
     *      1. 按序号计数
     *          a.选中：仅刷新选中的item
     *          b.取消选中：
     *              取消最后一位：仅刷新当前操作的item
     *              取消非最后一位：刷新所有选中的item
     *      2. 无序号计数
     *          a.选中：仅刷新选中的item
     *          b.取消选中：仅刷新选中的item
     */
    override fun onCheckViewClicked(checkView: CheckView, item: Media, holder: RecyclerView.ViewHolder) {
        if (_selection.isSingleChoose()) {
            notifySingleChooseData(item)
        } else {
            notifyMultiChooseData(item)
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private fun getImageResize(context: Context): Int {
        if (_imageResize != 0) return _imageResize

        val layoutManager = _recyclerView.layoutManager as GridLayoutManager
        val spanCount = layoutManager.spanCount
        val screenWidth = context.resources.displayMetrics.widthPixels
        val availableWidth = screenWidth - context.resources.getDimensionPixelSize(
            R.dimen.spacing_media_grid
        ) * (spanCount - 1)

        _imageResize = availableWidth / spanCount
        _imageResize = (_imageResize * _selection.imageThumbnailScale).toInt()
        return _imageResize
    }

    /**
     * 初始化选择框选中状态
     */
    private fun setCheckStatus(item: Media, mediaGrid: MediaGrid) {
        // 初始化时 添加上次选中的图片
        setLastChooseItems(item)
        if (_selection.isCountable()) {
            val checkedNum = _mediaSelectionProxy.checkedNumOf(item)

            if (checkedNum > 0) {
                mediaGrid.setCheckedNum(checkedNum)
            } else {
                mediaGrid.setCheckedNum(
                    if (_mediaSelectionProxy.maxSelectableReached(item)) CheckView.UNCHECKED else checkedNum
                )
            }
        } else {
            mediaGrid.setChecked(_mediaSelectionProxy.isSelected(item))
        }
    }

    /**
     * 单选刷新数据
     */
    private fun notifySingleChooseData(item: Media) {
        if (_mediaSelectionProxy.isSelected(item)) {
            _mediaSelectionProxy.remove(item)
            notifyItemChanged(item.positionInList)
        } else {
            notifyLastItem()
            if (!addItem(item)) return
            notifyItemChanged(item.positionInList)
        }
        notifyCheckStateChanged()
    }

    private fun notifyLastItem() {
        val itemLists = _mediaSelectionProxy.asList()
        if (itemLists.size > 0) {
            _mediaSelectionProxy.remove(itemLists[0])
            notifyItemChanged(itemLists[0].positionInList)
        }
    }

    /**
     * 多选刷新数据
     *      1. 按序号计数
     *          a.选中：仅刷新选中的item
     *          b.取消选中：
     *              取消最后一位：仅刷新当前操作的item
     *              取消非最后一位：刷新所有选中的item
     *      2. 无序号计数
     *          a.选中：仅刷新选中的item
     *          b.取消选中：仅刷新选中的item
     */
    private fun notifyMultiChooseData(item: Media) {
        if (_selection.isCountable()) {
            if (notifyMultiCountableItem(item)) return
        } else {
            if (_mediaSelectionProxy.isSelected(item)) {
                _mediaSelectionProxy.remove(item)
            } else {
                if (!addItem(item)) return
            }

            notifyItemChanged(item.positionInList)
        }

        notifyCheckStateChanged()
    }

    /**
     * @return 是否拦截 true=拦截  false=不拦截
     */
    private fun notifyMultiCountableItem(item: Media): Boolean {
        val checkedNum = _mediaSelectionProxy.checkedNumOf(item)
        if (checkedNum == CheckView.UNCHECKED) {
            if (!addItem(item)) return true
            notifyItemChanged(item.positionInList)
        } else {
            _mediaSelectionProxy.remove(item)
            // 取消选中中间序号时，刷新所有选中item
            if (checkedNum != _mediaSelectionProxy.count() + 1) {
                _mediaSelectionProxy.asList().forEach {
                    notifyItemChanged(it.positionInList)
                }
            }
            notifyItemChanged(item.positionInList)
        }
        return false
    }

    private fun notifyCheckStateChanged() {
        onMediaCheckSelectSateListener?.onMediaSelectUpdate()
    }

    private fun addItem(item: Media): Boolean {
        if (!assertAddSelection(_context, item)) return false
        _mediaSelectionProxy.add(item)
        return true
    }

    private fun assertAddSelection(context: Context, item: Media): Boolean {
        val cause = _mediaSelectionProxy.isAcceptable(item)
        handleCause(context, cause)
        return cause == null
    }

    /**
     * 初始化外部传入上次选中的图片
     */
    private fun setLastChooseItems(item: Media) {
        if (_selection.lastChooseMediaIdsOrUris == null) return

        _selection.lastChooseMediaIdsOrUris?.forEachIndexed { index, s ->
            if (s == item.id.toString() || s == item.getContentUri().toString()) {
                _mediaSelectionProxy.add(item)
                _selection.lastChooseMediaIdsOrUris!![index] = ""
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mediaGrid: MediaGrid = itemView as MediaGrid
    }

    class CaptureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var hint: TextView = itemView.findViewById(R.id.hint)
    }
}