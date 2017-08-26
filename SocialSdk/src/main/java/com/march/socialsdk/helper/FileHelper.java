package com.march.socialsdk.helper;

import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CreateAt : 2016/12/22
 * Describe :  文件帮助
 *
 * @author chendong
 */

public class FileHelper {

    public static final String TAG = FileHelper.class.getSimpleName();

    public static final String POINT_GIF  = ".gif";
    public static final String POINT_JPG  = ".jpg";
    public static final String POINT_JPEG = ".jpeg";
    public static final String POINT_PNG  = ".png";


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
            OtherHelper.closeStream(bos, bis);
        }

    }

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
            OtherHelper.closeStream(fis, baos);
        }
        return baos;
    }

    public static String getSuffix(String path) {
        return path.substring(path.lastIndexOf("."), path.length());
    }

    public static boolean isGifFile(String path) {
        return path.toLowerCase().endsWith(POINT_GIF);
    }

    public static boolean isJpgPngFile(String path) {
        return isJpgFile(path) || isPngFile(path);
    }

    public static boolean isJpgFile(String path) {
        return path.toLowerCase().endsWith(POINT_JPG) || path.toLowerCase().endsWith(POINT_JPEG);
    }

    public static boolean isPngFile(String path) {
        return path.toLowerCase().endsWith(POINT_PNG);
    }

    public static boolean isPicFile(String path) {
        return isJpgPngFile(path) || isGifFile(path);
    }

    public static boolean isExist(String thumbImagePath) {
        if (TextUtils.isEmpty(thumbImagePath)) return false;
        File file = new File(thumbImagePath);
        return file.exists() && file.length() > 0;
    }

    public static boolean isHttpPath(String path) {
        return path.toLowerCase().startsWith("http");
    }


    public static String getShareUrlMapLocalPath(String url) {
        // 映射文件名
        String fileName = OtherHelper.getMD5(url) + FileHelper.getSuffix(url);
        File saveFile = new File(SocialSdk.getConfig().getShareCacheDirPath(), fileName);
        return saveFile.getAbsolutePath();
    }

    public static String getFileUid(String suffix) {
        String format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date(System.currentTimeMillis()));
        return format + suffix;
    }
}


