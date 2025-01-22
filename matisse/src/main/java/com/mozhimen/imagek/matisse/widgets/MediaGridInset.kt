package com.mozhimen.imagek.matisse.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MediaGridInset(
    private var spanCount: Int, private var spacing: Int, private var includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.apply {
            if (includeEdge) {
                left = spacing - column * spacing / spanCount
                right = (column + 1) * spacing / spanCount

                if (position < spanCount) top = spacing
                bottom = spacing
            } else {
                left = column * spacing / spanCount
                right = spacing - (column + 1) * spacing / spanCount

                if (position >= spanCount) top = spacing
            }
        }
    }
}