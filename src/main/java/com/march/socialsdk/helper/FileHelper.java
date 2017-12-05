package com.march.socialsdk.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * CreateAt : 2016/12/22
 * Describe :  文件帮助
 *
 * @author chendong
 */

public class FileHelper {

    public static final String TAG = FileHelper.class.getSimpleName();

    public static final String POINT_GIF = ".gif";
    public static final String POINT_JPG = ".jpg";
    public static final String POINT_JPEG = ".jpeg";
    public static final String POINT_PNG = ".png";


    /**
     * 下载文件
     *
     * @param httpUrl 下载的链接
     * @param path    本地存储路径
     * @throws IOException
     */
    public static void downloadFileSync(String httpUrl, String path) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //连接地址
            URL url = new URL(httpUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() != 200)
                return;
            final long lengthOfFile = httpURLConnection.getContentLength();
            bis = new BufferedInputStream(httpURLConnection.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(path));
            int size;
            long total = 0;
            byte[] temp = new byte[8 * 1024];
            while ((size = bis.read(temp, 0, temp.length)) != -1) {
                total += size;
                bos.write(temp, 0, size);
            }
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            CommonHelper.closeStream(bos, bis);
        }
    }


    /**
     * 从文件中获取输出流
     *
     * @param path 路径
     * @return 输出流
     */
    public static ByteArrayOutputStream getOutputStreamFromFile(String path) {
        if (!FileHelper.isExist(path))
            return null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(path);
            baos = new ByteArrayOutputStream();
            byte[] b = new byte[8 * 1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                baos.write(b, 0, n);
            }
            return baos;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonHelper.closeStream(fis, baos);
        }
        return baos;
    }

    /**
     * 文件后缀
     *
     * @param path 路径
     * @return 后缀名
     */
    public static String getSuffix(String path) {
        return path.substring(path.lastIndexOf("."), path.length());
    }

    /**
     * @param path 路径
     * @return 是否是 gif 文件
     */
    public static boolean isGifFile(String path) {
        return path.toLowerCase().endsWith(POINT_GIF);
    }

    /**
     * @param path 路径
     * @return 是不是 jpg || png
     */
    public static boolean isJpgPngFile(String path) {
        return isJpgFile(path) || isPngFile(path);
    }

    /**
     * @param path 路径
     * @return 是不是 jpg 文件
     */
    public static boolean isJpgFile(String path) {
        return path.toLowerCase().endsWith(POINT_JPG) || path.toLowerCase().endsWith(POINT_JPEG);
    }

    /**
     * @param path 路径
     * @return 是不是 png 文件
     */
    public static boolean isPngFile(String path) {
        return path.toLowerCase().endsWith(POINT_PNG);
    }

    /**
     * @param path 路径
     * @return 是不是 图片 文件
     */
    public static boolean isPicFile(String path) {
        return isJpgPngFile(path) || isGifFile(path);
    }

    /**
     * @param path 路径
     * @return 文件是否存在
     */
    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        File file = new File(path);
        return file.exists() && file.length() > 0;
    }

    /**
     * @param file 文件
     * @return 文件是否存在
     */
    public static boolean isExist(File file) {
        return file != null && isExist(file.getAbsolutePath());
    }

    /**
     * @param path 路径
     * @return 是不是 http 路径
     */
    public static boolean isHttpPath(String path) {
        return path.toLowerCase().startsWith("http");
    }

    /**
     * 网络路径映射本地路径
     *
     * @param url 网络路径
     * @return 映射的本地路径
     */
    public static String mapUrl2LocalPath(String url) {
        // 映射文件名
        String fileName = CommonHelper.getMD5(url) + FileHelper.getSuffix(url);
        File saveFile = new File(SocialSdk.getConfig().getShareCacheDirPath(), fileName);
        return saveFile.getAbsolutePath();
    }


    /**
     * 将资源图片映射到本地文件存储，同一张图片不必重复decode
     *
     * @param context ctx
     * @param resId   资源ID
     * @return 路径
     */
    public static String mapResId2LocalPath(Context context, int resId) {
        String fileName = CommonHelper.getMD5(resId + "") + FileHelper.POINT_PNG;
        File saveFile = new File(SocialSdk.getConfig().getShareCacheDirPath(), fileName);
        if (saveFile.exists())
            return saveFile.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(saveFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            BitmapHelper.recyclerBitmaps(bitmap);
        }
        return saveFile.getAbsolutePath();
    }

    /**
     * 生成不重复文件名
     *
     * @param suffix 后缀
     * @return 文件名
     */
    public static String getFileUid(String suffix) {
        String format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(System.currentTimeMillis());
        return format + suffix;
    }
}


