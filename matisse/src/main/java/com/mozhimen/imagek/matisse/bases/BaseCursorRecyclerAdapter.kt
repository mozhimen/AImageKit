package com.mozhimen.imagek.matisse.bases

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.MediaStore
import androidx.recyclerview.widget.RecyclerView

abstract class BaseCursorRecyclerAdapter<VH : RecyclerView.ViewHolder>(cursor: Cursor?) : RecyclerView.Adapter<VH>() {

    private var _cursor: Cursor? = null
    private var _rowIDColumn = 0

    init {
        setHasStableIds(true)
        swapCursor(cursor)
    }

    abstract fun onBindViewHolder(holder: VH, cursor: Cursor, position: Int)

    override fun onBindViewHolder(holder: VH, position: Int) {
        check(isDataValid(_cursor)) {
            "Cannot bind view holder when cursor is in invalid state."
        }

        check(_cursor?.moveToPosition(position)!!) {
            "Could not move cursor to position $position when trying to bind view holder"
        }

        onBindViewHolder(holder, _cursor!!, position)
    }

    override fun getItemViewType(position: Int): Int {
        check(_cursor?.moveToPosition(position)!!) {
            "Could not move cursor to position $position when trying to get item view type."
        }
        return getItemViewType(position, _cursor!!)
    }

    abstract fun getItemViewType(position: Int, cursor: Cursor): Int

    override fun getItemCount(): Int =
        if (isDataValid(_cursor))
            _cursor?.count!!
        else
            0

    override fun getItemId(position: Int): Long {
        check(isDataValid(_cursor)) { "Cannot lookup item id when cursor is in invalid state." }
        check(_cursor?.moveToPosition(position)!!) {
            "Could not move cursor to position $position when trying to get an item id"
        }

        return _cursor?.getLong(_rowIDColumn) ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapCursor(newCursor: Cursor?) {
        if (newCursor == _cursor) return

        if (newCursor == null) {
            notifyItemRangeRemoved(0, itemCount)
            _cursor = null
            _rowIDColumn = -1
        } else {
            _cursor = newCursor
            _rowIDColumn = _cursor?.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID) ?: 0
            // notify the observers about the new cursor
            notifyDataSetChanged()
        }
    }

    private fun isDataValid(cursor: Cursor?): Boolean =
        cursor != null && !cursor.isClosed

    fun getCursor() = _cursor
}