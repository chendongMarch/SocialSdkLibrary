package com.march.socialsdk.model;

import com.march.socialsdk.model.token.AccessToken;
import com.march.socialsdk.model.user.SocialUser;

/**
 * CreateAt : 2016/12/25
 * Describe : 登陆结果
 *
 * @author chendong
 */

public class LoginResult {

    // 登陆的类型，对应 Target.LOGIN_QQ 等。。。
    private int         type;
    // 返回的基本用户信息
    // 针对登录类型可强转为 WbUser,WxUser,QQUser 来获取更加丰富的信息
    private SocialUser  socialUser;
    // 本次登陆的 token 信息，openid,unionid,token,expires_in
    private AccessToken accessToken;
    //
    private String      wxAuthCode;

    public LoginResult(int type, SocialUser baseUser, AccessToken baseToken) {
        this.type = type;
        socialUser = baseUser;
        accessToken = baseToken;
    }

    public LoginResult(int type, String wxAuthCode) {
        this.type = type;
        this.wxAuthCode = wxAuthCode;
    }

    public int getType() {
        return type;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSocialUser(SocialUser socialUser) {
        this.socialUser = socialUser;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public String getWxAuthCode() {
        return wxAuthCode;
    }

    public void setWxAuthCode(String wxAuthCode) {
        this.wxAuthCode = wxAuthCode;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "type=" + type +
                ", socialUser=" + socialUser +
                ", accessToken=" + accessToken +
                ", wxAuthCode='" + wxAuthCode + '\'' +
                '}';
    }
}
