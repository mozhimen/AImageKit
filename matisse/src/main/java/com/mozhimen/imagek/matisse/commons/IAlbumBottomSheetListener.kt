package com.mozhimen.imagek.matisse.commons

import com.mozhimen.imagek.matisse.mos.Album
import com.mozhimen.imagek.matisse.uis.adapters.AlbumSelectionAdapter

/**
 * @ClassName IFolderBottomSheetListener
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/18
 * @Version 1.0
 */
interface IAlbumBottomSheetListener {
    fun onInitData(adapter: AlbumSelectionAdapter)

    /**
     * 点击回调
     * @param album 当前选中的相册
     * @param position 当前选中的位置
     */
    fun onItemClick(album: Album, position: Int)
}