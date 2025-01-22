package com.mozhimen.imagek.matisse.uis.activities

import android.database.Cursor
import com.mozhimen.imagek.matisse.bases.BasePreviewActivity
import com.mozhimen.imagek.matisse.mos.Album
import com.mozhimen.imagek.matisse.cons.CImageKMatisse
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.commons.IAlbumLoadListener
import com.mozhimen.imagek.matisse.helpers.loader.AlbumSelectionCursorLoaderCallbacks
import com.mozhimen.imagek.matisse.uis.adapters.MediaPreviewPagerAdapter

/**
 * Created by liubo on 2018/9/11.
 */
class AlbumPreviewActivity : BasePreviewActivity(), IAlbumLoadListener {

    private var _albumSelectionCursorLoaderCallbacks = AlbumSelectionCursorLoaderCallbacks()
    private var isAlreadySetPosition = false

    //////////////////////////////////////////////////////////

    override fun setViewData() {
        super.setViewData()
        _albumSelectionCursorLoaderCallbacks.onCreate(this, this)
        val album = intent.getParcelableExtra<Album>(CImageKMatisse.EXTRA_ALBUM) ?: return
        _albumSelectionCursorLoaderCallbacks.loadAlbum(album)
        val item = intent.getParcelableExtra<Media>(CImageKMatisse.EXTRA_ITEM)
        checkView?.apply {
            if (selection?.isCountable() == true) {
                setCheckedNum(mediaSelectionProxy.checkedNumOf(item))
            } else {
                setChecked(mediaSelectionProxy.isSelected(item))
            }
        }
        updateSize(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _albumSelectionCursorLoaderCallbacks.onDestroy()
    }

    override fun onAlbumLoad(cursor: Cursor) {
        val items = ArrayList<Media>()
        while (cursor.moveToNext()) {
            Media.valueOf(cursor)?.run { items.add(this) }
        }

        if (items.isEmpty()) return
        val adapter = previewViewPager?.adapter as MediaPreviewPagerAdapter
        adapter.addAll(items)
        adapter.notifyDataSetChanged()
        if (!isAlreadySetPosition) {
            isAlreadySetPosition = true
            val selected = intent.getParcelableExtra<Media>(CImageKMatisse.EXTRA_ITEM) ?: return
            val selectedIndex = items.indexOf(selected)
            previewViewPager?.setCurrentItem(selectedIndex, false)
            previousPos = selectedIndex
        }
    }

    override fun onAlbumReset() {
    }

    override fun onAlbumStart() {
    }
}