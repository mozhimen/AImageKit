package com.mozhimen.imagek.matisse.helpers.loader

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.mozhimen.imagek.matisse.commons.IAlbumLoadListener
import java.lang.ref.WeakReference

class AlbumLoadCursorLoaderCallbacks : LoaderManager.LoaderCallbacks<Cursor> {
    companion object {
        const val LOADER_ID = 1
        const val STATE_CURRENT_SELECTION = "state_current_selection"
    }

    //////////////////////////////////////////////////////////

    private var _contextRef: WeakReference<Context>? = null
    private var _loaderManager: LoaderManager? = null
    private var _albumLoadListener: IAlbumLoadListener? = null
    private var _currentSelection = 0
    private var _isLoadFinished = false

    //////////////////////////////////////////////////////////

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val context = _contextRef?.get()
        _isLoadFinished = false
        return AlbumLoadCursorLoader.newInstance(context!!)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (_contextRef?.get() == null || data == null) return

        if (!_isLoadFinished) {
            _isLoadFinished = true
            _albumLoadListener?.onAlbumLoad(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        if (_contextRef?.get() == null) return
        _albumLoadListener?.onAlbumReset()
    }

    //////////////////////////////////////////////////////////

    fun onCreate(activity: FragmentActivity, callbacks: IAlbumLoadListener) {
        _contextRef = WeakReference(activity)
        _loaderManager = LoaderManager.getInstance(activity)
        _albumLoadListener = callbacks
    }

    fun onRestoreInstanceState(saveInstanceState: Bundle) {
        _currentSelection = saveInstanceState.getInt(STATE_CURRENT_SELECTION)
    }

    fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(STATE_CURRENT_SELECTION, _currentSelection)
    }

    fun onDestroy() {
        _loaderManager?.destroyLoader(LOADER_ID)
        if (_albumLoadListener != null) _albumLoadListener = null
    }

    @Synchronized
    fun loadAlbums() {
        _isLoadFinished = false
        _loaderManager?.initLoader(LOADER_ID, null, this)
    }

    fun getCurrentSelection() = _currentSelection

    fun setStateCurrentSelection(currentSelection: Int) {
        _currentSelection = currentSelection
    }
}