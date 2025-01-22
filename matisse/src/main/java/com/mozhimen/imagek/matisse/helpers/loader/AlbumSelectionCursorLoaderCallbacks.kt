package com.mozhimen.imagek.matisse.helpers.loader

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.mozhimen.imagek.matisse.commons.IAlbumLoadListener
import com.mozhimen.imagek.matisse.mos.Album
import java.lang.ref.WeakReference

class AlbumSelectionCursorLoaderCallbacks : LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
//        const val LOADER_ID = 2

        const val ARGS_ALBUM = "args_album"
        const val ARGS_ENABLE_CAPTURE = "args_enable_capture"
    }

    //////////////////////////////////////////////////////////

    private var _loaderId = 2
    private var _contextRef: WeakReference<Context>? = null
    private var _loaderManager: LoaderManager? = null
    private var _albumLoadListener: IAlbumLoadListener? = null

    //////////////////////////////////////////////////////////

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val content = _contextRef?.get()
        val album = args?.getParcelable<Album>(ARGS_ALBUM)
        return AlbumSelectionCursorLoader.newInstance(content!!, album!!, album.isAll() && args.getBoolean(ARGS_ENABLE_CAPTURE, false))
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (_contextRef?.get() == null) return
        _albumLoadListener?.onAlbumLoad(data!!)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        if (_contextRef?.get() == null) return
        _albumLoadListener?.onAlbumReset()
    }

    //////////////////////////////////////////////////////////

    fun onCreate(context: FragmentActivity, listener: IAlbumLoadListener) {
        _contextRef = WeakReference(context)
        _loaderManager = LoaderManager.getInstance(context)
        _albumLoadListener = listener
    }

    fun onDestroy() {
        _loaderManager?.destroyLoader(_loaderId)
        if (_albumLoadListener != null)
            _albumLoadListener = null
    }

    fun loadAlbum(album: Album) {
        loadAlbum(album, false)
    }

    fun loadAlbum(album: Album, enableCapture: Boolean) {
        val args = Bundle().apply {
            putParcelable(ARGS_ALBUM, album)
            putBoolean(ARGS_ENABLE_CAPTURE, enableCapture)
        }
        _loaderManager?.initLoader(album.getId().toInt().also { _loaderId = it }, args, this)
    }
}