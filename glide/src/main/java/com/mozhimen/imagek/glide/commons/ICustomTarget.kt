package com.mozhimen.imagek.glide.commons

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

/**
 * @ClassName ICustomTarget
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
open class ICustomTarget<T : Any>: CustomTarget<T>() {
    override fun onResourceReady(resource: T, transition: Transition<in T>?) {}

    override fun onLoadCleared(placeholder: Drawable?) {}
}