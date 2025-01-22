package com.mozhimen.imagek.matisse.commons

import android.net.Uri

/**
 * Created by Lijianyou on 2018-09-07.
 * @author  Lijianyou
 */
interface IMediaSelectedListener {
    /**
     * @param uriList the selected item [Uri] list.
     * @param pathList the selected item file path list.
     */
    fun onSelected(uriList: List<Uri>, pathList: List<String>)
}