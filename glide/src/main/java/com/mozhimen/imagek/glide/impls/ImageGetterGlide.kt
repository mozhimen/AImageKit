package com.mozhimen.imagek.glide.impls

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.mozhimen.kotlin.elemk.commons.IA_AListener
import com.mozhimen.kotlin.utilk.commons.IUtilK
import java.lang.ref.WeakReference


/**
 * @ClassName ImageGetterGlide
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/2/20
 * @Version 1.0
 */
open class ImageGetterGlide constructor(
    textView: TextView,
    protected val _imageScaleType: ImageScaleType = ImageScaleType.WRAP_CONTENT,
    protected val _modifier: IA_AListener<String>? = null,
) : ImageGetter, IUtilK {
    enum class ImageScaleType {
        WRAP_CONTENT,
        MATCH_PARENT
    }

    ////////////////////////////////////////////////////////////////////////////

    protected val _textViewRef: WeakReference<TextView> = WeakReference(textView)

    ////////////////////////////////////////////////////////////////////////////

    override fun getDrawable(source: String): Drawable {
        val finalSource = _modifier?.invoke(source) ?: source
        val bitmapDrawablePlaceholder = BitmapDrawablePlaceholder()

        // 使用 Glide 异步加载图片
        _textViewRef.get()?.let {
            it.post {
                Glide.with(it.context)
                    .asBitmap()
                    .load(finalSource)
                    .into(bitmapDrawablePlaceholder)
            }
        }

        // Since this loads async, we return a "blank" drawable, which we update
        // later
        return bitmapDrawablePlaceholder
    }

    ////////////////////////////////////////////////////////////////////////////

    inner class BitmapDrawablePlaceholder : BitmapDrawable(_textViewRef.get()?.resources, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)), Target<Bitmap> {

        private var _drawable: Drawable? = null
            set(value) {
                field = value
                val textView = _textViewRef.get() ?: return
                value?.let { drawable ->
                    val drawableWidth = (drawable.intrinsicWidth)
                    val drawableHeight = (drawable.intrinsicHeight)
                    val maxWidth = textView.measuredWidth
                    if (drawableWidth > maxWidth || _imageScaleType == ImageScaleType.MATCH_PARENT) {
                        val calculatedHeight = maxWidth * drawableHeight / drawableWidth
                        drawable.setBounds(0, 0, maxWidth, calculatedHeight)
                        setBounds(0, 0, maxWidth, calculatedHeight)
                    } else {
                        drawable.setBounds(0, 0, drawableWidth, drawableHeight)
                        setBounds(0, 0, drawableWidth, drawableHeight)
                    }
                    textView.post {
                        _textViewRef.get()?.text = _textViewRef.get()?.text
                    }
                }
            }

        ////////////////////////////////////////////////////////////////////////////

        override fun draw(canvas: Canvas) {
            _drawable?.draw(canvas)
        }

        override fun onLoadStarted(placeholder: Drawable?) {
            placeholder?.let {
                _drawable = it
            }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            errorDrawable?.let {
                _drawable = it
            }
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            _textViewRef.get()?.let { textView ->
                _drawable = BitmapDrawable(textView.resources, resource)
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            placeholder?.let {
                _drawable = it
            }
        }

        override fun getSize(cb: SizeReadyCallback) {
            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        }

        override fun removeCallback(cb: SizeReadyCallback) {
        }

        override fun setRequest(request: Request?) {
        }

        override fun getRequest(): Request? {
            return null
        }

        override fun onStart() {
        }

        override fun onStop() {
        }

        override fun onDestroy() {
        }
    }
}