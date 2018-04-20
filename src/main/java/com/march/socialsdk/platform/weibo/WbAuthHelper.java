package com.march.socialsdk.platform.weibo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.utils.TokenStoreUtils;
import com.march.socialsdk.utils.LogUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * CreateAt : 2016/12/5
 * Describe : 微博授权辅助，实现 auth 授权操作
 *
 * @author chendong
 */
class WbAuthHelper {

    public static final String TAG = WbAuthHelper.class.getSimpleName();

    public interface OnAuthOverListener {
        void onAuth(Oauth2AccessToken token);

        void onException(SocialError e);

        void onCancel();
    }

    public static void auth(Activity activity, SsoHandler mSsoHandler, OnAuthOverListener listener) {
        Oauth2AccessToken token = TokenStoreUtils.getWbToken(activity);
        if (token != null && token.isSessionValid()) {
            listener.onAuth(token);
        } else {
            // 创建微博实例
            // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
            LogUtils.e(TAG, "wb_auth", "开始授权");
            mSsoHandler.authorize(new MyWeiboAuthListener(activity, listener));
//            mSsoHandler.authorizeClientSso(new MyWeiboAuthListener(activity,listener));
        }
    }


    private static class MyWeiboAuthListener implements WeiboAuthListener {
        OnAuthOverListener listener;
        Context            context;

        public MyWeiboAuthListener(Context context, OnAuthOverListener listener) {
            this.context = context;
            this.listener = listener;
        }

        /**
         * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
         */
        @Override
        public void onComplete(Bundle values) {
            LogUtils.e(TAG,"wb_auth", "complete " + values.toString());
            // 从 Bundle 中解析 Token
            Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            //String phoneNum = mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 授权成功
                TokenStoreUtils.saveWbToken(context, mAccessToken);
                listener.onAuth(mAccessToken);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                // 授权失败
                LogUtils.e(TAG,"wb_auth", "授权失败 " + code);
                listener.onException(new SocialError("授权失败 code = " + code));
            }
        }

        @Override
        public void onCancel() {
            // 授权取消
            listener.onCancel();
            LogUtils.e(TAG, "wb_auth", "取消");

        }

        @Override
        public void onWeiboException(WeiboException e) {
            // 授权失败
            e.printStackTrace();
            LogUtils.e(TAG,"wb_auth", "Auth exception : " + e.getMessage());
            listener.onException(new SocialError("授权失败", e));
        }
    }
}
