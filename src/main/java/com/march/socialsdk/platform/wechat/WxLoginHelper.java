package com.march.socialsdk.platform.wechat;

import android.content.Context;

import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.helper.AuthTokenKeeper;
import com.march.socialsdk.helper.GsonHelper;
import com.march.socialsdk.helper.HttpsRequestHelper;
import com.march.socialsdk.helper.PlatformLog;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.manager.LoginManager;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.model.token.WeChatAccessToken;
import com.march.socialsdk.model.user.WxUser;
import com.march.socialsdk.platform.Target;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * CreateAt : 2016/12/3
 * Describe : 微信登陆辅助
 *
 * @author chendong
 */

public class WxLoginHelper {
    /*
     * 流程：
     * 发起登录申请流程 : 发起登录申请 -> code -> 获取access_token -> 存储 -> 获取用户信息 -> 结束
     * 检测本地token -> :
     * -> 没有 -> 发起登录申请流程
     * -> 有 -> 检测refresh_token有效期 —>
     *                               -> 快要到期 -> 发起登录申请流程
     *                               -> 还没到期 -> :
     *                                           -> 检测access_token有效性 —> :
     *                                                                  -> 有效 -> 获取用户信息
     *                                                                  -> 无效 -> 使用refresh_token刷新access_token -> :
     *                                                                                                             -> 成功 -> 存储 -> 获取用户信息 -> 结束
     *                                                                                                             -> refresh_token无效 -> 发起登录申请流程
     *
     */

    public static final String TAG = WxLoginHelper.class.getSimpleName();

    private static final String BASE_URL = "https://api.weixin.qq.com/sns";

    private int     loginType;
    private Context context;
    private IWXAPI  iwxapi;
    private String  appId;
    private String  secretKey;

    private OnLoginListener loginListener;


    WxLoginHelper(Context context, IWXAPI iwxapi, String appId) {
        this.context = context;
        this.iwxapi = iwxapi;
        this.appId = appId;
        this.loginType = Target.LOGIN_WX;
    }

    /**
     * 开始登录
     */
    public void login(String secretKey, OnLoginListener loginListener) {
        this.loginListener = loginListener;
        this.secretKey = secretKey;
        // 检测本地token的机制
        WeChatAccessToken storeToken = AuthTokenKeeper.getWxToken(context);
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
        PlatformLog.e(TAG, "本地没有token,发起登录");
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "carjob_wx_login";
        iwxapi.sendReq(req);
    }

    /**
     * 刷新token,当access_token失效时使用,使用refresh_token获取新的token
     *
     * @param token 用来放 refresh_token
     */
    private void refreshToken(final WeChatAccessToken token) {
        PlatformLog.e(TAG, "token失效，开始刷新token");
        HttpsRequestHelper.getHttps(buildRefreshTokenUrl(token),
                new HttpsRequestHelper.OnResultListener() {
                    @Override
                    public void onSuccess(String result) {
                        // 获取到access_token
                        WeChatAccessToken newToken = GsonHelper.getObject(result, WeChatAccessToken.class);
                        if (newToken.isNoError()) {
                            PlatformLog.e(TAG, "刷新token成功 token = " + newToken);
                            AuthTokenKeeper.saveWxToken(context, newToken);
                            // 刷新完成，获取用户信息
                            getUserInfoByValidToken(token);
                        } else {
                            PlatformLog.e(TAG, "coed = " + newToken.getErrcode() + "  ,msg = " + newToken.getErrmsg());
                            sendAuthReq();
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // 刷新token失败
                        exception.printStackTrace();
                        loginListener.onFailure(new SocialException(exception.getMessage()));
                    }
                });
    }

    /**
     * 根据code获取access_token
     *
     * @param code code
     */
    public void getAccessTokenByCode(String code) {
        PlatformLog.e(TAG, "使用code获取access_token " + code);
        HttpsRequestHelper.getHttps(buildGetTokenUrl(code),
                new HttpsRequestHelper.OnResultListener() {
                    @Override
                    public void onSuccess(String result) {
                        // 获取到access_token
                        WeChatAccessToken resp = GsonHelper.getObject(result, WeChatAccessToken.class);
                        if (resp.isNoError()) {
                            AuthTokenKeeper.saveWxToken(context, resp);
                            getUserInfoByValidToken(resp);
                        } else {
                            SocialException exception = new SocialException("获取access_token失败 code = " + resp.getErrcode() + "  msg = " + resp.getErrmsg());
                            loginListener.onFailure(exception);
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // 获取access_token失败
                        loginListener.onFailure(new SocialException("获取access_token失败", exception));
                    }
                });
    }


    /**
     * 检测token有效性
     *
     * @param token 用来拿access_token
     */
    private void checkAccessTokenValid(final WeChatAccessToken token) {
        PlatformLog.e(TAG, "本地存了token,开始检测有效性" + token.toString());
        HttpsRequestHelper.getHttps(buildCheckAccessTokenValidUrl(token),
                new HttpsRequestHelper.OnResultListener() {
                    @Override
                    public void onSuccess(String result) {
                        // 检测是否有效
                        TokenValidResp resp = GsonHelper.getObject(result, TokenValidResp.class);
                        PlatformLog.e(TAG, "检测token结束，结果 = " + result);
                        if (resp.isNoError()) {
                            // access_token有效。开始获取用户信息
                            getUserInfoByValidToken(token);
                        } else {
                            // access_token失效，刷新或者获取新的
                            refreshToken(token);
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // 检测access_token有效性失败
                        PlatformLog.e(TAG, "检测access_token失败");
                        loginListener.onFailure(new SocialException("检测access_token有效性失败", exception));
                    }
                });
    }

    /**
     * token是ok的，获取用户信息
     *
     * @param token 用来拿access_token
     */
    private void getUserInfoByValidToken(final WeChatAccessToken token) {
        PlatformLog.e(TAG, "access_token有效，开始获取用户信息");
        HttpsRequestHelper.getHttps(buildFetchUserInfoUrl(token),
                new HttpsRequestHelper.OnResultListener() {
                    @Override
                    public void onSuccess(String result) {
                        PlatformLog.e(TAG, "获取到用户信息" + result);
                        WxUser wxUserInfo = GsonHelper.getObject(result, WxUser.class);
                        if (wxUserInfo.isNoError()) {
                            loginListener.onLoginSucceed(new LoginResult(loginType, wxUserInfo, token));
                        } else {
                            loginListener.onFailure(new SocialException("wx_login code = " + wxUserInfo.getErrcode() + " ,msg = " + wxUserInfo.getErrmsg()));
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // 获取用户信息失败
                        loginListener.onFailure(new SocialException(exception.getMessage()));
                    }
                });
    }

    private String buildRefreshTokenUrl(WeChatAccessToken token) {
        return BASE_URL
                + "/oauth2/refresh_token"
                + "?appid=" + appId
                + "&grant_type=" + "refresh_token"
                + "&refresh_token=" + token.getRefresh_token();
    }

    private String buildGetTokenUrl(String code) {
        return BASE_URL
                + "/oauth2/access_token"
                + "?appid=" + appId
                + "&secret=" + secretKey
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


    /**
     * 检测token有效性的resp
     */
    private class TokenValidResp {
        private int    errcode;
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
    }

}
