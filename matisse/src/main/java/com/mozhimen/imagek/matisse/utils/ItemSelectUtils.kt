@file:JvmName("ItemSelectUtils")

package com.mozhimen.imagek.matisse.utils

import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.imagek.matisse.helpers.MediaSelectionProxy

/**
 * 返回选中图片中，超过原图大小上限的图片数量
 * @param selectedCollection 资源选中操作类
 */
fun countOverMaxSize(selectedCollection: MediaSelectionProxy): Int {
    var count = 0
    selectedCollection.asList().filter { it.isImage() }.forEach {
        val size = PhotoMetadataUtils.getSizeInMB(it.size)
        if (size > Selection.getInstance().imageOriginalMaxSize) count++
    }
    return count
}