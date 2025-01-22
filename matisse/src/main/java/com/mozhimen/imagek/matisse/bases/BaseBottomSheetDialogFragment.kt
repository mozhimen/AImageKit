package com.mozhimen.imagek.matisse.bases

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mozhimen.kotlin.elemk.androidx.appcompat.bases.BaseAppCompatDialogFragment
import com.mozhimen.imagek.matisse.R

abstract class BaseBottomSheetDialogFragment : BaseAppCompatDialogFragment() {

    private lateinit var _bottomSheetBehavior: BottomSheetBehavior<*>
    private var _coordinator: ViewGroup? = null
    private var _bottomSheet: FrameLayout? = null
    private var _contentView: View? = null
    private var _defaultHeight = -1
    private var _isCancelable = true

    /////////////////////////////////////////////////////////////////

    private var _bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // do noting
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
        }
    }

    /////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _coordinator = inflater.inflate(R.layout.fragment_dialog_bottom_sheet, container) as ViewGroup
        _bottomSheet = _coordinator?.findViewById(R.id.design_bottom_sheet)

        _bottomSheetBehavior = BottomSheetBehavior.from(_bottomSheet!!)
        _bottomSheetBehavior.setBottomSheetCallback(_bottomSheetCallback)
        _bottomSheetBehavior.isHideable = _isCancelable

        _contentView = getContentView(inflater, _coordinator!!)
        _bottomSheet?.addView(_contentView)

        if (_defaultHeight != -1) {
            setDefaultHeight(_defaultHeight)
        }

        // 设置 dialog 位于屏幕底部，并且设置出入动画
        setBottomLayout()
        setPeekHeight()
        initBackAction()

        return _coordinator
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        if (_isCancelable != cancelable) {
            _isCancelable = cancelable
            _bottomSheetBehavior.isHideable = cancelable
        }
    }

    /////////////////////////////////////////////////////////////////

    fun setDefaultHeight(defaultHeight: Int) {
        this._defaultHeight = defaultHeight
        if (_bottomSheet != null) {
            _bottomSheet?.layoutParams?.width = -1
            _bottomSheet?.layoutParams?.height = defaultHeight
        }
    }

    /////////////////////////////////////////////////////////////////

    open fun backAction() = false

    abstract fun getContentView(inflater: LayoutInflater, container: ViewGroup): View

    /////////////////////////////////////////////////////////////////

    private fun setPeekHeight() {
        val dm = DisplayMetrics()
        //取得窗口属性
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        //窗口高度
        val screenHeight = dm.heightPixels
        _bottomSheetBehavior.peekHeight = screenHeight
    }

    private fun initBackAction() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                backAction()
            } else false
        }
    }

    private fun setBottomLayout() {
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.matisse_bg_transparent)
            decorView.setPadding(0, 0, 0, 0)
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
            // dialog 布局位于底部
            setGravity(Gravity.BOTTOM)
            // 设置进出场动画
            setWindowAnimations(R.style.Animation_Bottom)
        }
    }
}