package com.mozhimen.imagek.matisse.uis.activities

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mozhimen.imagek.matisse.ImageKMatisse
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.annors.AFormType
import com.mozhimen.imagek.matisse.mos.Album
import com.mozhimen.imagek.matisse.cons.CImageKMatisse
import com.mozhimen.imagek.matisse.mos.IncapableCause
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.commons.IAlbumLoadListener
import com.mozhimen.imagek.matisse.helpers.MediaSelectionProxy
import com.mozhimen.imagek.ucrop.UCrop
import com.mozhimen.imagek.matisse.bases.BaseActivity
import com.mozhimen.imagek.matisse.commons.IAlbumBottomSheetListener
import com.mozhimen.imagek.matisse.helpers.AlbumSelectionBottomSheetProxy
import com.mozhimen.imagek.matisse.helpers.AlbumLoadProxy
import com.mozhimen.imagek.matisse.uis.adapters.AlbumSelectionAdapter
import com.mozhimen.imagek.matisse.uis.fragments.AlbumSelectionFragment
import com.mozhimen.imagek.matisse.widgets.CheckRadioView
import com.mozhimen.kotlin.elemk.android.provider.MediaStoreCaptureProxy
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_QUERY_ALL_PACKAGES
import com.mozhimen.kotlin.utilk.kotlin.UtilKLazyJVM.lazy_ofNone
import com.mozhimen.imagek.matisse.commons.IMediaCheckSelectSateListener
import com.mozhimen.imagek.matisse.commons.IMediaClickListener
import com.mozhimen.imagek.matisse.commons.IMediaPhotoCapture
import com.mozhimen.imagek.matisse.commons.IMediaSelectionProxyProvider
import com.mozhimen.imagek.matisse.utils.countOverMaxSize
import com.mozhimen.imagek.matisse.utils.finishIntentFromCrop
import com.mozhimen.imagek.matisse.utils.gotoImageCrop
import com.mozhimen.imagek.matisse.utils.handleIntentFromPreview
import com.mozhimen.imagek.matisse.utils.handlePreviewIntent
import com.mozhimen.imagek.matisse.utils.setOnClickListener
import com.mozhimen.imagek.matisse.utils.setViewVisible

/**
 * desc：入口</br>
 * time: 2019/9/11-14:17</br>
 * author：Leo </br>
 * since V 1.0.0 </br>
 */
class MatisseActivity : BaseActivity(),
    IMediaSelectionProxyProvider,
    IMediaCheckSelectSateListener,
    IMediaClickListener,
    IMediaPhotoCapture,
    View.OnClickListener {

    private var _mediaStoreCaptureProxy: MediaStoreCaptureProxy? = null
    private var _albumLoadProxy: AlbumLoadProxy? = null
    private lateinit var _mediaSelectionProxy: MediaSelectionProxy

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    private val _albumSelectionBottomSheetProxy: AlbumSelectionBottomSheetProxy by lazy_ofNone { AlbumSelectionBottomSheetProxy(this) }

    //////////////////////////////////////////////////////////

    private lateinit var _toolbar: ConstraintLayout
    private lateinit var _buttonApply: TextView
    private lateinit var _buttonPreview: TextView
    private lateinit var _buttonComplete: TextView
    private lateinit var _layoutOriginal: LinearLayout
    private lateinit var _buttonBack: TextView
    private lateinit var _checkRadioViewOriginal: CheckRadioView
    private lateinit var _viewEmpty: View
    private lateinit var _viewContainer: View

    //////////////////////////////////////////////////////////

    private var _isOriginalEnable = false
    private var _album: Album? = null

    //////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    private var _albumLoadListener = object : IAlbumLoadListener {
        override fun onAlbumStart() {
            // do nothing
        }

        override fun onAlbumLoad(cursor: Cursor) {
            _albumSelectionBottomSheetProxy.setAlbumFolderCursor(cursor)

            Handler(Looper.getMainLooper()).post {
                if (cursor.moveToFirst()) {
                    _album = Album.valueOf(cursor).apply {
                        onAlbumSelected(this)
                    }
                }
            }
        }

        override fun onAlbumReset() {
            _albumSelectionBottomSheetProxy.clearFolderSheetDialog()
        }
    }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    private var _folderBottomSheetListener = object : IAlbumBottomSheetListener {
        override fun onInitData(adapter: AlbumSelectionAdapter) {
            adapter.setListData(_albumSelectionBottomSheetProxy.readAlbumFromCursor())
        }

        override fun onItemClick(album: Album, position: Int) {
            if (!_albumSelectionBottomSheetProxy.setLastFolderCheckedPosition(position)) return
            _albumLoadProxy?.setStateCurrentSelection(position)

            _buttonApply.text = album.getDisplayName(activity)
            onAlbumSelected(album)
        }
    }

    //////////////////////////////////////////////////////////

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _mediaSelectionProxy.onSaveInstanceState(outState)
        _albumLoadProxy?.onSaveInstanceState(outState)
        outState.putBoolean(CImageKMatisse.CHECK_STATE, _isOriginalEnable)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        _albumLoadProxy?.onDestroy()
        selection?.mediaCheckedListener = null
        selection?.mediaSelectedListener = null
    }

    override fun initFlag() {
        try {
            selection?.onLoadStatusBarListener?.invoke(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun configActivity() {
        super.configActivity()
        initView()
        try {
            selection?.onLoadToolbarListener?.invoke(this, _toolbar)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (selection?.mediaCaptureEnable == true) {
            _mediaStoreCaptureProxy = MediaStoreCaptureProxy(this)
            if (selection?.mediaCaptureStrategy == null)
                throw RuntimeException("Don't forget to set CaptureStrategy.")
            _mediaStoreCaptureProxy?.setCaptureStrategy(selection?.mediaCaptureStrategy!!)
        }
    }

    override fun getResourceLayoutId() = R.layout.activity_matisse

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    override fun setViewData() {
        _buttonApply.setText(getAttrString(R.attr.BottomBarAlbum_Text, R.string.album_name_all))
        _mediaSelectionProxy = MediaSelectionProxy(this).apply { onCreate(savedInstanceState) }
        _albumLoadProxy = AlbumLoadProxy(this, _albumLoadListener)
        _albumSelectionBottomSheetProxy.bindLifecycle(this@MatisseActivity)
        updateBottomToolbar()
    }

    override fun initListener() {
        setOnClickListener(this, _buttonApply, _buttonPreview, _layoutOriginal, _buttonComplete, _buttonBack)
    }

    override fun onMediaSelectUpdate() {
        updateBottomToolbar()
        selection?.mediaSelectedListener?.onSelected(
            _mediaSelectionProxy.asListOfUri(), _mediaSelectionProxy.asListOfString()
        )
    }

    @OptIn(OPermission_QUERY_ALL_PACKAGES::class)
    override fun onCapture() {
        _mediaStoreCaptureProxy?.dispatchCaptureIntent(this, CImageKMatisse.REQUEST_CODE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CImageKMatisse.REQUEST_CODE_PREVIEW -> {
                if (resultCode != Activity.RESULT_OK) return
                val cropPath = ImageKMatisse.obtainCropResult(data)

                // 裁剪带回数据，则认为图片经过裁剪流程
                if (cropPath != null) finishIntentFromCrop(activity, cropPath)
                else doActivityResultFromPreview(data)
            }

            CImageKMatisse.REQUEST_CODE_CAPTURE -> doActivityResultFromCapture()
            CImageKMatisse.REQUEST_CODE_CROP -> {
                data?.run {
                    val resultUri = UCrop.getOutput(data)
                    finishIntentFromCrop(activity, resultUri)
                }
            }

            CImageKMatisse.REQUEST_CODE_CROP_ERROR -> {
                data?.run {
                    val cropError = UCrop.getError(data)?.message ?: ""
                    IncapableCause.handleCause(activity, IncapableCause(cropError))
                }
            }
        }
    }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    override fun onClick(v: View?) {
        when (v) {
            _buttonBack -> onBackPressed()
            _buttonPreview -> {
                if (_mediaSelectionProxy.count() == 0) {
                    handleCauseTips(getString(R.string.please_select_media_resource))
                    return
                }

                MediaSelectionPreviewActivity.newInstance(activity, _mediaSelectionProxy.getDataWithBundle(), _isOriginalEnable)
            }

            _buttonComplete -> {
                if (_mediaSelectionProxy.count() == 0) {
                    handleCauseTips(getString(R.string.please_select_media_resource))
                    return
                }

                val item = _mediaSelectionProxy.asList()[0]
                if (selection?.openCrop() == true && selection?.isSupportCrop(item) == true) {
                    gotoImageCrop(this, _mediaSelectionProxy.asListOfUri() as ArrayList<Uri>)
                    return
                }

                handleIntentFromPreview(activity, _isOriginalEnable, _mediaSelectionProxy.items())
            }

            _layoutOriginal -> {
                val count = countOverMaxSize(_mediaSelectionProxy)
                if (count <= 0) {
                    _isOriginalEnable = !_isOriginalEnable
                    _checkRadioViewOriginal.setChecked(_isOriginalEnable)
                    selection?.mediaCheckedListener?.onCheck(_isOriginalEnable)
                    return
                }

                handleCauseTips(
                    getString(R.string.error_over_original_count, count, selection?.imageOriginalMaxSize),
                    AFormType.DIALOG
                )
            }

            _buttonApply -> {
                if (_album?.isAll() == true && _album?.isEmpty() == true) {
                    handleCauseTips(getString(R.string.empty_album))
                    return
                }

                _albumSelectionBottomSheetProxy.createFolderSheetDialog(_folderBottomSheetListener)
            }
        }
    }

    override fun provideMediaSelectionProxy(): MediaSelectionProxy =
        _mediaSelectionProxy

    override fun onMediaClick(album: Album?, item: Media, adapterPosition: Int) {
        val intent = Intent(this, AlbumPreviewActivity::class.java)
            .putExtra(CImageKMatisse.EXTRA_ALBUM, album as Parcelable)
            .putExtra(CImageKMatisse.EXTRA_ITEM, item)
            .putExtra(CImageKMatisse.EXTRA_DEFAULT_BUNDLE, _mediaSelectionProxy.getDataWithBundle())
            .putExtra(CImageKMatisse.EXTRA_RESULT_ORIGINAL_ENABLE, _isOriginalEnable)

        startActivityForResult(intent, CImageKMatisse.REQUEST_CODE_PREVIEW)
    }

    //////////////////////////////////////////////////////////

    private fun initView() {
        _toolbar = findViewById(R.id.toolbar)
        _buttonApply = findViewById(R.id.button_apply)
        _buttonPreview = findViewById(R.id.button_preview)
        _layoutOriginal = findViewById(R.id.original_layout)
        _buttonComplete = findViewById(R.id.button_complete)
        _buttonBack = findViewById(R.id.button_back)
        _checkRadioViewOriginal = findViewById(R.id.original)
        _viewEmpty = findViewById(R.id.empty_view)
        _viewContainer = findViewById(R.id.container)
    }

    /**
     * 处理预览的[onActivityResult]
     */
    private fun doActivityResultFromPreview(data: Intent?) {
        data?.apply {

            _isOriginalEnable = getBooleanExtra(CImageKMatisse.EXTRA_RESULT_ORIGINAL_ENABLE, false)
            val isApplyData = getBooleanExtra(CImageKMatisse.EXTRA_RESULT_APPLY, false)
            handlePreviewIntent(activity, data, _isOriginalEnable, isApplyData, _mediaSelectionProxy)

            if (!isApplyData) {
                val albumSelectionFragment = supportFragmentManager.findFragmentByTag(
                    AlbumSelectionFragment::class.java.simpleName
                )
                if (albumSelectionFragment is AlbumSelectionFragment) {
                    albumSelectionFragment.refreshSelectionAdapter()
                }
                updateBottomToolbar()
            }
        }
    }

    /**
     * 处理拍照的[onActivityResult]
     */
    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    private fun doActivityResultFromCapture() {
        val capturePathUri = _mediaStoreCaptureProxy?.getCurrentPhotoUri() ?: return
        val capturePath = _mediaStoreCaptureProxy?.getCurrentPhotoStrPath() ?: return
        // 刷新系统相册
        MediaScannerConnection.scanFile(this, arrayOf(capturePath), null, null)
        // 重新获取相册数据
        _albumLoadProxy?.loadAlbumData()
        // 手动插入到相册列表
        _albumSelectionBottomSheetProxy.insetAlbumToFolder(capturePathUri)
        // 重新load所有资源
        _albumSelectionBottomSheetProxy.getAlbumFolderList()?.apply {
            onAlbumSelected(this[0])
        }

        // Check is Crop first
        if (selection?.openCrop() == true) {
            gotoImageCrop(this, arrayListOf(capturePathUri))
        }
    }

    private fun updateBottomToolbar() {
        val selectedCount = _mediaSelectionProxy.count()
        setCompleteText(selectedCount)

        if (selection?.imageOriginalEnable == true) {
            setViewVisible(true, _layoutOriginal)
            updateOriginalState()
        } else {
            setViewVisible(false, _layoutOriginal)
        }
    }

    private fun setCompleteText(selectedCount: Int) {
        if (selectedCount == 0) {
            _buttonComplete.setText(getAttrString(R.attr.Navigation_TextSure, R.string.button_sure))

        } else if (selectedCount == 1 && selection?.singleSelectionModeEnabled() == true) {
            _buttonComplete.setText(getAttrString(R.attr.Navigation_TextSure, R.string.button_sure))

        } else {
            _buttonComplete.text =
                getString(getAttrString(R.attr.Navigation_TextSure, R.string.button_sure))
                    .plus("(").plus(selectedCount.toString()).plus(")")
        }
    }

    private fun updateOriginalState() {
        _checkRadioViewOriginal.setChecked(_isOriginalEnable)
        if (countOverMaxSize(_mediaSelectionProxy) > 0 || _isOriginalEnable) {
            handleCauseTips(
                getString(R.string.error_over_original_size, selection?.imageOriginalMaxSize),
                AFormType.DIALOG
            )

            _checkRadioViewOriginal.setChecked(false)
            _isOriginalEnable = false
        }
    }

    private fun onAlbumSelected(album: Album) {
        if (album.isAll() && album.isEmpty()) {
            setViewVisible(true, _viewEmpty)
            setViewVisible(false, _viewContainer)
        } else {
            setViewVisible(false, _viewEmpty)
            setViewVisible(true, _viewContainer)
            val albumSelectionFragment = AlbumSelectionFragment.newInstance(album)
            supportFragmentManager.beginTransaction().replace(_viewContainer.id, albumSelectionFragment, AlbumSelectionFragment::class.java.simpleName).commitAllowingStateLoss()
        }
    }
}
