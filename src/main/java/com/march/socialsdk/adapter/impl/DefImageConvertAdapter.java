package com.march.socialsdk.adapter.impl;

import com.march.socialsdk.adapter.IImageConvertAdapter;
import com.march.socialsdk.helper.CommonHelper;
import com.march.socialsdk.helper.FileHelper;
import com.march.socialsdk.helper.PlatformLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * CreateAt : 2017/12/5
 * Describe :
 *
 * @author chendong
 */
public class DefImageConvertAdapter implements IImageConvertAdapter {

    @Override
    public File convertImage(String url) {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setReadTimeout(3_000);
            conn.setConnectTimeout(3_000);
            conn.setDoOutput(false);
            conn.setDoInput(true);
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 发起连接
            conn.connect();
            return saveInputStreamToFile(conn.getInputStream(), url);
        } catch (IOException e) {
            PlatformLog.t(e);
        }
        return null;
    }


    public File saveInputStreamToFile(InputStream is, String url) {
        File file = new File(FileHelper.mapUrl2LocalPath(System.currentTimeMillis() + url));
        if (file.exists())
            return file;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        byte[] bs;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bs = new byte[1024 * 10];
            int len;
            while ((len = bis.read(bs)) != -1) {
                bos.write(bs, 0, len);
                bos.flush();
            }
            bis.close();
            bos.close();
            bs = null;
        } catch (Exception e) {
            PlatformLog.t(e);
            return null;
        } finally {
            CommonHelper.closeStream(bis, bos);
            bs = null;
        }
        return file;
    }
}
