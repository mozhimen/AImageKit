package com.mozhimen.imagek.matisse.commons

import android.database.Cursor

interface IAlbumLoadListener {
    fun onAlbumStart()
    fun onAlbumLoad(cursor: Cursor)
    fun onAlbumReset()
}