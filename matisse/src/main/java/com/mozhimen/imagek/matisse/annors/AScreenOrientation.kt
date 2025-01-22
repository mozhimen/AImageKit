package com.mozhimen.imagek.matisse.annors

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi

/**
 * @ClassName AScreenOrientation
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/14
 * @Version 1.0
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@IntDef(
    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_USER, ActivityInfo.SCREEN_ORIENTATION_BEHIND,
    ActivityInfo.SCREEN_ORIENTATION_SENSOR, ActivityInfo.SCREEN_ORIENTATION_NOSENSOR, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
    ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
    ActivityInfo.SCREEN_ORIENTATION_FULL_USER, ActivityInfo.SCREEN_ORIENTATION_LOCKED
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
internal annotation class AScreenOrientation
