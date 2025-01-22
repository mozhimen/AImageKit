package com.mozhimen.imagek.matisse.cons

import com.mozhimen.imagek.matisse.helpers.MediaMimeTypeHelper

/**
 * Describe : MIME Type enumeration to restrict selectable media on the selection activity.
 * Matisse only supports images and videos.
 * Created by Leo on 2018/8/29 on 14:55.
 */
enum class EMimeType {


    ////////////////////////////////////////////////////////////////////////
    //images
    ////////////////////////////////////////////////////////////////////////

    JPG {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("jpg", "jpeg")
        override fun getKey() = "image/jpg"
    },

    JPEG {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("jpg", "jpeg")
        override fun getKey() = "image/jpeg"
    },

    PNG {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("png")
        override fun getKey() = "image/png"
    },

    GIF {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("gif")
        override fun getKey() = "image/gif"
    },

    BMP {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("bmp")
        override fun getKey() = "image/x-ms-bmp"
    },

    WEBP {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("webp")
        override fun getKey() = "image/webp"
    },

    ////////////////////////////////////////////////////////////////////////
    //videos
    ////////////////////////////////////////////////////////////////////////

    MPEG {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("mpg")
        override fun getKey() = "video/mpeg"
    },

    MP4 {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("m4v", "mp4")
        override fun getKey() = "video/mp4"
    },

    QUICKTIME {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("mov")
        override fun getKey() = "video/quicktime"
    },

    THREEGPP {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("3gp", "3gpp")
        override fun getKey() = "video/3gpp"
    },

    THREEGPP2 {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("3g2", "3gpp2")
        override fun getKey() = "video/3gpp2"
    },

    MKV {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("mkv")
        override fun getKey() = "video/x-matroska"
    },

    WEBM {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("webm")
        override fun getKey() = "video/webm"
    },

    TS {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("ts")
        override fun getKey() = "video/mp2ts"
    },

    AVI {
        override fun getValue() = MediaMimeTypeHelper.arraySetOf("avi")
        override fun getKey() = "video/avi"
    };

    ////////////////////////////////////////////////////////////////////////

    abstract fun getValue(): Set<String>

    abstract fun getKey(): String
}