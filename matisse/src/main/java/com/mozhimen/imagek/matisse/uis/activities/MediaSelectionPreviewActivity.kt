package com.mozhimen.imagek.matisse.uis.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mozhimen.imagek.matisse.bases.BasePreviewActivity
import com.mozhimen.imagek.matisse.cons.CImageKMatisse
import com.mozhimen.imagek.matisse.mos.Media

/**
 * desc：图片选中预览</br>
 * time: 2019/9/11-14:17</br>
 * author：Leo </br>
 * since V 1.0.0 </br>
 */
class MediaSelectionPreviewActivity : BasePreviewActivity() {

    companion object {
        fun newInstance(context: Context, bundle: Bundle, mOriginalEnable: Boolean) {
            val intent = Intent(context, MediaSelectionPreviewActivity::class.java)
            intent
                .putExtra(CImageKMatisse.EXTRA_DEFAULT_BUNDLE, bundle)
                .putExtra(CImageKMatisse.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable)
            (context as Activity).startActivityForResult(intent, CImageKMatisse.REQUEST_CODE_PREVIEW)
        }
    }

    //////////////////////////////////////////////////////////

    override fun setViewData() {
        super.setViewData()
        val bundle = intent.getBundleExtra(CImageKMatisse.EXTRA_DEFAULT_BUNDLE)
        val selected = bundle?.getParcelableArrayList<Media>(CImageKMatisse.STATE_SELECTION)
        selected?.apply {
            mediaPreviewPagerAdapter?.addAll(this)
            mediaPreviewPagerAdapter?.notifyDataSetChanged()
            checkView?.apply {
                if (selection?.isCountable() == true) {
                    setCheckedNum(1)
                } else {
                    setChecked(true)
                }
            }
            previousPos = 0
            updateSize(this[0])
        }
    }
}