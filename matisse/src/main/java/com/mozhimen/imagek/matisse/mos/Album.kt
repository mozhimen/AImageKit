package com.mozhimen.imagek.matisse.mos

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.helpers.loader.AlbumLoadCursorLoader

class Album() : Parcelable {
    companion object CREATOR : Parcelable.Creator<Album> {

        const val ALBUM_ID_ALL = (-1).toString()
        const val ALBUM_NAME_ALL = "All"

        override fun createFromParcel(parcel: Parcel): Album {
            return Album(parcel)
        }

        override fun newArray(size: Int): Array<Album?> {
            return arrayOfNulls(size)
        }

        @SuppressLint("Range")
        fun valueOf(cursor: Cursor): Album = Album(
            cursor.getString(cursor.getColumnIndex(AlbumLoadCursorLoader.BUCKET_ID)),
            Uri.parse(cursor.getString(cursor.getColumnIndex(AlbumLoadCursorLoader.COLUMN_URI)) ?: ""),
            cursor.getString(cursor.getColumnIndex(AlbumLoadCursorLoader.BUCKET_DISPLAY_NAME)),
            cursor.getLong(cursor.getColumnIndex(AlbumLoadCursorLoader.COLUMN_COUNT))
        )
    }

    //////////////////////////////////////////////////////////////////////////

    private var id = ""
    private var coverUri: Uri? = null
    private var displayName = ""
    private var count: Long = 0
    private var isCheck = false

    //////////////////////////////////////////////////////////////////////////

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        coverUri = parcel.readParcelable(Uri::class.java.classLoader)
        displayName = parcel.readString() ?: ""
        count = parcel.readLong()
        isCheck = parcel.readByte() != 0.toByte()
    }

    constructor(mCoverUri: Uri?, mDisplayName: String, mCount: Long) : this("-1", mCoverUri, mDisplayName, mCount)

    constructor(mDisplayName: String, mCount: Long) : this(System.currentTimeMillis().toString(), mDisplayName, mCount, false)

    constructor(mId: String, mCoverUri: Uri?, mDisplayName: String, mCount: Long) : this() {
        this.id = mId
        this.coverUri = mCoverUri
        this.displayName = mDisplayName
        this.count = mCount
        this.isCheck = false
    }

    constructor(mId: String, mDisplayName: String, mCount: Long, mIsCheck: Boolean = false) : this() {
        this.id = mId
        this.displayName = mDisplayName
        this.count = mCount
        this.isCheck = mIsCheck
    }

    //////////////////////////////////////////////////////////////////////////

    fun getId() = id

    fun getCoverPath() = coverUri

    fun setCoverPath(path: Uri?) {
        path?.apply { coverUri = this }
    }

    fun getCount() = count

    fun addCaptureCount() {
        count++
    }

    fun getDisplayName(context: Context): String {
        return if (isAll()) {
            context.getString(R.string.album_name_all)
        } else displayName
    }

    fun isAll() = ALBUM_ID_ALL == id

    fun isEmpty() = count == 0L

    fun isChecked() = isCheck

    //////////////////////////////////////////////////////////////////////////

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(coverUri, 0)
        parcel.writeString(displayName)
        parcel.writeLong(count)
        parcel.writeByte(if (isCheck) 1 else 0)
    }

    override fun describeContents() = 0

    override fun toString(): String {
        return "Album(id='$id', coverUri=$coverUri, displayName='$displayName', count=$count, isCheck=$isCheck)"
    }
}