package com.march.socialsdk.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.adapter.IJsonAdapter;
import com.march.socialsdk.exception.SocialException;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * CreateAt : 2016/12/3
 * Describe : 使用外部注入的 json 转换类，减轻类库的依赖
 *
 * @author chendong
 */

public class JsonUtils {

    public static final String TAG = JsonUtils.class.getSimpleName();

    public static <T> T getObject(String jsonString, Class<T> cls) {
        IJsonAdapter jsonAdapter = SocialSdk.getJsonAdapter();
        if (jsonAdapter != null) {
            try {
                return jsonAdapter.toObj(jsonString, cls);
            } catch (Exception e) {
                LogUtils.e(TAG, e);
            }
        }
        return null;
    }

    public static String getObject2Json(Object object) {
        IJsonAdapter jsonAdapter = SocialSdk.getJsonAdapter();
        try {
            return jsonAdapter.toJson(object);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return null;
    }

    public interface Callback<T> {

        void onSuccess(@NonNull T object);

        void onFailure(SocialException e);
    }

    public static <T> void startJsonRequest(final String url, final Class<T> clz, final Callback<T> callback) {
        LogUtils.e("开始请求" + url);
        Task.callInBackground(new Callable<T>() {
            @Override
            public T call() throws Exception {
                T object = null;
                String json = SocialSdk.getRequestAdapter().getJson(url);
                if (!TextUtils.isEmpty(json)) {
                    LogUtils.e("请求结果" + json);
                    object = getObject(json, clz);
                }
                return object;
            }
        }).continueWith(new Continuation<T, Boolean>() {
            @Override
            public Boolean then(Task<T> task) throws Exception {
                if (!task.isFaulted() && task.getResult() != null) {
                    callback.onSuccess(task.getResult());
                } else if (task.isFaulted()) {
                    callback.onFailure(new SocialException("startJsonRequest error", task.getError()));
                } else if (task.getResult() == null) {
                    callback.onFailure(new SocialException("json 无法解析"));
                } else {
                    callback.onFailure(new SocialException("unKnow error"));
                }
                return true;
            }
        }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Boolean, Object>() {
            @Override
            public Object then(Task<Boolean> task) throws Exception {
                if(task.isFaulted()) {
                    callback.onFailure(new SocialException("未 handle 的错误"));
                }
                return null;
            }
        });
    }
}
