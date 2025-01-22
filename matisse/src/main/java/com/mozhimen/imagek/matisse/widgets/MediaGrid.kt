package com.mozhimen.imagek.matisse.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.imagek.matisse.utils.setViewVisible

class MediaGrid : SquareFrameLayout, View.OnClickListener {

    private lateinit var media: Media
    private lateinit var preBindInfo: PreBindInfo
    lateinit var listener: IOnMediaGridClickListener
    private var media_thumbnail: ImageView
    private var check_view: CheckView
    private var gif: ImageView
    private var video_duration: TextView

    /////////////////////////////////////////////////////////////////

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.merge_media_grid, this, true)
        media_thumbnail = findViewById(R.id.media_thumbnail)
        check_view = findViewById(R.id.check_view)
        video_duration = findViewById(R.id.video_duration)
        gif = findViewById(R.id.gif)
        media_thumbnail.setOnClickListener(this)
        check_view.setOnClickListener(this)
    }

    /////////////////////////////////////////////////////////////////

    override fun onClick(v: View?) {
        when (v) {
            media_thumbnail -> listener.onThumbnailClicked(
                media_thumbnail, media, preBindInfo.viewHolder
            )
            check_view -> listener.onCheckViewClicked(check_view, media, preBindInfo.viewHolder)
        }
    }

    /////////////////////////////////////////////////////////////////

    interface IOnMediaGridClickListener {
        fun onThumbnailClicked(thumbnail: ImageView, item: Media, holder: RecyclerView.ViewHolder)
        fun onCheckViewClicked(checkView: CheckView, item: Media, holder: RecyclerView.ViewHolder)
    }

    /////////////////////////////////////////////////////////////////

    class PreBindInfo(
        var resize: Int, var placeholder: Drawable?,
        var checkViewCountable: Boolean, var viewHolder: RecyclerView.ViewHolder
    )

    fun preBindMedia(info: PreBindInfo) {
        preBindInfo = info
    }

    fun bindMedia(item: Media) {
        media = item
        setGifTag()
        initCheckView()
        setImage()
        setVideoDuration()
    }

    fun setCheckedNum(checkedNum: Int) {
        check_view.setCheckedNum(checkedNum)
    }

    fun setChecked(checked: Boolean) {
        check_view.setChecked(checked)
    }

    /////////////////////////////////////////////////////////////////

    private fun setGifTag() {
        setViewVisible(media.isGif(), gif)
    }

    private fun initCheckView() {
        check_view.setCountable(preBindInfo.checkViewCountable)
    }

    private fun setImage() {
        if (media.isGif()) {
            Selection.getInstance().imageEngine?.loadGifThumbnail(
                context, preBindInfo.resize, preBindInfo.placeholder,
                media_thumbnail, media.getContentUri()
            )
        } else {
            Selection.getInstance().imageEngine?.loadThumbnail(
                context, preBindInfo.resize, preBindInfo.placeholder,
                media_thumbnail, media.getContentUri()
            )
        }
    }

    private fun setVideoDuration() {
        if (media.isVideo()) {
            setViewVisible(true, video_duration)
            video_duration.text = DateUtils.formatElapsedTime(media.duration / 1000)
        } else {
            setViewVisible(false, video_duration)
        }
    }
}