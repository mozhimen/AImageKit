package com.mozhimen.imagek.matisse.mos

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.mozhimen.imagek.matisse.annors.AFormType
import com.mozhimen.imagek.matisse.commons.INoticeEventListener
import com.mozhimen.imagek.matisse.widgets.IncapableDialog

class IncapableCause {

    companion object {
        fun handleCause(context: Context, cause: IncapableCause?) {
            if (cause?.onNoticeEventListener != null) {
                cause.onNoticeEventListener?.invoke(context, cause.formType, cause.title ?: "", cause.message ?: "")
                return
            }

            when (cause?.formType) {
                AFormType.DIALOG -> {
                    IncapableDialog.newInstance(cause.title, cause.message)
                        .show(
                            (context as FragmentActivity).supportFragmentManager,
                            IncapableDialog::class.java.name
                        )
                }

                AFormType.TOAST -> {
                    Toast.makeText(context, cause.message, Toast.LENGTH_SHORT).show()
                }

                AFormType.LOADING -> {
                    // TODO Leo 2019-12-24 complete loading
                }
            }
        }
    }

    //////////////////////////////////////////////////////////

    var formType = AFormType.TOAST
    var title: String? = null
    var message: String? = null
    var isDismissLoading: Boolean? = null
    var onNoticeEventListener: INoticeEventListener? = null

    ////////////////////////////////////////////////////////////////////////////////////

    constructor(message: String) : this(AFormType.TOAST, message)
    constructor(@AFormType form: Int, message: String) : this(form, "", message)
    constructor(@AFormType form: Int, title: String, message: String) : this(form, title, message, true)
    constructor(@AFormType form: Int, title: String, message: String, dismissLoading: Boolean) {
        this.formType = form
        this.title = title
        this.message = message
        this.isDismissLoading = dismissLoading
        this.onNoticeEventListener = Selection.getInstance().onNoticeEventListener
    }
}