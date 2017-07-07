package com.march.socialsdk.helper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * CreateAt : 2016/12/3
 * Describe : wx发送http请求。获取token
 *
 * @author chendong
 */
public class HttpsRequestHelper {

    public static void getHttps(final String url, final OnResultListener onResultListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());

                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                    HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
                    HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null)
                        sb.append(line);

                    return sb.toString();

                } catch (Exception e) {
                    if (onResultListener != null) {
                        onResultListener.onFailure(e);
                    }
                    Log.e(this.getClass().getName(), e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String str) {
                super.onPostExecute(str);
                if (str != null && onResultListener != null) {
                    onResultListener.onSuccess(str);
                }
            }
        }.execute();
    }

    public interface OnResultListener {
        void onSuccess(String result);

        void onFailure(Exception exception);
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
