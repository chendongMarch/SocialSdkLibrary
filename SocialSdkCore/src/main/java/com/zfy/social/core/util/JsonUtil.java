package com.zfy.social.core.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.adapter.IJsonAdapter;
import com.zfy.social.core.exception.SocialError;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * CreateAt : 2016/12/3
 * Describe : 外部注入json模块
 *
 * @author chendong
 */
public class JsonUtil {

    public static final String TAG = JsonUtil.class.getSimpleName();

    public interface Callback<T> {

        void onSuccess(@NonNull T object);

        void onFailure(SocialError e);
    }


    public static <T> T getObject(String jsonString, Class<T> cls) {
        IJsonAdapter jsonAdapter = SocialSdk.getJsonAdapter();
        if (jsonAdapter != null) {
            try {
                return jsonAdapter.toObj(jsonString, cls);
            } catch (Exception e) {
                SocialUtil.t(TAG, e);
            }
        }
        return null;
    }

    public static String getObject2Json(Object object) {
        IJsonAdapter jsonAdapter = SocialSdk.getJsonAdapter();
        try {
            return jsonAdapter.toJson(object);
        } catch (Exception e) {
            SocialUtil.t(TAG, e);
        }
        return null;
    }


    public static <T> void startJsonRequest(final String url, final Class<T> clz, final Callback<T> callback) {
        Task.callInBackground(new Callable<T>() {
            @Override
            public T call() {
                T object = null;
                String json = SocialSdk.getRequestAdapter().getJson(url);
                if (!TextUtils.isEmpty(json)) {
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
                    callback.onFailure(SocialError.make(SocialError.CODE_REQUEST_ERROR, "", task.getError()));
                } else if (task.getResult() == null) {
                    callback.onFailure(SocialError.make(SocialError.CODE_PARSE_ERROR, "json 无法解析"));
                } else {
                    callback.onFailure(SocialError.make(SocialError.CODE_REQUEST_ERROR, "unKnow error"));
                }
                return true;
            }
        }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Boolean, Object>() {
            @Override
            public Object then(Task<Boolean> task) throws Exception {
                if(task.isFaulted()) {
                    callback.onFailure(SocialError.make(SocialError.CODE_REQUEST_ERROR, "未 handle 的错误"));
                }
                return null;
            }
        });
    }
}
