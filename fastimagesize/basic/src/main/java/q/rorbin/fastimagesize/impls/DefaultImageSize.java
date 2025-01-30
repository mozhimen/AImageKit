package q.rorbin.fastimagesize.impls;

import java.io.IOException;
import java.io.InputStream;

import q.rorbin.fastimagesize.commons.ImageSize;
import q.rorbin.fastimagesize.cons.ImageType;

/**
 * @author chqiu
 *         Email:qstumn@163.com
 */

public class DefaultImageSize extends ImageSize {
    @Override
    public int getSupportImageType() {
        return ImageType.NULL;
    }

    @Override
    public boolean isSupportImageType(byte[] buffer) {
        return false;
    }

    @Override
    public int[] getImageSize(InputStream stream, byte[] buffer) throws IOException {
        return new int[3];
    }
}
