package com.mozhimen.imagek.coil2.impls

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.widget.TextView
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import com.mozhimen.kotlin.elemk.commons.IA_AListener
import java.lang.ref.WeakReference

/**
 * @ClassName ImageGetterCoil
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/2/20
 * @Version 1.0
 */
open class ImageGetterCoil2(
    textView: TextView,
    private val imageLoader: ImageLoader = Coil.imageLoader(textView.context),
    private val _modifier: IA_AListener<String>? = null,
):ImageGetter {
    private val _textViewRef: WeakReference<TextView> = WeakReference(textView)

    override fun getDrawable(source: String): Drawable {
        val finalSource = _modifier?.invoke(source) ?: source
        val bitmapDrawablePlaceholder = BitmapDrawablePlaceHolder()
        _textViewRef.get()?.let {
            it.post {
                imageLoader.enqueue(ImageRequest.Builder(it.context).data(finalSource).apply {
                    target { drawable ->
                        bitmapDrawablePlaceholder.updateDrawable(drawable)
                        // invalidating the drawable doesn't seem to be enough...
                        it.text = it.text
                    }
                }.build())
            }
        }

        // Since this loads async, we return a "blank" drawable, which we update
        // later
        return bitmapDrawablePlaceholder
    }

    private inner class BitmapDrawablePlaceHolder : BitmapDrawable(_textViewRef.get()?.resources, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) {

        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }

        fun updateDrawable(drawable: Drawable) {
            this.drawable = drawable
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            drawable.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)
        }
    }
}