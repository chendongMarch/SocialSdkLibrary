package com.march.socialsdk.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * CreateAt : 2017/12/8
 * Describe : IO流
 *
 * @author chendong
 */
public class StreamUtil {


    // 关闭流
    public static void closeStream(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 打开一个网络流
     *
     * @param conn 网络连接
     * @return 流
     * @throws IOException error
     */
    public static InputStream openGetHttpStream(HttpURLConnection conn) throws IOException {
        conn.setRequestMethod("GET");
        conn.setReadTimeout(3_000);
        conn.setConnectTimeout(3_000);
        conn.setDoInput(true);
        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        // 发起连接
        conn.connect();
        return conn.getInputStream();
    }


    /**
     * 保存文件到
     *
     * @param file 文件
     * @param is   流
     * @return 文件
     */
    public static File saveStreamToFile(File file, InputStream is) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        byte[] bs;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
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
        } catch (Exception e) {
            SocialLogUtil.t(e);
            return null;
        } finally {
            closeStream(bis, bos);
            bs = null;
        }
        return file;
    }


    /**
     * 从流中读取为字符串
     * @param is 流
     * @return json
     */
    public static String saveStreamToString(InputStream is) {
        BufferedReader br = null;
        String json = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(br);
        }
        return json;
    }
}
