package com.mozhimen.imagek.matisse.uis.adapters

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.uis.fragments.MediaImagePreviewFragment

/**
 * Created by liubo on 2018/9/6.
 */
class MediaPreviewPagerAdapter(manager: FragmentManager, listener: OnPrimaryItemSetListener?) :
    FragmentStatePagerAdapter(manager) {

    var items: ArrayList<Media> = ArrayList()
    var kListener: OnPrimaryItemSetListener? = null

    init {
        this.kListener = listener
    }

    override fun getCount() = items.size

    override fun getItem(position: Int) = MediaImagePreviewFragment.newInstance(items[position])

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        kListener?.onPrimaryItemSet(position)
    }

    fun getMediaItem(position: Int): Media? {
        if (count > position) {
            return items[position]
        }

        return null
    }

    fun addAll(items: List<Media>) {
        this.items.addAll(items)
    }

    interface OnPrimaryItemSetListener {
        fun onPrimaryItemSet(position: Int)
    }
}