package com.march.socialsdk.adapter.impl;

import android.text.TextUtils;

import com.march.socialsdk.adapter.IReqAdapter;
import com.march.socialsdk.helper.HttpsRequestHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import bolts.Capture;
import bolts.Continuation;
import bolts.Task;

/**
 * CreateAt : 2017/11/25
 * Describe :
 *
 * @author chendong
 */
public class DefReqAdapter implements IReqAdapter {

    private boolean isInitSuccess;

    public DefReqAdapter() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
            isInitSuccess = false;
        }
    }




    @Override
    public void sendRequest(final String url, Map<String, String> params, final OnResultListener listener) {
        if (!isInitSuccess)
            return;
        final Capture<HttpsURLConnection> capture = new Capture<>();
        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
                capture.set(conn);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                if (capture.get() != null) {
                    capture.get().disconnect();
                }
                if (listener != null) {
                    if (task.isFaulted() || TextUtils.isEmpty(task.getResult())) {
                        listener.onFailure(task.getError());
                    } else {
                        listener.onSuccess(task.getResult());
                    }
                }
                return null;
            }
        },Task.UI_THREAD_EXECUTOR);
    }


    private static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    private static class MyTrustManager implements X509TrustManager {

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
