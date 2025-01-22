package com.mozhimen.imagek.ucrop.annors;

import static com.mozhimen.imagek.ucrop.annors.GestureTypes.ALL;
import static com.mozhimen.imagek.ucrop.annors.GestureTypes.NONE;
import static com.mozhimen.imagek.ucrop.annors.GestureTypes.ROTATE;
import static com.mozhimen.imagek.ucrop.annors.GestureTypes.SCALE;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @ClassName GestureTypes
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/21
 * @Version 1.0
 */
@IntDef({NONE, SCALE, ROTATE, ALL})
@Retention(RetentionPolicy.SOURCE)
public @interface GestureTypes {
    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;
}
