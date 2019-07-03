package com.zfy.social.wx;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory;
import com.tencent.mm.opensdk.diffdev.IDiffDevOAuth;
import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.zfy.social.core._SocialSdk;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.model.LoginObj;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.token.AccessToken;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.JsonUtil;
import com.zfy.social.core.util.SocialUtil;
import com.zfy.social.wx.model.WeChatAccessToken;
import com.zfy.social.wx.model.WxUser;

import java.io.File;
import java.io.IOException;

/**
 * CreateAt : 2016/12/3
 * Describe : 微信登陆辅助
 *
 * @author chendong
 */

class WxLoginHelper {

    public static final String TAG = WxLoginHelper.class.getSimpleName();

    private static final String BASE_URL = "https://api.weixin.qq.com/sns";

    private int mLoginTarget;
    private IWXAPI mIWXAPI;
    private String mAppId;
    private String mSecretKey;
    private String mAuthCode;
    private Activity mAct;
    private LoginObj mLoginObj;

    private OnLoginStateListener mListener;
    private IDiffDevOAuth mDiffDevOAuth;

    WxLoginHelper(Activity act, IWXAPI iwxapi, int target, String appId, String appSecret, LoginObj loginObj) {
        mAct = act;
        mLoginObj = loginObj;
        mIWXAPI = iwxapi;
        mAppId = appId;
        mSecretKey = appSecret;
        mLoginTarget = target;
    }

    private WeChatAccessToken getToken() {
        return AccessToken.getToken(mAct, mLoginTarget, WeChatAccessToken.class);
    }

    /**
     * 开始登录
     */
    void requestAuthCode(OnLoginStateListener listener) {
        mListener = listener;

        // 检测本地token的机制
        WeChatAccessToken storeToken = getToken();
        if (storeToken != null && storeToken.isValid()) {
            checkAccessTokenValid(storeToken);
        } else {
            // 本地没有token, 发起请求，wxEntry将会获得code，接着获取access_token
            if (mLoginTarget == Target.LOGIN_WX_SCAN) {
                sendWxCodeAuthReq();
            } else if (mLoginTarget == Target.LOGIN_WX) {
                sendAuthReq();
            } else {
                mListener.onState(null, LoginResult.failOf(SocialError.make(SocialError.CODE_PARAM_ERROR, "target 错误")));
            }
        }
    }

    private void sendWxCodeAuthReq() {
        if (mLoginObj == null) {
            mListener.onState(null,
                    LoginResult.failOf(SocialError.make(SocialError.CODE_PARAM_ERROR, "login scan code param")));
            return;
        }
        LoginObj obj = mLoginObj;
        if (mDiffDevOAuth != null) {
            mDiffDevOAuth.stopAuth();
            mDiffDevOAuth.removeAllListeners();
            mDiffDevOAuth.detach();
            mDiffDevOAuth = null;
        }
        mDiffDevOAuth = DiffDevOAuthFactory.getDiffDevOAuth();
        mDiffDevOAuth.auth(mAppId, obj.getScope(), obj.getNonceStr(), obj.getTimestamp(), obj.getSignature(), new OAuthListener() {
            @Override
            public void onAuthGotQrcode(String s, byte[] bytes) {
                try {
                    File file = FileUtil.saveWxCode2File(bytes);
                    if (FileUtil.isExist(file)) {
                        LoginResult result = LoginResult.stateOf(LoginResult.STATE_WX_CODE_RECEIVE);
                        result.wxCodePath = file.getAbsolutePath();
                        mListener.onState(null, result);
                    }
                    bytes = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    mListener.onState(null,
                            LoginResult.failOf(
                                    SocialError.make(SocialError.CODE_PARAM_ERROR, "login scan code param", e)));
                }
            }

            @Override
            public void onQrcodeScanned() {
                LoginResult result = LoginResult.stateOf(LoginResult.STATE_WX_CODE_SCANNED);
                mListener.onState(null, result);
            }

            @Override
            public void onAuthFinish(OAuthErrCode oAuthErrCode, String authCode) {
                switch (oAuthErrCode) {
                    case WechatAuth_Err_OK:
                        if (_SocialSdk.getInst().opts().isWxOnlyAuthCode()) {
                            mListener.onState(null, LoginResult.successOf(Target.LOGIN_WX, authCode));
                        } else {
                            getAccessTokenByCode(authCode);
                        }
                        break;
                    case WechatAuth_Err_Cancel:
                        mListener.onState(null,
                                LoginResult.cancelOf());
                        break;
                    case WechatAuth_Err_NormalErr:
                        mListener.onState(null,
                                LoginResult.failOf(SocialError.make(SocialError.CODE_COMMON_ERROR, "微信扫码登录错误[NORMAL]")));
                        break;
                    case WechatAuth_Err_NetworkErr:
                        mListener.onState(null,
                                LoginResult.failOf(SocialError.make(SocialError.CODE_COMMON_ERROR, "微信扫码登录错误[NETWORK]")));
                        break;
                    case WechatAuth_Err_JsonDecodeErr:
                        mListener.onState(null,
                                LoginResult.failOf(SocialError.make(SocialError.CODE_COMMON_ERROR, "微信扫码登录错误[JSON]")));
                        break;

                    case WechatAuth_Err_Timeout:
                        mListener.onState(null,
                                LoginResult.failOf(SocialError.make(SocialError.CODE_COMMON_ERROR, "微信扫码登录错误[TIMEOUT]")));
                        break;
                    case WechatAuth_Err_Auth_Stopped:
                        mListener.onState(null,
                                LoginResult.failOf(SocialError.make(SocialError.CODE_COMMON_ERROR, "微信扫码登录错误[STOP]")));
                        break;
                }
            }
        });
    }

    /**
     * 发起申请
     */
    private void sendAuthReq() {
        SocialUtil.e(TAG, "本地没有token,发起登录");
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
        SocialUtil.e(TAG, "token失效，开始刷新token");
        JsonUtil.startJsonRequest(buildRefreshTokenUrl(token), WeChatAccessToken.class, new JsonUtil.Callback<WeChatAccessToken>() {
            @Override
            public void onSuccess(@NonNull WeChatAccessToken newToken) {
                // 获取到access_token
                if (newToken.isNoError()) {
                    SocialUtil.e(TAG, "刷新token成功 token = " + newToken);
                    AccessToken.saveToken(mAct, mLoginTarget, newToken);
                    // 刷新完成，获取用户信息
                    getUserInfoByValidToken(newToken);
                } else {
                    SocialUtil.e(TAG, "code = " + newToken.getErrcode() + "  ,msg = " + newToken.getErrmsg());
                    sendAuthReq();
                }
            }

            @Override
            public void onFailure(SocialError e) {
                // 刷新token失败
                mListener.onState(mAct, LoginResult.failOf(mLoginTarget, e.append("refreshToken fail")));
            }
        });
    }

    /**
     * 根据code获取access_token
     *
     * @param code code
     */
    public void getAccessTokenByCode(String code) {
        mAuthCode = code;

        SocialUtil.e(TAG, "使用code获取access_token " + code);
        JsonUtil.startJsonRequest(buildGetTokenUrl(code), WeChatAccessToken.class, new JsonUtil.Callback<WeChatAccessToken>() {
            @Override
            public void onSuccess(@NonNull WeChatAccessToken token) {
                // 获取到access_token
                if (token.isNoError()) {
                    AccessToken.saveToken(mAct, mLoginTarget, token);
                    getUserInfoByValidToken(token);
                } else {
                    SocialError exception = SocialError.make(SocialError.CODE_REQUEST_ERROR, TAG + "#getAccessTokenByCode#获取access_token失败 code = " + token.getErrcode() + "  msg = " + token.getErrmsg());
                    mListener.onState(mAct, LoginResult.failOf(mLoginTarget, exception));
                }
            }

            @Override
            public void onFailure(SocialError e) {
                // 获取access_token失败
                mListener.onState(mAct, LoginResult.failOf(mLoginTarget, e.append("getAccessTokenByCode fail")));
            }
        });
    }


    /**
     * 检测token有效性
     *
     * @param token 用来拿access_token
     */
    private void checkAccessTokenValid(final WeChatAccessToken token) {
        SocialUtil.e(TAG, "本地存了token,开始检测有效性" + token.toString());
        JsonUtil.startJsonRequest(buildCheckAccessTokenValidUrl(token), TokenValidResp.class, new JsonUtil.Callback<TokenValidResp>() {
            @Override
            public void onSuccess(@NonNull TokenValidResp resp) {
                // 检测是否有效
                SocialUtil.e(TAG, "检测token结束，结果 = " + resp.toString());
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
                mListener.onState(mAct, LoginResult.failOf(mLoginTarget, e.append("checkAccessTokenValid fail")));

            }
        });
    }

    /**
     * token是ok的，获取用户信息
     *
     * @param token 用来拿access_token
     */
    private void getUserInfoByValidToken(final WeChatAccessToken token) {
        SocialUtil.e(TAG, "access_token有效，开始获取用户信息");
        JsonUtil.startJsonRequest(buildFetchUserInfoUrl(token), WxUser.class, new JsonUtil.Callback<WxUser>() {
            @Override
            public void onSuccess(@NonNull WxUser wxUserInfo) {
                SocialUtil.e(TAG, "获取到用户信息" + wxUserInfo.toString());
                if (wxUserInfo.isNoError()) {
                    LoginResult result = LoginResult.successOf(mLoginTarget, wxUserInfo, token);
                    result.wxAuthCode = mAuthCode;
                    mListener.onState(mAct, result);
                } else {
                    mListener.onState(mAct, LoginResult.failOf(mLoginTarget,
                            SocialError.make(SocialError.CODE_REQUEST_ERROR, TAG + "#getUserInfoByValidToken#requestAuthCode code = " + wxUserInfo.getErrcode() + " ,msg = " + wxUserInfo.getErrmsg())));
                }
            }

            @Override
            public void onFailure(SocialError e) {
                // 获取用户信息失败
                mListener.onState(mAct, LoginResult.failOf(mLoginTarget, e.append("getUserInfoByValidToken fail")));

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


    OnLoginStateListener getListener() {
        return mListener;
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

    public void recycle() {
        if (mDiffDevOAuth != null) {
            mDiffDevOAuth.removeAllListeners();
            mDiffDevOAuth.stopAuth();
            mDiffDevOAuth.detach();
        }
    }

}
