package com.mozhimen.imagek.matisse.commons

import android.content.Context
import android.view.View
import com.mozhimen.imagek.matisse.bases.BaseActivity

/**
 * @ClassName INoticeTypeListener
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/14
 * @Version 1.0
 */
typealias INoticeEventListener = (context: Context, noticeType: Int, title: String, msg: String) -> Unit

typealias ILoadStatusBarListener = (activity: BaseActivity) -> Unit

typealias ILoadToolBarListener = (activity: BaseActivity, view: View?) -> Unit