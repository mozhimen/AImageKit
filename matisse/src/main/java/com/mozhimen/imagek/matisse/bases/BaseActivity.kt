package com.mozhimen.imagek.matisse.bases

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.CallSuper
import com.mozhimen.kotlin.elemk.androidx.appcompat.commons.IActivity
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.annors.AFormType
import com.mozhimen.imagek.matisse.mos.IncapableCause
import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.imagek.matisse.utils.handleCause
import com.mozhimen.imagek.matisse.utils.obtainAttrString
import com.mozhimen.kotlin.elemk.androidx.appcompat.bases.BaseActivity

abstract class BaseActivity : BaseActivity(), IActivity {

    lateinit var activity: Activity
    var selection: Selection? = null
    var savedInstanceState: Bundle? = null

    //////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        selection = Selection.getInstance()
        setTheme(selection?.themeRes ?: R.style.ImageKMatisse_Default)
        super.onCreate(savedInstanceState)
        if (safeCancelActivity()) return
        activity = this

        initFlag()
        setContentView(getResourceLayoutId())
        configActivity()
        configSaveInstanceState(savedInstanceState)
        setViewData()
        initListener()
    }

    //////////////////////////////////////////////////////////////////////////////////

    abstract fun setViewData()

    abstract fun initListener()

    abstract fun getResourceLayoutId(): Int

    //////////////////////////////////////////////////////////////////////////////////

    override fun initLayout() {}

    override fun initData(savedInstanceState: android.os.Bundle?) {}

    override fun initFlag() {}

    /**
     * 处理状态栏(状态栏颜色、状态栏字体颜色、是否隐藏等操作)
     * 空实现，供外部重写
     */
    @CallSuper
    open fun configActivity() {
        if (selection?.needOrientationRestriction() == true) {
            requestedOrientation = selection?.orientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取主题配置中的属性值
     * @param attr 主题配置属性key
     * @param defaultRes 默认值
     */
    fun getAttrString(attr: Int, defaultRes: Int): Int =
        obtainAttrString(this, attr, defaultRes)

    /**
     * 抽离提示方法
     */
    fun handleCauseTips(message: String = "", @AFormType form: Int = AFormType.TOAST, title: String = "", dismissLoading: Boolean = true) {
        handleCause(activity, IncapableCause(form, title, message, dismissLoading))
    }

    //////////////////////////////////////////////////////////////////////////////////

    private fun safeCancelActivity(): Boolean {
        if (selection?.hasInited == false) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return true
        }

        return false
    }

    private fun configSaveInstanceState(savedInstanceState: Bundle?) {
        this.savedInstanceState = savedInstanceState
    }
}