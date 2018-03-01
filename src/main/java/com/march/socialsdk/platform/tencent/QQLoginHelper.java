package com.march.socialsdk.platform.tencent;

import android.app.Activity;
import android.content.Intent;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.utils.TokenStoreUtils;
import com.march.socialsdk.utils.JsonUtils;
import com.march.socialsdk.utils.LogUtils;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.model.token.QQAccessToken;
import com.march.socialsdk.model.user.QQUser;
import com.march.socialsdk.platform.Target;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * CreateAt : 2016/12/6
 * Describe : qq 登录辅助
 *
 * @author chendong
 */

public class QQLoginHelper {

    public static final String TAG = QQLoginHelper.class.getSimpleName();

    private int loginType;
    private Tencent mTencentApi;
    private Activity activity;
    private OnLoginListener onLoginListener;
    private LoginUiListener loginUiListener;


    QQLoginHelper(Activity activity, Tencent mTencentApi, OnLoginListener onQQLoginListener) {
        this.activity = activity;
        this.mTencentApi = mTencentApi;
        this.onLoginListener = onQQLoginListener;
        this.loginType = Target.LOGIN_QQ;
    }


    // 接受登录结果
    void handleResultData(Intent data) {
        Tencent.handleResultData(data, this.loginUiListener);
    }

    // 登录
    public void login() {
        QQAccessToken qqToken = TokenStoreUtils.getQQToken(activity);
        if (qqToken != null) {
            mTencentApi.setAccessToken(qqToken.getAccess_token(), qqToken.getExpires_in() + "");
            mTencentApi.setOpenId(qqToken.getOpenid());
            if (mTencentApi.isSessionValid()) {
                getUserInfo(qqToken);
            } else {
                loginUiListener = new LoginUiListener();
                mTencentApi.login(activity, "all", loginUiListener);
            }
        } else {
            loginUiListener = new LoginUiListener();
            mTencentApi.login(activity, "all", loginUiListener);
        }
    }

    // 登录监听包装类
    private class LoginUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            JSONObject jsonResponse = (JSONObject) o;
            QQAccessToken qqToken = JsonUtils.getObject(jsonResponse.toString(), QQAccessToken.class);
            LogUtils.e(TAG, "获取到 qq token = " + qqToken.toString());
            // 保存token
            TokenStoreUtils.saveQQToken(activity, qqToken);

            mTencentApi.setAccessToken(qqToken.getAccess_token(), qqToken.getExpires_in() + "");
            mTencentApi.setOpenId(qqToken.getOpenid());

            getUserInfo(qqToken);
        }


        @Override
        public void onError(UiError e) {
            onLoginListener.onFailure(new SocialError("qq,获取用户信息失败 " + parseUiError(e)));
        }

        @Override
        public void onCancel() {
            onLoginListener.onCancel();
        }
    }

    // 获取用户信息
    private void getUserInfo(final QQAccessToken qqToken) {
        UserInfo info = new UserInfo(activity, mTencentApi.getQQToken());
        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                LogUtils.e(TAG, "qq 获取到用户信息 = " + object);
                QQUser qqUserInfo = JsonUtils.getObject(object.toString(), QQUser.class);
                if(qqUserInfo == null){
                    if (onLoginListener != null) {
                        onLoginListener.onFailure(new SocialError("解析 qq user 错误"));
                    }
                }else {
                    qqUserInfo.setOpenId(mTencentApi.getOpenId());
                    if (onLoginListener != null) {
                        onLoginListener.onSuccess(new LoginResult(loginType, qqUserInfo, qqToken));
                    }
                }
            }

            @Override
            public void onError(UiError e) {
                onLoginListener.onFailure(new SocialError("qq获取用户信息失败  " + parseUiError(e)));
            }

            @Override
            public void onCancel() {
                onLoginListener.onCancel();
            }

        });
    }

    public String parseUiError(UiError error) {
        return "qq error [ code = " + error.errorCode + ", msg = " + error.errorMessage + ", detail = " + error.errorDetail + " ]";
    }
}
