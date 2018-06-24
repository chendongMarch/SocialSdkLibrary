package com.march.socialsdk.platform.weibo;

import android.app.Activity;
import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareObj;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * CreateAt : 2018/6/24
 * Describe : 使用 openApi 分享动图
 *
 * @author chendong
 */
public class OpenApiShareHelper {

    private WbLoginHelper   mWbLoginHelper;
    private OnShareListener mOnShareListener;

    OpenApiShareHelper(WbLoginHelper wbLoginHelper, OnShareListener onShareListener) {
        mWbLoginHelper = wbLoginHelper;
        mOnShareListener = onShareListener;
    }

    public void post(Activity activity, final ShareObj obj) {
        mWbLoginHelper.justAuth(activity, new WbAuthListenerImpl() {
            @Override
            public void onSuccess(final Oauth2AccessToken token) {
                Task.callInBackground(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Map<String, String> params = new HashMap<>();
                        params.put("access_token", token.getToken());
                        params.put("status", obj.getSummary());
                        return SocialSdk.getRequestAdapter().postData("https://api.weibo.com/2/statuses/share.json", params, "pic", obj.getThumbImagePath());
                    }
                }).continueWith(new Continuation<String, Boolean>() {
                    @Override
                    public Boolean then(Task<String> task) throws Exception {
                        if (task.isFaulted() || TextUtils.isEmpty(task.getResult())) {
                            throw new SocialError("open api 分享失败 " + task.getResult(), task.getError());
                        } else {
                            JSONObject jsonObject = new JSONObject(task.getResult());
                            if (jsonObject.has("id") && jsonObject.get("id") != null) {
                                mOnShareListener.onSuccess();
                                return true;
                            } else {
                                throw new SocialError("open api 分享失败 " + task.getResult());
                            }
                        }
                    }
                }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Boolean, Boolean>() {
                    @Override
                    public Boolean then(Task<Boolean> task) {
                        if (task.isFaulted()) {
                            task.getError().printStackTrace();
                            mOnShareListener.onFailure(new SocialError("open api 分享失败", task.getError()));
                        }
                        return true;
                    }
                }, Task.UI_THREAD_EXECUTOR);
            }
        });
    }

    class WbAuthListenerImpl implements WbAuthListener {
        @Override
        public void onSuccess(Oauth2AccessToken token) {
        }

        @Override
        public void cancel() {
            mOnShareListener.onCancel();
        }

        @Override
        public void onFailure(WbConnectErrorMessage msg) {
            mOnShareListener.onFailure(new SocialError("wb auth fail" + msg.getErrorCode() + " " + msg.getErrorMessage()));
        }
    }
}
