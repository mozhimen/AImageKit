package com.mozhimen.imagek.matisse.bases

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.mozhimen.kotlin.utilk.android.os.UtilKBuildVersion
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.annors.AFormType
import com.mozhimen.imagek.matisse.cons.CImageKMatisse
import com.mozhimen.imagek.matisse.helpers.MediaSelectionProxy
import com.mozhimen.imagek.matisse.mos.IncapableCause
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.uis.adapters.MediaPreviewPagerAdapter
import com.mozhimen.imagek.matisse.uis.fragments.MediaImagePreviewFragment
import com.mozhimen.imagek.matisse.widgets.CheckRadioView
import com.mozhimen.imagek.matisse.widgets.CheckView
import com.mozhimen.imagek.matisse.widgets.PreviewViewPager
import com.mozhimen.imagek.matisse.utils.PhotoMetadataUtils
import com.mozhimen.imagek.matisse.utils.countOverMaxSize
import com.mozhimen.imagek.matisse.utils.finishIntentFromCropSuccess
import com.mozhimen.imagek.matisse.utils.finishIntentFromPreviewApply
import com.mozhimen.imagek.matisse.utils.gotoImageCrop
import com.mozhimen.imagek.matisse.utils.setOnClickListener
import com.mozhimen.imagek.matisse.utils.setViewVisible
import com.mozhimen.imagek.ucrop.UCrop

/**
 * desc：BasePreviewActivity</br>
 * time: 2018/9/6-11:15</br>
 * author：liubo </br>
 * since V 1.0.0 </br>
 */
open class BasePreviewActivity : BaseActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    lateinit var mediaSelectionProxy: MediaSelectionProxy
    var mediaPreviewPagerAdapter: MediaPreviewPagerAdapter? = null
    var previousPos = -1
    private var _isOriginalEnable = false

    ////////////////////////////////////////////////////////////////////////////////////////////
    private lateinit var _buttonPreview: TextView
    private lateinit var _buttonApply: TextView
    private lateinit var _textViewSize: TextView
    private lateinit var _layoutOriginal: LinearLayout
    private var _checkRadioViewOriginal: CheckRadioView? = null

    lateinit var checkView: CheckView
    var previewViewPager: PreviewViewPager? = null

    ////////////////////////////////////////////////////////////////////////////////////////////

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
            selection?.onLoadToolbarListener?.invoke(this, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (UtilKBuildVersion.isAfterV_19_44_K()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        mediaSelectionProxy = MediaSelectionProxy(this)
        _isOriginalEnable = if (savedInstanceState == null) {
            mediaSelectionProxy.onCreate(intent.getBundleExtra(CImageKMatisse.EXTRA_DEFAULT_BUNDLE))
            intent.getBooleanExtra(CImageKMatisse.EXTRA_RESULT_ORIGINAL_ENABLE, false)
        } else {
            mediaSelectionProxy.onCreate(savedInstanceState)
            savedInstanceState!!.getBoolean(CImageKMatisse.CHECK_STATE)
        }
    }

    override fun getResourceLayoutId() = R.layout.activity_preview

    override fun setViewData() {
        _buttonPreview.setText(getAttrString(R.attr.Preview_TextBack, R.string.button_back))

        mediaPreviewPagerAdapter = MediaPreviewPagerAdapter(supportFragmentManager, null)
        previewViewPager?.adapter = mediaPreviewPagerAdapter
        checkView.setCountable(selection?.isCountable() == true)
        updateApplyButton()
    }

    override fun initListener() {
        setOnClickListener(this, _buttonPreview, _buttonApply, checkView, _layoutOriginal)
        previewViewPager?.addOnPageChangeListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mediaSelectionProxy.onSaveInstanceState(outState)
        outState.putBoolean(CImageKMatisse.CHECK_STATE, _isOriginalEnable)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishIntentFromPreviewApply(activity, false, mediaSelectionProxy, _isOriginalEnable)
        super.onBackPressed()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        val pager = previewViewPager
        val adapter = pager?.adapter as? MediaPreviewPagerAdapter
        adapter ?: return
        checkView.apply {
            if (previousPos != -1 && previousPos != position) {
                (adapter.instantiateItem(pager, previousPos) as MediaImagePreviewFragment).resetView()
                val item = adapter.getMediaItem(position)
                if (selection?.isCountable() == true) {
                    val checkedNum = mediaSelectionProxy.checkedNumOf(item)
                    setCheckedNum(checkedNum)
                    if (checkedNum > 0) {
                        setEnable(true)
                    } else {
                        setEnable(!mediaSelectionProxy.maxSelectableReached(item))
                    }
                } else {
                    val checked = mediaSelectionProxy.isSelected(item)
                    setChecked(checked)
                    if (checked)
                        setEnable(true)
                    else
                        setEnable(!mediaSelectionProxy.maxSelectableReached(item))
                }
                updateSize(item)
            }
        }

        previousPos = position
    }

    override fun onClick(v: View?) {
        when (v) {
            _buttonPreview -> onBackPressed()

            _buttonApply -> {
                if (selection?.openCrop() == true) {
                    val item = mediaSelectionProxy.items()[0]

                    if (selection?.isSupportCrop(item) == true) {
                        item.getContentUri().apply { gotoImageCrop(this@BasePreviewActivity, arrayListOf(this)) }
                    } else {
                        finishIntentFromPreviewApply(activity, true, mediaSelectionProxy, _isOriginalEnable)
                    }
                } else {
                    finishIntentFromPreviewApply(activity, true, mediaSelectionProxy, _isOriginalEnable)
                }
            }

            _layoutOriginal -> {
                val count = countOverMaxSize(mediaSelectionProxy)
                if (count <= 0) {
                    _isOriginalEnable = !_isOriginalEnable
                    _checkRadioViewOriginal?.setChecked(_isOriginalEnable)
                    selection?.mediaCheckedListener?.onCheck(_isOriginalEnable)
                    return
                }

                handleCauseTips(
                    getString(R.string.error_over_original_count, count, selection?.imageOriginalMaxSize),
                    AFormType.DIALOG
                )
            }

            checkView -> {
                val item = mediaPreviewPagerAdapter?.getMediaItem(previewViewPager?.currentItem ?: 0)
                if (mediaSelectionProxy.isSelected(item)) {
                    mediaSelectionProxy.remove(item)
                    if (selection?.isCountable() == true) {
                        checkView.setCheckedNum(CheckView.UNCHECKED)
                    } else {
                        checkView.setChecked(false)
                    }
                } else {
                    if (assertAddSelection(item)) {
                        mediaSelectionProxy.add(item)
                        if (selection?.isCountable() == true) {
                            checkView.setCheckedNum(mediaSelectionProxy.checkedNumOf(item))
                        } else {
                            checkView.setChecked(true)
                        }
                    }
                }

                updateApplyButton()

                selection?.mediaSelectedListener?.onSelected(
                    mediaSelectionProxy.asListOfUri(), mediaSelectionProxy.asListOfString()
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            CImageKMatisse.REQUEST_CODE_CROP -> {
                data?.run {
                    val resultUri = UCrop.getOutput(data) ?: return@run
                    finishIntentFromCropSuccess(activity, resultUri)
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    fun updateSize(item: Media?) {
        item?.apply {
            _textViewSize.apply {
                if (isGif()) {
                    setViewVisible(true, this)
                    text = String.format(
                        getString(R.string.picture_size), PhotoMetadataUtils.getSizeInMB(size)
                    )
                } else {
                    setViewVisible(false, this)
                }
            }

            _layoutOriginal?.apply {
                if (isVideo()) {
                    setViewVisible(false, this)
                } else if (selection?.imageOriginalEnable == true) {
                    setViewVisible(true, this)
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    private fun initView() {
        _buttonPreview = findViewById(R.id.button_preview)
        previewViewPager = findViewById(R.id.pager)
        checkView = findViewById(R.id.check_view)
        _buttonApply = findViewById(R.id.button_apply)
        _layoutOriginal = findViewById(R.id.original_layout)
        _checkRadioViewOriginal = findViewById(R.id.original)
        _textViewSize = findViewById(R.id.tv_size)
    }

    private fun updateApplyButton() {
        val selectedCount = mediaSelectionProxy.count()

        setApplyText(selectedCount)

        if (selection?.imageOriginalEnable == true) {
            setViewVisible(true, _layoutOriginal)
            updateOriginalState()
        } else {
            setViewVisible(false, _layoutOriginal)
        }
    }

    private fun setApplyText(selectedCount: Int) {
        _buttonApply.apply {
            when (selectedCount) {
                0 -> {
                    text = getString(
                        getAttrString(R.attr.Preview_TextConfirm, R.string.button_sure_default)
                    )
                    isEnabled = false
                }

                1 -> {
                    isEnabled = true

                    text = if (selection?.singleSelectionModeEnabled() == true) {
                        getString(R.string.button_sure_default)
                    } else {
                        getString(
                            getAttrString(
                                R.attr.Preview_TextConfirm, R.string.button_sure_default
                            )
                        ).plus("(").plus(selectedCount.toString()).plus(")")
                    }
                }

                else -> {
                    isEnabled = true
                    text = getString(
                        getAttrString(R.attr.Preview_TextConfirm, R.string.button_sure_default),
                        "($selectedCount)"
                    )
                }
            }
        }
    }

    private fun updateOriginalState() {
        _checkRadioViewOriginal?.setChecked(_isOriginalEnable)
        if (countOverMaxSize(mediaSelectionProxy) > 0 || _isOriginalEnable) {
            handleCauseTips(
                getString(R.string.error_over_original_size, selection?.imageOriginalMaxSize),
                AFormType.DIALOG
            )
            _checkRadioViewOriginal?.setChecked(false)
            _isOriginalEnable = false
        }
    }

    private fun assertAddSelection(item: Media?): Boolean {
        val cause = mediaSelectionProxy.isAcceptable(item)
        IncapableCause.handleCause(this, cause)
        return cause == null
    }
}