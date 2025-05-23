package com.mozhimen.imagek.glide.ext.impls

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.mozhimen.imagek.glide.ext.mos.GlideAppMapper
import com.mozhimen.kotlin.elemk.commons.IA_AListener
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * @ClassName ImageKGlideFactory
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
/** The default factory for [OkHttpUrlLoader]s.  */ // Public API.
/**
 * Constructor for a new Factory that runs requests using given client.
 * @param _client this is typically an instance of `OkHttpClient`.
 */
class ModelLoaderFactoryImpl
/** Constructor for a new Factory that runs requests using a static singleton client.  */ @JvmOverloads constructor(
    private val _client: Call.Factory = internalClient!!,
    private val _listener: IA_AListener<String?>
) : ModelLoaderFactory<GlideAppMapper, InputStream> {
    companion object {
        @Volatile
        private var internalClient: Call.Factory? = null
            get() {
                if (field == null) {
                    synchronized(ModelLoaderFactoryImpl::class.java) {
                        if (field == null)
                            field = OkHttpClient()
                    }
                }
                return field
            }
    }

    ///////////////////////////////////////////////////////////////

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideAppMapper, InputStream> =
        ModelLoaderImpl(_client, _listener)

    override fun teardown() {
        // Do nothing, this instance doesn't own the client.
    }
}