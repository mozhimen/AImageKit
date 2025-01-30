package q.rorbin.fastimagesize.helpers;

import android.view.View;

import q.rorbin.fastimagesize.FastImageSize;
import q.rorbin.fastimagesize.net.DefaultInputStreamProvider;
import q.rorbin.fastimagesize.net.InputStreamProvider;
import q.rorbin.fastimagesize.cons.ImageType;
import q.rorbin.fastimagesize.request.ImageSizeCallback;

/**
 * @author chqiu
 * Email:qstumn@163.com
 */

public class FastImageLoader {
    public InputStreamProvider mProvider;
    public String mImagePath;
    public int mOverrideSize;
    public boolean mUseCache;
    private FastImageSize mFastImageSize;

    private FastImageLoader(String imagePath, FastImageSize fastImageSize) {
        mFastImageSize = fastImageSize;
        mImagePath = imagePath;
        mProvider = DefaultInputStreamProvider.getInstance();
        mOverrideSize = -1;
        mUseCache = true;
    }

    public static FastImageLoader newInstance(String imagePath, FastImageSize fastImageSize) {
        return new FastImageLoader(imagePath, fastImageSize);
    }

    public FastImageLoader customProvider(InputStreamProvider provider) {
        if (provider == null) {
            throw new IllegalStateException("provider must be not null");
        }
        mProvider = provider;
        return this;
    }

    public FastImageLoader override(int overrideSize) {
        if (overrideSize <= 0) {
            throw new IllegalStateException("overrideSize must be bigger than 0");
        }
        mOverrideSize = overrideSize;
        return this;
    }

    public FastImageLoader setUseCache(boolean useCache) {
        mUseCache = useCache;
        return this;
    }

    /**
     * @return width:int[0]   height:int[1]   type:int[2] {@link ImageType}
     */
    public int[] get() {
        return mFastImageSize.get(this);
    }

    public void get(ImageSizeCallback callback) {
        mFastImageSize.get(callback, this);
    }

    public void into(View view) {
        mFastImageSize.into(view, this);
    }
}
