package com.mozhimen.imagek.matisse.helpers

import android.os.Bundle
import com.mozhimen.imagek.matisse.commons.IAlbumLoadListener
import com.mozhimen.imagek.matisse.helpers.loader.AlbumLoadCursorLoaderCallbacks
import com.mozhimen.imagek.matisse.uis.activities.MatisseActivity

class AlbumLoadProxy(private var _matisseActivity: MatisseActivity, private var _albumLoadListener: IAlbumLoadListener) {

    private var _albumLoadCursorLoaderCallbacks: AlbumLoadCursorLoaderCallbacks? = null

    ///////////////////////////////////////////////////////////////

    init {
        _albumLoadCursorLoaderCallbacks = AlbumLoadCursorLoaderCallbacks()
        loadAlbumData()
    }

    ///////////////////////////////////////////////////////////////

    fun loadAlbumData() {
        _albumLoadCursorLoaderCallbacks?.apply {
            onCreate(_matisseActivity, _albumLoadListener)
            _matisseActivity.savedInstanceState?.apply {
                _albumLoadCursorLoaderCallbacks?.onRestoreInstanceState(this)
            }
            loadAlbums()
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        _albumLoadCursorLoaderCallbacks?.onSaveInstanceState(outState)
    }

    /**
     * 设置当前选中位置，用于数据回收后恢复
     */
    fun setStateCurrentSelection(position: Int) {
        _albumLoadCursorLoaderCallbacks?.setStateCurrentSelection(position)
    }

    fun onDestroy() {
        _albumLoadCursorLoaderCallbacks?.onDestroy()
    }
}