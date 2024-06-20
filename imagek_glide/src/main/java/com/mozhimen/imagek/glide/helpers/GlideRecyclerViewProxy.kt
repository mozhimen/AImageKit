package com.mozhimen.imagek.glide.helpers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mozhimen.basick.elemk.androidx.lifecycle.bases.BaseWakeBefDestroyLifecycleObserver
import com.mozhimen.basick.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.basick.lintk.optins.OApiCall_BindViewLifecycle
import com.mozhimen.basick.lintk.optins.OApiInit_ByLazy
import com.mozhimen.basick.utilk.kotlin.UtilKLazyJVM.lazy_ofNone

/**
 * @ClassName BaseGlideProxy
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/6/20
 * @Version 1.0
 */

@OApiInit_ByLazy
@OApiCall_BindLifecycle
@OApiCall_BindViewLifecycle
class GlideRecyclerViewProxy(private var _activity: Context?) : BaseWakeBefDestroyLifecycleObserver() {
    private val _onScrollListener: RecyclerView.OnScrollListener by lazy_ofNone {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    _activity?.let {
                        Glide.with(it).pauseRequestsRecursive()
                    }
                } else {
                    _activity?.let {
                        Glide.with(it).resumeRequests()
                    }
                }
            }
        }
    }

    fun addListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(_onScrollListener)
    }

    fun removeListener(recyclerView: RecyclerView?) {
        recyclerView?.removeOnScrollListener(_onScrollListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        _activity = null
        super.onDestroy(owner)
    }
}