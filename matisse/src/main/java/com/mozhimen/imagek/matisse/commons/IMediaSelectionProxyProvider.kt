package com.mozhimen.imagek.matisse.commons

import com.mozhimen.imagek.matisse.helpers.MediaSelectionProxy

/**
 * @ClassName IMediaSelectionProxyProvider
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/12/20 22:03
 * @Version 1.0
 */
interface IMediaSelectionProxyProvider {
    fun provideMediaSelectionProxy(): MediaSelectionProxy
}