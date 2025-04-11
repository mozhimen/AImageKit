package com.mozhimen.imagek.glide.ext.impls

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.mozhimen.imagek.glide.ext.impls.DataFetcherImpl
import com.mozhimen.imagek.glide.ext.mos.GlideAppMapper
import com.mozhimen.kotlin.elemk.commons.IA_AListener
import okhttp3.Call
import java.io.InputStream

/**
 * @ClassName ImageKFileIdLoader
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
class ModelLoaderImpl(
    private val _client: Call.Factory,
    private val _onExecuteMapper: IA_AListener<String?>,
) : ModelLoader<GlideAppMapper, InputStream> {

    override fun handles(model: GlideAppMapper): Boolean =
        if (model.destination?.isNotEmpty() == true) true else model.source.isNotEmpty()

    override fun buildLoadData(model: GlideAppMapper, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? =
        ModelLoader.LoadData(ObjectKey(model), DataFetcherImpl(_client, model, _onExecuteMapper))
}