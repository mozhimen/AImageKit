package q.rorbin.fastimagesize.impls;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import q.rorbin.fastimagesize.commons.ImageSize;
import q.rorbin.fastimagesize.cons.ImageType;
import q.rorbin.fastimagesize.utils.ByteArrayUtil;

/**
 * @ClassName RiffImageSize
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/28
 * @Version 1.0
 */
public class WebpImageSize extends ImageSize {
    @Override
    public int getSupportImageType() {
        return ImageType.RIFF;
    }

    @Override
    public boolean isSupportImageType(byte[] buffer) {
        if (buffer == null || buffer.length <= 0)
            return false;
        //PIFF 52 49 46 46
        if (buffer.length >= 4) {
            return buffer[0] == (byte) 0x52 &&
                    buffer[1] == (byte) 0x49 &&
                    buffer[2] == (byte) 0x46 &&
                    buffer[3] == (byte) 0x46;
        }
        return false;
    }

    /**
     * 52 49 46 46 文件签名（Signature）：WebP图像文件的头部以一个固定的签名开始，用于标识文件格式。WebP图像文件的签名是 "RIFF"（0x52 0x49 0x46 0x46）。
     * b4 62 02 00 文件大小（File Size）：占据四个字节，表示整个文件的大小，包括头部信息和图像数据
     * 57 45 42 50 文件类型（File Type）：占据四个字节，用于标识文件的类型。对于WebP图像文件，文件类型是 "WEBP"（0x57 0x45 0x42 0x50）。
     * 56 50 38 4c VP8/VP8L标识符（VP8/VL8L Identifier）：占据四个字节，用于标识WebP图像的压缩方式。如果标识符是 "VP8 "（0x56 0x50 0x38 0x20），表示使用VP8压缩算法。如果标识符是 "VP8L"（0x56 0x50 0x38 0x4C），表示使用VP8L（Lossless）压缩算法。
     * a7 62 图像宽度（Image Width）：占据两个字节，表示图像的宽度（以像素为单位）。
     * 02 00 图像高度（Image Height）：占据两个字节，表示图像的高度（以像素为单位）。
     */
    @Override
    public int[] getImageSize(InputStream stream, byte[] buffer) throws IOException {
        int[] size = new int[3];
        if (buffer == null || buffer.length <= 0)
            return size;
//        buffer = ByteArrayUtil.findSizeBytes(stream, buffer, 16, 4);
//        size[0] = ByteBuffer.wrap(buffer, 0, 2).getInt();
//        size[1] = ByteBuffer.wrap(buffer, 2, 2).getInt();
        size[0] = ((int) buffer[27] & 0xff) << 8
                | ((int) buffer[26] & 0xff);
        size[1] = ((int) buffer[29] & 0xff) << 8
                | ((int) buffer[28] & 0xff);
        size[2] = getSupportImageType();
        return size;
    }
}
