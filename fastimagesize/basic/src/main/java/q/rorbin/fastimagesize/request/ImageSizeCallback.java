package q.rorbin.fastimagesize.request;

import androidx.annotation.Nullable;

import q.rorbin.fastimagesize.cons.ImageType;

/**
 * @author chqiu
 *         Email:qstumn@163.com
 */

public interface ImageSizeCallback {
    /**
     * @param size width:int[0]   height:int[1]   type:int[2] {@link ImageType}
     */
    void onSizeReady(@Nullable int[] size);
}
