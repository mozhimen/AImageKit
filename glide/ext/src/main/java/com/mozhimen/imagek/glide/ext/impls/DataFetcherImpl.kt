package com.mozhimen.imagek.glide.ext.impls

import android.text.TextUtils
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Preconditions
import com.mozhimen.imagek.glide.ext.mos.GlideAppMapper
import com.mozhimen.kotlin.elemk.commons.IA_AListener
import com.mozhimen.kotlin.utilk.commons.IUtilK
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream

/**
 * @ClassName OkHttpStreamFetcher
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */

class DataFetcherImpl constructor(
    private val _factory: Call.Factory,
    private val _glideAppMapper: GlideAppMapper,
    private val _onExecuteMapper: IA_AListener<String?>,
) : DataFetcher<InputStream>, Callback, IUtilK {

    private var _inputStream: InputStream? = null
    private var _responseBody: ResponseBody? = null
    private var _dataCallback: DataFetcher.DataCallback<in InputStream>? = null

    // call may be accessed on the main thread while the object is in use on other threads. All other
    // accesses to variables may occur on different threads, but only one at a time.
    @Volatile
    private var _call: Call? = null

    //////////////////////////////////////////////////////////////////////////

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val glideAppMapper: GlideAppMapper = _glideAppMapper
        var destination: String? = glideAppMapper.destination
        if (TextUtils.isEmpty(destination)) {
            destination = _onExecuteMapper.invoke(glideAppMapper.source)//Router.serviceRouter.executeCmsNodeGetByIdAndPath(glideImageFileId.getFileId())
        }
        if (destination.isNullOrEmpty()) {
            callback.onLoadFailed(RuntimeException("查询失败，未找到该节点"))
            return
        }
        val requestBuilder: Request.Builder = Request.Builder().url(destination)
        val request: Request = requestBuilder.build()
        _dataCallback = callback
        _call = _factory.newCall(request)
        _call!!.enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        UtilKLogWrapper.d(TAG, "OkHttp failed to obtain result $e")
        _dataCallback?.onLoadFailed(e)
    }

    override fun onResponse(call: Call, response: Response) {
        _responseBody = response.body
        if (response.isSuccessful) {
            val contentLength = Preconditions.checkNotNull(_responseBody).contentLength()
            _inputStream = ContentLengthInputStream.obtain(_responseBody!!.byteStream(), contentLength)
            _dataCallback?.onDataReady(_inputStream)
        } else {
            _dataCallback?.onLoadFailed(HttpException(response.message, response.code))
        }
    }

    override fun cleanup() {
        try {
            _inputStream?.close()
            _responseBody?.close()
            _dataCallback = null
        } catch (e: IOException) {
            // Ignored
        }
    }

    override fun cancel() {
        _call?.cancel()
    }

    override fun getDataClass(): Class<InputStream> =
        InputStream::class.java

    override fun getDataSource(): DataSource =
        DataSource.REMOTE
}

