package com.march.socialsdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * CreateAt : 2016/12/22
 * Describe : Bitmap 辅助 32kb = 32768
 *
 * @author chendong
 */

public class BitmapUtils {

    public static final String TAG = BitmapUtils.class.getSimpleName();

    static class Size {
        int width;
        int height;
    }

    /**
     * 创建指定大小的bitmap的byte流，大小 <= maxSize
     *
     * @param srcBitmap bitmap
     * @param maxSize   kb,example 32kb
     * @return byte流
     */
    public static byte[] getStaticSizeBitmapByteByBitmap(Bitmap srcBitmap, int maxSize, Bitmap.CompressFormat format) {
        // 首先进行一次大范围的压缩
        Bitmap tempBitmap;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // 设置矩阵数据
        Matrix matrix = new Matrix();
        srcBitmap.compress(format, 100, output);
        // 如果进行了上面的压缩后，依旧大于32K，就进行小范围的微调压缩
        byte[] bytes = output.toByteArray();
        LogUtils.e(TAG, "压缩之前 = " + bytes.length);
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
            LogUtils.e(TAG, "压缩一次 = " + bytes.length);
        }
        LogUtils.e(TAG, "压缩后的图片输出大小 = " + bytes.length);
        recyclerBitmaps(srcBitmap);
        return bytes;
    }


    /**
     * 根据kb计算缩放后的大约宽高
     *
     * @param bw      当前宽度
     * @param bh      当前高度
     * @param maxSize byte
     * @return 大小
     */
    private static Size calculateSize(int bw, int bh, int maxSize) {
        Size size = new Size();

        if (bw * bh <= maxSize) {
            size.width = bw;
            size.height = bh;
        }

        boolean isHeightLong = true;

        float bitRatio = bh * 1f / bw;
        if (bitRatio < 1) {
            bitRatio = bw * 1f / bh;
            isHeightLong = false;
        }
        // 短边
        int thumbShort = (int) Math.sqrt(maxSize / bitRatio);
        // 长边
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
     * 使用path decode 出来一个差不多大小的，此时因为图片质量的关系，可能大于kbNum
     *
     * @param filePath path
     * @param maxSize  byte
     * @return bitmap
     */
    public static Bitmap getBitmapByPath(String filePath, int maxSize) {
        //第一次采样
        BitmapFactory.Options options = new BitmapFactory.Options();
        //该属性设置为true只会加载图片的边框进来，并不会加载图片具体的像素点
        options.inJustDecodeBounds = true;
        //第一次加载图片，这时只会加载图片的边框进来，并不会加载图片中的像素点
        BitmapFactory.decodeFile(filePath, options);
        //获得原图的宽和高
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        int sampleSize = 0;
        if (options.outHeight * options.outWidth < 400 * 400) {
            sampleSize = 1;
        } else {
            Size size = calculateSize(outWidth, outHeight, maxSize);
            //定义缩放比例
            while (sampleSize == 0 || outHeight / sampleSize > size.height || outWidth / sampleSize > size.width) {
                sampleSize += 2;
            }
        }
        options.inJustDecodeBounds = false;
        //设置缩放比例
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        //加载图片并返回
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        LogUtils.e(TAG, "sample size = " + sampleSize + "  bitmap大小 = " + bitmap.getByteCount());
        return bitmap;
    }


    /**
     * 获取指定size的byte
     *
     * @param path    路径
     * @param maxSize kb
     * @return byte[]
     */
    public static byte[] getStaticSizeBitmapByteByPath(String path, int maxSize) {
        Bitmap srcBitmap = getBitmapByPath(path, maxSize);
//        Bitmap largeRangeScale = getLargeScaleBitmap(srcBitmap, maxSize);
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (FileUtils.isPngFile(path)) format = Bitmap.CompressFormat.PNG;
        return getStaticSizeBitmapByteByBitmap(srcBitmap, maxSize, format);
    }


    public static Task<byte[]> getStaticSizeBitmapByteByPathTask(final String path, final int maxSize) {
        return Task.callInBackground(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                Bitmap srcBitmap = getBitmapByPath(path, maxSize);
                Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                if (FileUtils.isPngFile(path)) format = Bitmap.CompressFormat.PNG;
                return getStaticSizeBitmapByteByBitmap(srcBitmap, maxSize, format);
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
