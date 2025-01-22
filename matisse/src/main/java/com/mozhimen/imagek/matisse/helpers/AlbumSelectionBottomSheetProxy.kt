package com.mozhimen.imagek.matisse.helpers

import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.basick.bases.BaseWakeBefDestroyLifecycleObserver
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.commons.IAlbumBottomSheetListener
import com.mozhimen.imagek.matisse.mos.Album
import com.mozhimen.imagek.matisse.uis.fragments.AlbumSelectionBottomSheetDialogFragment

@OApiInit_ByLazy
@OApiCall_BindLifecycle
class AlbumSelectionBottomSheetProxy(
    private var _fragmentActivity: FragmentActivity
) : BaseWakeBefDestroyLifecycleObserver() {
    companion object {
        const val TAG_FOLDER_BOTTOM_SHEET = "Folder"
    }

    //////////////////////////////////////////////////////////////

    private var _folderCursor: Cursor? = null
    private var _folderList: ArrayList<Album>? = null
    private var _albumSelectionBottomSheetDialogFragment: AlbumSelectionBottomSheetDialogFragment? = null
    private var _lastFolderCheckedPosition = 0

    //////////////////////////////////////////////////////////

    fun createFolderSheetDialog(listener: IAlbumBottomSheetListener) {
        _albumSelectionBottomSheetDialogFragment = AlbumSelectionBottomSheetDialogFragment.newInstance(_lastFolderCheckedPosition)
        _albumSelectionBottomSheetDialogFragment!!.show(_fragmentActivity.supportFragmentManager, TAG_FOLDER_BOTTOM_SHEET)
        _albumSelectionBottomSheetDialogFragment!!.folderBottomSheetListener = listener
    }

    fun readAlbumFromCursor(): ArrayList<Album>? {
        if ((_folderList?.size ?: 0) > 0) return _folderList
        if (_folderCursor == null) return null

        var allFolderCoverPath: Uri? = null
        var allFolderCount = 0L
        if (_folderList == null) {
            _folderList = arrayListOf()
        }

        _folderCursor?.moveToFirst()
        while (_folderCursor!!.moveToNext()) {
            val album = Album.valueOf(_folderCursor!!)
            if (_folderList?.size == 0) {
                allFolderCoverPath = album.getCoverPath()
            }
            _folderList?.add(album)
            allFolderCount += album.getCount()
        }
        _folderList?.add(
            0, Album(allFolderCoverPath, _fragmentActivity.getString(R.string.album_name_all), allFolderCount)
        )
        return _folderList
    }

    fun insetAlbumToFolder(capturePath: Uri) {
        readAlbumFromCursor()

        _folderList?.apply {
            // 全部相册需添加一张
            this[0].addCaptureCount()
            this[0].setCoverPath(capturePath)

            /**
             * 拍照后图片保存在Pictures目录下
             * Pictures为空时，需手动创建
             */
            // TODO 2019/10/28 Leo 查询相册下图片需指定id，无法手动生成
//            val listDCIM: List<Album>? =
//                filter { Environment.DIRECTORY_PICTURES == it.getDisplayName(context) }
//            if (listDCIM == null || listDCIM.isEmpty()) {
//                albumFolderList?.add(Album(Environment.DIRECTORY_PICTURES, 0))
//            }

            // Pictures目录手动添加一张图片
            filter { Environment.DIRECTORY_PICTURES == it.getDisplayName(_fragmentActivity) }.forEach {
                it.addCaptureCount()
                it.setCoverPath(capturePath)
            }
        }
    }

    /**
     * 记录上次选中位置
     * @return true=记录成功   false=记录失败
     */
    fun setLastFolderCheckedPosition(lastPosition: Int): Boolean {
        if (_lastFolderCheckedPosition == lastPosition) return false
        _lastFolderCheckedPosition = lastPosition
        return true
    }

    fun setAlbumFolderCursor(cursor: Cursor) {
        _folderCursor = cursor
        readAlbumFromCursor()
    }

    fun getAlbumFolderList() = _folderList

    fun clearFolderSheetDialog() {
        if (_albumSelectionBottomSheetDialogFragment != null && _albumSelectionBottomSheetDialogFragment?.albumSelectionAdapter != null) {
            _folderCursor = null
            _albumSelectionBottomSheetDialogFragment?.albumSelectionAdapter?.setListData(null)
        }
    }

    //////////////////////////////////////////////////////////////

    override fun onPause(owner: LifecycleOwner) {
        _albumSelectionBottomSheetDialogFragment = null
        super.onPause(owner)
    }
}