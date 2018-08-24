package com.march.socialsdk.platform.wechat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.model.token.AccessToken;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.platform.wechat.model.WeChatAccessToken;
import com.march.socialsdk.platform.wechat.model.WxUser;
import com.march.socialsdk.util.JsonUtil;
import com.march.socialsdk.util.SocialLogUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.lang.ref.WeakReference;

/**
 * CreateAt : 2016/12/3
 * Describe : 微信登陆辅助
 *
 * @author chendong
 */

class WxLoginHelper {

    public static final String TAG = WxLoginHelper.class.getSimpleName();

    private static final String BASE_URL = "https://api.weixin.qq.com/sns";

    private int mLoginType;
    private WeakReference<Context> mContextRef;
    private IWXAPI mIWXAPI;
    private String mAppId;
    private String mSecretKey;
    private OnLoginListener mOnLoginListener;

    WxLoginHelper(Context context, IWXAPI iwxapi, String appId) {
        this.mContextRef = new WeakReference<>(context.getApplicationContext());
        this.mIWXAPI = iwxapi;
        this.mAppId = appId;
        this.mLoginType = Target.LOGIN_WX;
    }

    private WeChatAccessToken getToken() {
        return AccessToken.getToken(mContextRef.get(), AccessToken.WECHAT_TOKEN_KEY, WeChatAccessToken.class);

    }
    /**
     * 开始登录
     */
    public void login(String secretKey, OnLoginListener loginListener) {
        this.mOnLoginListener = loginListener;
        this.mSecretKey = secretKey;
        // 检测本地token的机制
        WeChatAccessToken storeToken = getToken();
        if (storeToken != null && storeToken.isValid()) {
            checkAccessTokenValid(storeToken);
        } else {
            // 本地没有token, 发起请求，wxEntry将会获得code，接着获取access_token
            sendAuthReq();
        }
    }

    /**
     * 发起申请
     */
    private void sendAuthReq() {
        SocialLogUtil.e(TAG, "本地没有token,发起登录");
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "carjob_wx_login";
        mIWXAPI.sendReq(req);
    }

    /**
     * 刷新token,当access_token失效时使用,使用refresh_token获取新的token
     *
     * @param token 用来放 refresh_token
     */
    private void refreshToken(final WeChatAccessToken token) {
        SocialLogUtil.e(TAG, "token失效，开始刷新token");
        JsonUtil.startJsonRequest(buildRefreshTokenUrl(token), WeChatAccessToken.class, new JsonUtil.Callback<WeChatAccessToken>() {
            @Override
            public void onSuccess(@NonNull WeChatAccessToken newToken) {
                // 获取到access_token
                if (newToken.isNoError()) {
                    SocialLogUtil.e(TAG, "刷新token成功 token = " + newToken);
                    AccessToken.saveToken(mContextRef.get(), AccessToken.WECHAT_TOKEN_KEY, newToken);
                    // 刷新完成，获取用户信息
                    getUserInfoByValidToken(newToken);
                } else {
                    SocialLogUtil.e(TAG, "code = " + newToken.getErrcode() + "  ,msg = " + newToken.getErrmsg());
                    sendAuthReq();
                }
            }

            @Override
            public void onFailure(SocialError e) {
                // 刷新token失败
                mOnLoginListener.onFailure(e.append("refreshToken fail"));
            }
        });
    }

    /**
     * 根据code获取access_token
     *
     * @param code code
     */
    public void getAccessTokenByCode(String code) {
        SocialLogUtil.e(TAG, "使用code获取access_token " + code);
        JsonUtil.startJsonRequest(buildGetTokenUrl(code), WeChatAccessToken.class, new JsonUtil.Callback<WeChatAccessToken>() {
            @Override
            public void onSuccess(@NonNull WeChatAccessToken token) {
                // 获取到access_token
                if (token.isNoError()) {
                    AccessToken.saveToken(mContextRef.get(), AccessToken.WECHAT_TOKEN_KEY, token);
                    getUserInfoByValidToken(token);
                } else {
                    SocialError exception = new SocialError(SocialError.CODE_REQUEST_ERROR, TAG + "#getAccessTokenByCode#获取access_token失败 code = " + token.getErrcode() + "  msg = " + token.getErrmsg());
                    mOnLoginListener.onFailure(exception);
                }
            }

            @Override
            public void onFailure(SocialError e) {
                // 获取access_token失败
                mOnLoginListener.onFailure(e.append("getAccessTokenByCode fail"));
            }
        });
    }


    /**
     * 检测token有效性
     *
     * @param token 用来拿access_token
     */
    private void checkAccessTokenValid(final WeChatAccessToken token) {
        SocialLogUtil.e(TAG, "本地存了token,开始检测有效性" + token.toString());
        JsonUtil.startJsonRequest(buildCheckAccessTokenValidUrl(token), TokenValidResp.class, new JsonUtil.Callback<TokenValidResp>() {
            @Override
            public void onSuccess(@NonNull TokenValidResp resp) {
                // 检测是否有效
                SocialLogUtil.e(TAG, "检测token结束，结果 = " + resp.toString());
                if (resp.isNoError()) {
                    // access_token有效。开始获取用户信息
                    getUserInfoByValidToken(token);
                } else {
                    // access_token失效，刷新或者获取新的
                    refreshToken(token);
                }
            }

            @Override
            public void onFailure(SocialError e) {
                // 检测access_token有效性失败
                SocialLogUtil.e(TAG, "检测access_token失败");
                mOnLoginListener.onFailure(e.append("checkAccessTokenValid fail"));
            }
        });
    }

    /**
     * token是ok的，获取用户信息
     *
     * @param token 用来拿access_token
     */
    private void getUserInfoByValidToken(final WeChatAccessToken token) {
        SocialLogUtil.e(TAG, "access_token有效，开始获取用户信息");
        JsonUtil.startJsonRequest(buildFetchUserInfoUrl(token), WxUser.class, new JsonUtil.Callback<WxUser>() {
            @Override
            public void onSuccess(@NonNull WxUser wxUserInfo) {
                SocialLogUtil.e(TAG, "获取到用户信息" + wxUserInfo.toString());
                if (wxUserInfo.isNoError()) {
                    mOnLoginListener.onSuccess(new LoginResult(mLoginType, wxUserInfo, token));
                } else {
                    mOnLoginListener.onFailure(new SocialError(SocialError.CODE_REQUEST_ERROR, TAG + "#getUserInfoByValidToken#login code = " + wxUserInfo.getErrcode() + " ,msg = " + wxUserInfo.getErrmsg()));
                }
            }

            @Override
            public void onFailure(SocialError e) {
                // 获取用户信息失败
                mOnLoginListener.onFailure(e.append("getUserInfoByValidToken fail"));
            }
        });
    }

    private String buildRefreshTokenUrl(WeChatAccessToken token) {
        return BASE_URL
                + "/oauth2/refresh_token"
                + "?appid=" + mAppId
                + "&grant_type=" + "refresh_token"
                + "&refresh_token=" + token.getRefresh_token();
    }

    private String buildGetTokenUrl(String code) {
        return BASE_URL
                + "/oauth2/access_token"
                + "?appid=" + mAppId
                + "&secret=" + mSecretKey
                + "&code=" + code
                + "&grant_type=" + "authorization_code";
    }

    private String buildCheckAccessTokenValidUrl(WeChatAccessToken token) {
        return BASE_URL
                + "/auth"
                + "?access_token=" + token.getAccess_token()
                + "&openid=" + token.getOpenid();
    }

    private String buildFetchUserInfoUrl(WeChatAccessToken token) {
        return BASE_URL
                + "/userinfo"
                + "?access_token=" + token.getAccess_token()
                + "&openid=" + token.getOpenid();
    }


    public OnLoginListener getOnLoginListener() {
        return mOnLoginListener;
    }

    /**
     * 检测token有效性的resp
     */
    private static class TokenValidResp {
        private int errcode;
        private String errmsg;

        public boolean isNoError() {
            return errcode == 0;
        }

        int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        @Override
        public String toString() {
            return "TokenValidResp{" +
                    "errcode=" + errcode +
                    ", errmsg='" + errmsg + '\'' +
                    '}';
        }
    }

}
