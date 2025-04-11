package com.mozhimen.imagek.glide.ext.mos

/**
 * @ClassName GlideImageFileId
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
class GlideAppMapper constructor(var source: String) {

    /**
     * fid对应的url
     */
    var destination: String? = null

    ////////////////////////////////////////////////////////////////

    /**
     *  需要重写equals和hashCode，用于从缓存中取出ImageFid
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        return source == (other as GlideAppMapper).source
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }
}