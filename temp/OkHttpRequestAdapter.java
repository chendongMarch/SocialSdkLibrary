package com.babypat;

import android.text.TextUtils;

import com.march.socialsdk.adapter.impl.RequestAdapterImpl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * CreateAt : 2018/6/24
 * Describe :
 *
 * @author chendong
 */
public class OkHttpRequestAdapter extends RequestAdapterImpl {

    private OkHttpClient mOkHttpClient;

    public OkHttpRequestAdapter() {
        mOkHttpClient = buildOkHttpClient();
    }

    private OkHttpClient buildOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 连接超时
        builder.connectTimeout(5 * 1000, TimeUnit.MILLISECONDS);
        // 读超时
        builder.readTimeout(5 * 1000, TimeUnit.MILLISECONDS);
        // 写超时
        builder.writeTimeout(5 * 1000, TimeUnit.MILLISECONDS);
        // 失败后重试
        builder.retryOnConnectionFailure(true);
        return builder.build();
    }


    // 借助 open api 提交图片
    @Override
    public String postData(String url, Map<String, String> params, String fileKey, String filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            builder.addFormDataPart(fileKey, file.getName(), body);
            builder.setType(MultipartBody.FORM);
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key));
            }
            MultipartBody multipartBody = builder.build();
            Request request = new Request.Builder().url(url).post(multipartBody).build();
            try {
                Response execute = mOkHttpClient.newCall(request).execute();
                return execute.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
