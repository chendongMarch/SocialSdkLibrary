package com.zfy.social.core.model;

import com.zfy.social.core.common.SocialValues;

/**
 * CreateAt : 2019/6/13
 * Describe :
 *
 * @author chendong
 */
public class LoginObj {

    private String appSecret; // 授权域
    private String scope = SocialValues.WX_SCOPE; // 授权域
    private String nonceStr; // 随机字符串
    private String timestamp; // 时间戳
    private String signature; // 签名


    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
