package com.march.socialsdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * CreateAt : 2016/12/22
 * Describe : Bitmap 辅助 32kb = 32768
 * 获取指定大小图片的流程
 * 1. decode options outWidth,outHeight
 * 2. 利用bitmap的宽高，通过 w*h < maxSize 大致计算目标图片宽高,
 *    这里 maxSize 指的是 byte[] length，利用宽高计算略有差异，
 *    这样做有两个好处，一个是不需要将整个图片 decode 到内存，只拿到 32kb 多一点的图片，
 *    第二个是可以尽快接近目标图片的大小,减少后续细节调整的次数
 *    经过此步之后拿到的 bitmap 会稍微大于 32kb
 * 3. 细节调整，利用 matrix.scale 每次缩小为原来的 0.9，循环接近目标大小
 *
 * @author chendong
 */
public class BitmapUtil {

    public static final String TAG = BitmapUtil.class.getSimpleName();

    static class Size {

        Size() {
        }

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        int width;
        int height;
    }

    /**
     * 根据kb计算缩放后的大约宽高
     *
     * @param originSize  图片原始宽高
     * @param maxSize byte length
     * @return 大小
     */
    private static Size calculateSize(Size originSize, int maxSize) {
        int bw = originSize.width;
        int bh = originSize.height;
        Size size = new Size();
        // 如果本身已经小于，就直接返回
        if (bw * bh <= maxSize) {
            size.width = bw;
            size.height = bh;
            return size;
        }
        // 拿到大于1的宽高比
        boolean isHeightLong = true;
        float bitRatio = bh * 1f / bw;
        if (bitRatio < 1) {
            bitRatio = bw * 1f / bh;
            isHeightLong = false;
        }
        // 较长边 = 较短边 * 比例(>1)
        // maxSize = 较短边 * 较长边 = 较短边 * 较短边 * 比例(>1)
        // 由此计算短边应该为 较短边 = sqrt(maxSize/比例(>1))
        int thumbShort = (int) Math.sqrt(maxSize / bitRatio);
        // 较长边 = 较短边 * 比例(>1)
        int thumbLong = (int) (thumbShort * bitRatio);
        if (isHeightLong) {
            size.height = thumbLong;
            size.width = thumbShort;
        } else {
            size.width = thumbLong;
            size.height = thumbShort;
        }
        return size;
    }

    /**
     * 获取图片大小
     * @param filePath 路径
     * @return Size
     */
    private static Size getBitmapSize(String filePath) {
        // 仅获取宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 该属性设置为 true 只会加载图片的边框进来，并不会加载图片具体的像素点
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // 获得原图的宽和高
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        return new Size(outWidth, outHeight);
    }

    /**
     * 使用 path decode 出来一个差不多大小的，此时因为图片质量的关系，可能大于kbNum
     *
     * @param filePath path
     * @param maxSize  byte
     * @return bitmap
     */
    private static Bitmap getMaxSizeBitmap(String filePath, int maxSize) {
        Size originSize = getBitmapSize(filePath);
        SocialLogUtil.e(TAG, "原始图片大小 = " + originSize.width + " * " + originSize.height);
        int sampleSize = 0;
        // 我们对较小的图片不进行采样，因为采样只是尽量接近 32k 和避免占用大量内存
        // 对较小图片进行采样会导致图片更模糊，所以对不大的图片，直接走后面的细节调整
        if (originSize.height * originSize.width < 400 * 400) {
            sampleSize = 1;
        } else {
            Size size = calculateSize(originSize, maxSize * 5);
            SocialLogUtil.e(TAG, "目标图片大小 = " + size.width + " * " + size.height);
            while (sampleSize == 0
                    || originSize.height / sampleSize > size.height
                    || originSize.width / sampleSize > size.width) {
                sampleSize += 2;
            }
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        SocialLogUtil.e(TAG, "sample size = " + sampleSize + " 采样后 bitmap大小 = " + bitmap.getByteCount());
        return bitmap;
    }


    /**
     * 创建指定大小的bitmap的byte流，大小 <= maxSize
     *
     * @param srcBitmap bitmap
     * @param maxSize   kb,example 32kb
     * @return byte流
     */
    private static byte[] getStaticSizeBitmapByteByBitmap(Bitmap srcBitmap, int maxSize, Bitmap.CompressFormat format) {
        // 首先进行一次大范围的压缩
        Bitmap tempBitmap;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // 设置矩阵数据
        Matrix matrix = new Matrix();
        srcBitmap.compress(format, 100, output);
        // 如果进行了上面的压缩后，依旧大于32K，就进行小范围的微调压缩
        byte[] bytes = output.toByteArray();
        SocialLogUtil.e(TAG, "开始循环压缩之前 bytes = " + bytes.length);
        while (bytes.length > maxSize) {
            matrix.setScale(0.9f, 0.9f);//每次缩小 1/10
            tempBitmap = srcBitmap;
            srcBitmap = Bitmap.createBitmap(
                    tempBitmap, 0, 0,
                    tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
            recyclerBitmaps(tempBitmap);
            output.reset();
            srcBitmap.compress(format, 100, output);
            bytes = output.toByteArray();
            SocialLogUtil.e(TAG, "压缩一次 bytes = " + bytes.length);
        }
        SocialLogUtil.e(TAG, "压缩后的图片输出大小 bytes = " + bytes.length);
        recyclerBitmaps(srcBitmap);
        return bytes;
    }

    /**
     * 根据路径获取指定大小的图片
     * @param path 路径
     * @param maxSize 最大尺寸
     * @return byte[]
     */
    public static byte[] getStaticSizeBitmapByteByPath(final String path, final int maxSize) {
        Bitmap srcBitmap = getMaxSizeBitmap(path, maxSize);
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (FileUtil.isPngFile(path)) format = Bitmap.CompressFormat.PNG;
        return getStaticSizeBitmapByteByBitmap(srcBitmap, maxSize, format);
    }

    public static Task<byte[]> getStaticSizeBitmapByteByPathTask(final String path, final int maxSize) {
        return Task.callInBackground(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                return getStaticSizeBitmapByteByPath(path, maxSize);
            }
        });
    }

    public static void recyclerBitmaps(Bitmap... bitmaps) {
        try {
            for (Bitmap bitmap : bitmaps) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
