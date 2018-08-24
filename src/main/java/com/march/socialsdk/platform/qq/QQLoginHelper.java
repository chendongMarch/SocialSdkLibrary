package com.march.socialsdk.platform.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.model.token.AccessToken;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.platform.qq.model.QQAccessToken;
import com.march.socialsdk.platform.qq.model.QQUser;
import com.march.socialsdk.util.JsonUtil;
import com.march.socialsdk.util.SocialLogUtil;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * CreateAt : 2016/12/6
 * Describe : qq 登录辅助
 *
 * @author chendong
 */

class QQLoginHelper {

    public static final String TAG = QQLoginHelper.class.getSimpleName();

    private int loginType;
    private Tencent mTencentApi;
    private WeakReference<Activity> mActivityRef;
    private OnLoginListener onLoginListener;
    private LoginUiListener loginUiListener;


    QQLoginHelper(Activity activity, Tencent mTencentApi, OnLoginListener onQQLoginListener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mTencentApi = mTencentApi;
        this.onLoginListener = onQQLoginListener;
        this.loginType = Target.LOGIN_QQ;
    }

    private Context getContext() {
        return mActivityRef.get().getApplicationContext();
    }

    public QQAccessToken getToken() {
        return AccessToken.getToken(getContext(), AccessToken.QQ_TOKEN_KEY, QQAccessToken.class);
    }

    // 接受登录结果
    void handleResultData(Intent data) {
        Tencent.handleResultData(data, this.loginUiListener);
    }

    // 登录
    public void login() {
        QQAccessToken qqToken = getToken();
        if (qqToken != null) {
            mTencentApi.setAccessToken(qqToken.getAccess_token(), qqToken.getExpires_in() + "");
            mTencentApi.setOpenId(qqToken.getOpenid());
            if (mTencentApi.isSessionValid()) {
                getUserInfo(qqToken);
            } else {
                loginUiListener = new LoginUiListener();
                mTencentApi.login(mActivityRef.get(), "all", loginUiListener);
            }
        } else {
            loginUiListener = new LoginUiListener();
            mTencentApi.login(mActivityRef.get(), "all", loginUiListener);
        }
    }

    // 登录监听包装类
    private class LoginUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            JSONObject jsonResponse = (JSONObject) o;
            QQAccessToken qqToken = JsonUtil.getObject(jsonResponse.toString(), QQAccessToken.class);
            SocialLogUtil.e(TAG, "获取到 qq token = ", qqToken);
            if (qqToken == null) {
                onLoginListener.onFailure(new SocialError(SocialError.CODE_PARSE_ERROR, TAG + "#LoginUiListener#qq token is null, data = " + qqToken));
                return;
            }
            // 保存token
            AccessToken.saveToken(getContext(), AccessToken.QQ_TOKEN_KEY, qqToken);
            mTencentApi.setAccessToken(qqToken.getAccess_token(), qqToken.getExpires_in() + "");
            mTencentApi.setOpenId(qqToken.getOpenid());
            getUserInfo(qqToken);
        }


        @Override
        public void onError(UiError e) {
            onLoginListener.onFailure(new SocialError(SocialError.CODE_SDK_ERROR, TAG + "#LoginUiListener#获取用户信息失败 " + QQPlatform.parseUiError(e)));
        }

        @Override
        public void onCancel() {
            onLoginListener.onCancel();
        }
    }

    // 获取用户信息
    private void getUserInfo(final QQAccessToken qqToken) {
        UserInfo info = new UserInfo(getContext(), mTencentApi.getQQToken());
        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                QQUser qqUserInfo = JsonUtil.getObject(object.toString(), QQUser.class);
                if (qqUserInfo == null) {
                    if (onLoginListener != null) {
                        onLoginListener.onFailure(new SocialError(SocialError.CODE_PARSE_ERROR, TAG + "#getUserInfo#解析 qq user 错误, data = " + object.toString()));
                    }
                } else {
                    qqUserInfo.setOpenId(mTencentApi.getOpenId());
                    if (onLoginListener != null) {
                        onLoginListener.onSuccess(new LoginResult(loginType, qqUserInfo, qqToken));
                    }
                }
            }

            @Override
            public void onError(UiError e) {
                onLoginListener.onFailure(new SocialError(SocialError.CODE_SDK_ERROR, TAG + "#getUserInfo#qq获取用户信息失败  " + QQPlatform.parseUiError(e)));
            }

            @Override
            public void onCancel() {
                onLoginListener.onCancel();
            }

        });
    }
}
