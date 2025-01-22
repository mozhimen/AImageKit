package com.mozhimen.imagek.glide.ext

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.mozhimen.kotlin.elemk.commons.IA_AListener
import okhttp3.Call
import java.io.InputStream

/**
 * @ClassName ImageKFileIdLoader
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
class ImageKGlideModelLoader(
    private val _client: Call.Factory,
    private val _listener: IA_AListener<String?>
) : ModelLoader<ImageKGlideFile, InputStream> {

    override fun handles(model: ImageKGlideFile): Boolean {
        if (model.url?.isNotEmpty() == true) {
            return true
        }
        return model.fileId.isNotEmpty()
    }

    override fun buildLoadData(model: ImageKGlideFile, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), OkHttpInputStreamDataFetcher(_client, model, _listener))
    }
}