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

public class SocialSdkConfig {

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
    private int           defImageResId;

    private SparseArray<PlatformFactory> platformFactoryArray;

    // 静态工厂
    public static SocialSdkConfig with(Context context) {
        SocialSdkConfig config = new SocialSdkConfig();
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

    private SocialSdkConfig() {

    }

    public SocialSdkConfig registerPlatform(PlatformFactory factory) {
        platformFactoryArray.append(factory.getTarget(), factory);
        return this;
    }

    public SparseArray<PlatformFactory> getPlatformFactoryArray() {
        return platformFactoryArray;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public SocialSdkConfig dd(String ddAppId) {
        this.ddAppId = ddAppId;
        return this;
    }

    public SocialSdkConfig qq(String qqAppId) {
        this.qqAppId = qqAppId;
        return this;
    }

    public SocialSdkConfig wechat(String wxAppId, String wxSecretKey) {
        this.wxSecretKey = wxSecretKey;
        this.wxAppId = wxAppId;
        return this;
    }

    public SocialSdkConfig wechat(String wxAppId, String wxSecretKey, boolean onlyAuthCode) {
        this.onlyAuthCode = onlyAuthCode;
        this.wxSecretKey = wxSecretKey;
        this.wxAppId = wxAppId;
        return this;
    }

    public SocialSdkConfig sina(String sinaAppId) {
        this.sinaAppId = sinaAppId;
        return this;
    }

    public SocialSdkConfig sinaScope(String sinaScope) {
        this.sinaScope = sinaScope;
        return this;
    }

    public SocialSdkConfig sinaRedirectUrl(String sinaRedirectUrl) {
        this.sinaRedirectUrl = sinaRedirectUrl;
        return this;
    }

    public SocialSdkConfig defImageResId(int defImageResId) {
        this.defImageResId = defImageResId;
        return this;
    }

    public SocialSdkConfig debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public int getDefImageResId() {
        return defImageResId;
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