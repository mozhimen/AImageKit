package com.mozhimen.imagek.matisse.commons

import com.mozhimen.imagek.matisse.mos.Album
import com.mozhimen.imagek.matisse.mos.Media

/**
 * @ClassName IOnMediaClickListener
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/12/20 21:33
 * @Version 1.0
 */
interface IMediaClickListener {
    fun onMediaClick(album: Album?, item: Media, adapterPosition: Int)
}