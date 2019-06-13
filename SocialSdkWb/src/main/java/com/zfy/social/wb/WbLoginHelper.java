package com.zfy.social.wb;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.listener.Recyclable;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.token.AccessToken;
import com.zfy.social.core.util.JsonUtil;
import com.zfy.social.core.util.SocialUtil;
import com.zfy.social.wb.model.SinaAccessToken;
import com.zfy.social.wb.model.WbUser;

/**
 * CreateAt : 2016/12/5
 * Describe : 新浪微博登陆辅助
 *
 * @author chendong
 */

class WbLoginHelper implements Recyclable {

    public static final String TAG = WbLoginHelper.class.getSimpleName();

    private int mLoginTarget;
    private OnLoginStateListener mOnLoginListener;
    private SsoHandler      mSsoHandler;

    WbLoginHelper(Activity context) {
        this.mSsoHandler = new SsoHandler(context);
        this.mLoginTarget = Target.LOGIN_WB;
    }

    /**
     * 获取用户信息
     *
     * @param token token
     */
    private void getUserInfo(final Oauth2AccessToken token) {
        JsonUtil.startJsonRequest("https://api.weibo.com/2/users/show.json?access_token=" + token.getToken() + "&uid=" + token.getUid(), WbUser.class, new JsonUtil.Callback<WbUser>() {
            @Override
            public void onSuccess(@NonNull WbUser user) {
                SocialUtil.e(TAG, JsonUtil.getObject2Json(user));
                mOnLoginListener.onState(null, LoginResult.successOf(mLoginTarget, user, new SinaAccessToken(token)));
            }

            @Override
            public void onFailure(SocialError e) {
                mOnLoginListener.onState(null, LoginResult.failOf(e));
            }
        });
    }

    public void login(Activity activity, final OnLoginStateListener listener) {
        if (listener == null)
            return;
        mOnLoginListener = listener;
        justAuth(activity, new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                getUserInfo(oauth2AccessToken);
            }

            @Override
            public void cancel() {
                listener.onState(null, LoginResult.cancelOf());
            }

            @Override
            public void onFailure(WbConnectErrorMessage msg) {
                listener.onState(null, LoginResult.failOf(SocialError.make(SocialError.CODE_SDK_ERROR, TAG + "#login#connect error," + msg.getErrorCode() + " " + msg.getErrorMessage())));
            }
        });
    }

    public void justAuth(final Activity activity, final WbAuthListener listener) {
        Oauth2AccessToken token = AccessToken.getToken(activity, mLoginTarget, Oauth2AccessToken.class);
        if (token != null && token.isSessionValid() && token.getExpiresTime() > System.currentTimeMillis()) {
            listener.onSuccess(token);
        } else {
            AccessToken.clearToken(activity, Target.LOGIN_WB);
            mSsoHandler.authorize(new WbAuthListener() {
                @Override
                public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                    oauth2AccessToken.setBundle(null);
                    SocialUtil.json("test", oauth2AccessToken.toString());
                    AccessToken.saveToken(activity, mLoginTarget, oauth2AccessToken);
                    listener.onSuccess(oauth2AccessToken);
                }

                @Override
                public void cancel() {
                    listener.cancel();
                }

                @Override
                public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                    listener.onFailure(wbConnectErrorMessage);
                }
            });
        }
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    @Override
    public void recycle() {
        mSsoHandler = null;
    }
}
