package com.march.socialsdk.adapter.impl;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.march.socialsdk.adapter.IRequestAdapter;
import com.march.socialsdk.util.FileUtil;
import com.march.socialsdk.util.SocialLogUtil;
import com.march.socialsdk.util.StreamUtil;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * CreateAt : 2017/12/8
 * Describe : 请求 adapter
 *
 * @author chendong
 */
public class DefaultRequestAdapter implements IRequestAdapter {

    private HttpURLConnection mConnection;

    @Override
    public File getFile(String url) {
        if(TextUtils.isEmpty(url) || !url.startsWith("http"))
            return null;
        File file = null;
        try {
            file = new File(FileUtil.mapUrl2LocalPath(url));
            if (!FileUtil.isExist(file)) {
                return StreamUtil.saveStreamToFile(file, openStream(url, isHttps(url)));
            }
        } catch (Exception e) {
            SocialLogUtil.e(e);
        } finally {
            close();
        }
        return file;
    }

    @Override
    public String getJson(String url) {
        if(TextUtils.isEmpty(url) || !url.startsWith("http"))
            return null;
        try {
            return StreamUtil.saveStreamToString(openStream(url, isHttps(url)));
        } catch (Exception e) {
            SocialLogUtil.e(e);
        } finally {
            close();
        }
        return null;
    }

    @Override
    public String postData(String url, Map<String, String> params, String fileKey, String filePath) {
        throw new RuntimeException("如果想要支持 openApi, 则需要实现该方法，由于使用 HttpUrlConn 实现太复杂，建议使用 OkHttpClient 实现");
    }

    private boolean isHttps(String url) {
        return url.startsWith("https");
    }

    // 关闭连接
    private void close() {
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }

    // 开启连接，打开流
    private InputStream openStream(String url, boolean isHttps) throws Exception {
        mConnection = (HttpURLConnection) new URL(url).openConnection();
        if (isHttps) {
            initHttpsConnection(mConnection);
        }
        return StreamUtil.openGetHttpStream(mConnection);
    }

    /**
     * 针对 https 连接配置
     *
     * @param conn 连接
     * @throws Exception e
     */
    private void initHttpsConnection(HttpURLConnection conn) throws Exception {
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) conn;
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
        httpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
        httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return hostname != null;
            }
        });
    }


    @SuppressLint("TrustAllX509TrustManager")
    private class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
