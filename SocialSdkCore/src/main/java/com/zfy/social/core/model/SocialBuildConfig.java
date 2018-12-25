package com.zfy.social.core.model;

/**
 * CreateAt : 2018/12/25
 * Describe :
 *
 * @author chendong
 */
public class SocialBuildConfig {

    // 设置 token 有效期，有效期内不会重新获取 token
    // 默认一天，如下设置为 12 小时
    // 设置为0，将不会做持久化存储，每次获取最新的
    public int tokenExpireHour = -1;

    // 开启微信平台
    public boolean wxEnable = true;
    // 微信 appId
    public String wxAppId = "";
    // 微信 secret 登录使用
    public String wxAppSecret = "";
    public boolean wxOnlyAuthCode = false;
    // 开启 QQ 平台
    public boolean qqEnable = true;
    // qq appId
    public String qqAppId = "";

    // 开启 DD 平台
    public boolean ddEnable = false;
    // dd appId
    public String ddAppId = "";

    // 开启 Wb 平台
    public boolean wbEnable = true;
    // wb appId
    public String wbAppId = "";
    // wb redirect url
    public String wbRedirectUrl = "";


    @Override
    public String toString() {
        return "SocialBuildConfig{" +
                ", wxEnable=" + wxEnable +
                ", wxAppId='" + wxAppId + '\'' +
                ", wxAppSecret='" + wxAppSecret + '\'' +
                ", qqEnable=" + qqEnable +
                ", qqAppId='" + qqAppId + '\'' +
                ", ddEnable=" + ddEnable +
                ", ddAppId='" + ddAppId + '\'' +
                ", wbEnable=" + wbEnable +
                ", wbAppId='" + wbAppId + '\'' +
                ", wbRedirectUrl='" + wbRedirectUrl + '\'' +
                ", tokenExpireHour=" + tokenExpireHour +
                '}';
    }
}

