package com.mozhimen.imagek.matisse.helpers.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import com.mozhimen.kotlin.utilk.android.content.UtilKPackage
import com.mozhimen.imagek.matisse.mos.Album
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.kotlin.utilk.android.content.UtilKPackageManager

/**
 * Load images and videos into a single cursor.
 * Created by Leo on 2018/9/4 on 19:53.
 */
class AlbumSelectionCursorLoader(
    context: Context,
    selection: String,
    selectionArgs: Array<out String>,
    capture: Boolean
) : CursorLoader(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY) {

    companion object {
        private val QUERY_URI = MediaStore.Files.getContentUri("external")

        val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE, "duration"
        )

        private const val SELECTION_ALL = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) AND " + MediaStore.MediaColumns.SIZE + ">0")

        private val SELECTION_ALL_ARGS = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        // === params for album ALL && showSingleMediaType: true ===
        private const val SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE = (
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0")

        // === params for ordinary album && showSingleMediaType: false ===
        private const val SELECTION_ALBUM = (
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                        + " AND "
                        + " bucket_id=?"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0")

        private fun getSelectionAlbumArgs(albumId: String): Array<String> {
            return arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(), albumId
            )
        }

        // === params for ordinary album && showSingleMediaType: true ===
        private const val SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE = (
                MediaStore.Files.FileColumns.MEDIA_TYPE
                        + "=? AND bucket_id=? AND " + MediaStore.MediaColumns.SIZE + ">0")

        private const val ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"

        fun newInstance(context: Context, album: Album, capture: Boolean): CursorLoader {
            val selection: String
            val selectionArgs: Array<String>
            val enableCapture: Boolean

            if (album.isAll()) {
                when {
                    Selection.getInstance().onlyShowImages() -> {
                        selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
                        selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                    }
                    Selection.getInstance().onlyShowVideos() -> {
                        selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
                        selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
                    }
                    else -> {
                        selection = SELECTION_ALL
                        selectionArgs = SELECTION_ALL_ARGS
                    }
                }
                enableCapture = capture
            } else {
                when {
                    Selection.getInstance().onlyShowImages() -> {
                        selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
                        selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), album.getId())
                    }
                    Selection.getInstance().onlyShowVideos() -> {
                        selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
                        selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(), album.getId())
                    }
                    else -> {
                        selection = SELECTION_ALBUM
                        selectionArgs = getSelectionAlbumArgs(album.getId())
                    }
                }
                enableCapture = false
            }
            return AlbumSelectionCursorLoader(context, selection, selectionArgs, enableCapture)
        }
    }

    //////////////////////////////////////////////////////////

    private var enableCapture = false

    //////////////////////////////////////////////////////////

    init {
        enableCapture = capture
    }

    //////////////////////////////////////////////////////////

    override fun loadInBackground(): Cursor? {
        val result = super.loadInBackground()
        if (!enableCapture || !UtilKPackage.hasBackCamera()) {
            return result
        }
        val dummy = MatrixCursor(PROJECTION)
        dummy.addRow(arrayOf(Media.ITEM_ID_CAPTURE, Media.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0))
        return MergeCursor(arrayOf(dummy, result!!))
    }

    override fun onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}