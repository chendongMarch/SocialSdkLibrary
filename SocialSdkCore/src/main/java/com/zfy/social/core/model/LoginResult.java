package com.zfy.social.core.model;

import com.zfy.social.core.model.token.AccessToken;
import com.zfy.social.core.model.user.SocialUser;

/**
 * CreateAt : 2016/12/25
 * Describe : 登陆结果
 *
 * @author chendong
 */

public class LoginResult {

    // 登陆的类型，对应 Target.LOGIN_QQ 等。。。
    private int target;
    // 返回的基本用户信息
    // 针对登录类型可强转为 WbUser,WxUser,QQUser 来获取更加丰富的信息
    private SocialUser socialUser;
    // 本次登陆的 token 信息，openId, unionId,token,expires_in
    private AccessToken accessToken;
    // 授权码，如果 onlyAuthCode 为 true, 将会返回它
    private String wxAuthCode;

    public LoginResult(int target, SocialUser baseUser, AccessToken baseToken) {
        this.target = target;
        socialUser = baseUser;
        accessToken = baseToken;
    }

    public LoginResult(int target, String wxAuthCode) {
        this.target = target;
        this.wxAuthCode = wxAuthCode;
    }

    public int getTarget() {
        return target;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public void setTarget(int target) {
        this.target = target;
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
                "target=" + target +
                ", socialUser=" + socialUser +
                ", accessToken=" + accessToken +
                ", wxAuthCode='" + wxAuthCode + '\'' +
                '}';
    }
}
