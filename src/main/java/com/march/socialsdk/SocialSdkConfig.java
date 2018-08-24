package com.march.socialsdk;

import android.content.Context;

import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.platform.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2017/5/20
 * Describe :配置信息
 *
 * @author chendong
 */

public class SocialSdkConfig {

    public static final String SHARE_CACHE_DIR_NAME = "toShare";

    // 调试配置
    private boolean isDebug = false;
    // 应用名
    private String  appName;
    // 微信配置
    private String  wxAppId;
    private String  wxSecretKey;
    private boolean onlyAuthCode;
    // qq 配置
    private String  qqAppId;
    // 微博配置
    private String  sinaAppId;
    private String sinaRedirectUrl = SocialConstants.REDIRECT_URL;
    private String sinaScope       = SocialConstants.SCOPE;
    // 存储路径，不允许更改
    private String shareCacheDirPath;
    // 钉钉配置
    private String ddAppId;
    // 图片默认资源
    private int    defImageResId;
    private List<Integer> disablePlatforms = new ArrayList<>();

    public SocialSdkConfig(Context context) {
        this.appName = context.getString(R.string.app_name);
        File shareDir = new File(context.getExternalCacheDir(), SHARE_CACHE_DIR_NAME);
        shareDir.mkdirs();
        shareCacheDirPath = shareDir.getAbsolutePath();
    }

    public String getShareCacheDirPath() {
        return shareCacheDirPath;
    }

    public SocialSdkConfig disablePlatform(@Target.PlatformTarget int platform) {
        this.disablePlatforms.add(platform);
        return this;
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
        return isDebug;
    }

    public SocialSdkConfig setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public boolean isOnlyAuthCode() {
        return onlyAuthCode;
    }

    public List<Integer> getDisablePlatforms() {
        return disablePlatforms;
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
                ", shareCacheDirPath='" + shareCacheDirPath + '\'' +
                '}';
    }
}