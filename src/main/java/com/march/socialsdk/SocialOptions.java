package com.march.socialsdk;

import android.content.Context;
import android.util.SparseArray;

import com.march.socialsdk.common.SocialValues;
import com.march.socialsdk.platform.PlatformFactory;

import java.io.File;

/**
 * CreateAt : 2017/5/20
 * Describe :配置信息
 *
 * @author chendong
 */

public class SocialOptions {

    private static final String SHARE_CACHE_DIR_NAME = "toShare";

    // 调试配置
    private boolean       debug;
    // 应用名
    private String        appName;
    // 微信配置
    private String        wxAppId;
    private String        wxSecretKey;
    private boolean       onlyAuthCode;
    // qq 配置
    private String        qqAppId;
    // 微博配置
    private String        sinaAppId;
    private String        sinaRedirectUrl;
    private String        sinaScope;
    // 存储路径，不允许更改
    private String        cacheDir;
    // 钉钉配置
    private String        ddAppId;
    // 图片默认资源
    private int failImgRes;
    // token 有效时间，默认1天
    private long tokenExpires = 24 * 60 * 60 * 1000;
    // 平台工厂注册
    private SparseArray<PlatformFactory> platformFactoryArray;

    // 静态工厂
    public static SocialOptions with(Context context) {
        SocialOptions config = new SocialOptions();
        config.appName = context.getString(R.string.app_name);
        File shareDir = new File(context.getExternalCacheDir(), SHARE_CACHE_DIR_NAME);
        config.cacheDir = (shareDir.mkdirs() ? shareDir : context.getCacheDir()).getAbsolutePath();
        // init
        config.platformFactoryArray = new SparseArray<>();
        config.sinaRedirectUrl = SocialValues.REDIRECT_URL;
        config.sinaScope = SocialValues.SCOPE;
        config.debug = false;
        return config;
    }

    private SocialOptions() {

    }

    public SocialOptions registerPlatform(PlatformFactory factory) {
        platformFactoryArray.append(factory.getTarget(), factory);
        return this;
    }


    public SocialOptions tokenExpires(long time) {
        this.tokenExpires = time;
        return this;
    }

    public SocialOptions dd(String ddAppId) {
        this.ddAppId = ddAppId;
        return this;
    }

    public SocialOptions qq(String qqAppId) {
        this.qqAppId = qqAppId;
        return this;
    }

    public SocialOptions wechat(String wxAppId, String wxSecretKey) {
        this.wxSecretKey = wxSecretKey;
        this.wxAppId = wxAppId;
        return this;
    }

    public SocialOptions wechat(String wxAppId, String wxSecretKey, boolean onlyAuthCode) {
        this.onlyAuthCode = onlyAuthCode;
        this.wxSecretKey = wxSecretKey;
        this.wxAppId = wxAppId;
        return this;
    }

    public SocialOptions sina(String sinaAppId) {
        this.sinaAppId = sinaAppId;
        return this;
    }

    public SocialOptions sinaScope(String sinaScope) {
        this.sinaScope = sinaScope;
        return this;
    }

    public SocialOptions sinaRedirectUrl(String sinaRedirectUrl) {
        this.sinaRedirectUrl = sinaRedirectUrl;
        return this;
    }

    public SocialOptions failImgRes(int failImgRes) {
        this.failImgRes = failImgRes;
        return this;
    }

    public SocialOptions debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public long getTokenExpires() {
        return tokenExpires;
    }

    public SparseArray<PlatformFactory> getPlatformFactoryArray() {
        return platformFactoryArray;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public int getFailImgRes() {
        return failImgRes;
    }

    public String getAppName() {
        return appName;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public String getWxSecretKey() {
        return wxSecretKey;
    }

    public String getDdAppId() {
        return ddAppId;
    }

    public String getQqAppId() {
        return qqAppId;
    }

    public String getSinaAppId() {
        return sinaAppId;
    }

    public String getSinaRedirectUrl() {
        return sinaRedirectUrl;
    }

    public String getSinaScope() {
        return sinaScope;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isOnlyAuthCode() {
        return onlyAuthCode;
    }

    @Override
    public String toString() {
        return "SocialSdkConfig{" +
                "appName='" + appName + '\'' +
                ", wxAppId='" + wxAppId + '\'' +
                ", wxSecretKey='" + wxSecretKey + '\'' +
                ", qqAppId='" + qqAppId + '\'' +
                ", ddAppId='" + ddAppId + '\'' +
                ", sinaAppId='" + sinaAppId + '\'' +
                ", sinaRedirectUrl='" + sinaRedirectUrl + '\'' +
                ", sinaScope='" + sinaScope + '\'' +
                ", cacheDir='" + cacheDir + '\'' +
                '}';
    }
}