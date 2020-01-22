package com.zfy.social.core;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseArray;

import com.zfy.social.core.adapter.IJsonAdapter;
import com.zfy.social.core.adapter.IRequestAdapter;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.listener.ShareInterceptor;
import com.zfy.social.core.platform.PlatformFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CreateAt : 2017/5/20
 * Describe :配置信息
 *
 * @author chendong
 */

public class SocialOptions {

    private static final String SHARE_CACHE_DIR_NAME = "toShare";

    private boolean debug;
    private String appName;

    private String wxAppId;
    private String wxSecretKey;
    private boolean wxOnlyAuthCode;

    private String qqAppId;

    private String wbAppId;
    private String wbRedirectUrl;
    private String wbScope;
    private int wbProgressColor = Color.YELLOW;

    private String ddAppId;

    private String cacheDir;
    private int failImgRes;
    private long tokenExpiresHours;
    private boolean shareSuccessIfStay;

    private boolean wxEnable;
    private boolean qqEnable;
    private boolean wbEnable;
    private boolean ddEnable;

    IJsonAdapter jsonAdapter;
    IRequestAdapter reqAdapter;
    SparseArray<PlatformFactory> factories;
    List<ShareInterceptor> shareInterceptors;

    Set<String> factoryClassList;

    boolean useGson;
    boolean useOkHttp;

    public long getTokenExpiresHoursMs() {
        if (tokenExpiresHours <= 0) {
            return 0;
        }
        return tokenExpiresHours * 60 * 60 * 1000;
    }

    public long getTokenExpiresHours() {
        if (tokenExpiresHours <= 0) {
            return 0;
        }
        return tokenExpiresHours;

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

    public String getWbAppId() {
        return wbAppId;
    }

    public String getWbRedirectUrl() {
        return wbRedirectUrl;
    }

    public String getWbScope() {
        return wbScope;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isWxOnlyAuthCode() {
        return wxOnlyAuthCode;
    }

    public boolean isWxEnable() {
        return wxEnable;
    }

    public boolean isQqEnable() {
        return qqEnable;
    }

    public boolean isWbEnable() {
        return wbEnable;
    }

    public int getWbProgressColor() {
        return wbProgressColor;
    }

    public boolean isDdEnable() {
        return ddEnable;
    }

    public boolean isShareSuccessIfStay() {
        return shareSuccessIfStay;
    }

    @Override
    public String toString() {
        return "SocialSdkConfig{" +
                "appName='" + appName + '\'' +
                ", wxAppId='" + wxAppId + '\'' +
                ", wxSecretKey='" + wxSecretKey + '\'' +
                ", qqAppId='" + qqAppId + '\'' +
                ", ddAppId='" + ddAppId + '\'' +
                ", wbAppId='" + wbAppId + '\'' +
                ", wbRedirectUrl='" + wbRedirectUrl + '\'' +
                ", wbScope='" + wbScope + '\'' +
                ", cacheDir='" + cacheDir + '\'' +
                '}';
    }

    private SocialOptions(Builder builder) {

        jsonAdapter = builder.jsonAdapter;
        reqAdapter = builder.requestAdapter;
        factories = builder.factories;
        shareInterceptors = builder.shareInterceptors;

        useGson = builder.useGson;
        useOkHttp = builder.useOkHttp;
        factoryClassList = builder.factoryClassList;

        if (TextUtils.isEmpty(builder.wbRedirectUrl)) {
            builder.wbRedirectUrl = SocialValues.REDIRECT_URL;
        }
        if (builder.tokenExpiresHours < 0) {
            builder.tokenExpiresHours = 0;
        }
        this.wbProgressColor = builder.wbProgressColor;
        this.shareSuccessIfStay = builder.shareSuccessIfStay;
        this.debug = builder.debug;
        this.appName = builder.appName;
        this.wxAppId = builder.wxAppId;
        this.wxSecretKey = builder.wxSecretKey;
        this.wxOnlyAuthCode = builder.wxOnlyAuthCode;
        // qq 配置
        this.qqAppId = builder.qqAppId;
        // 微博配置
        this.wbAppId = builder.wbAppId;
        this.wbRedirectUrl = builder.wbRedirectUrl;
        this.wbScope = builder.wbScope;
        // 钉钉配置
        this.ddAppId = builder.ddAppId;
        // 图片默认资源
        this.failImgRes = builder.failImgRes;
        // token 有效时间，默认1天
        this.tokenExpiresHours = builder.tokenExpiresHours;
        this.cacheDir = builder.cacheDir;
        // enable
        this.wxEnable = builder.wxEnable;
        this.qqEnable = builder.qqEnable;
        this.wbEnable = builder.wbEnable;
        this.ddEnable = builder.ddEnable;
    }


    public static class Builder {
        // 调试配置
        private boolean debug;
        private String appName;
        private String cacheDir;
        // 微信配置
        private String wxAppId;
        private String wxSecretKey;
        private boolean wxOnlyAuthCode;
        // qq 配置
        private String qqAppId;
        // 微博配置
        private String wbAppId;
        private String wbRedirectUrl;
        private String wbScope;
        private int wbProgressColor = Color.YELLOW;
        // 钉钉配置
        private String ddAppId;
        // 图片默认资源
        private int failImgRes;
        // token 失效时间，默认立刻失效
        private int tokenExpiresHours = -1;
        // 如果留在三方应用，是回调成功还是失败，默认是失败
        private boolean shareSuccessIfStay;
        // json 解析框架注入
        private IJsonAdapter jsonAdapter;
        // 网络请求框架注入
        private IRequestAdapter requestAdapter;

        private List<ShareInterceptor> shareInterceptors;

        private SparseArray<PlatformFactory> factories;
        private Set<String> factoryClassList;

        private boolean wxEnable;
        private boolean qqEnable;
        private boolean wbEnable;
        private boolean ddEnable;

        private Context context;

        private boolean useGson = true;
        private boolean useOkHttp = true;

        public Builder(Context context) {
            this.context = context;
            this.shareInterceptors = new ArrayList<>();
            this.factories = new SparseArray<>();
            this.factoryClassList = new HashSet<>();

            initConfigByAsm();
        }

        private void initConfigByAsm() {

        }

        public Builder failImgRes(int failImgRes) {
            this.failImgRes = failImgRes;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder addShareInterceptor(ShareInterceptor interceptor) {
            if (!shareInterceptors.contains(interceptor)) {
                shareInterceptors.add(interceptor);
            }
            return this;
        }

        public SocialOptions build() {
            if (TextUtils.isEmpty(appName)) {
                appName = context.getString(R.string.app_name);
            }
            File storageDir = new File(context.getExternalCacheDir(), SHARE_CACHE_DIR_NAME);
            storageDir.mkdirs();
            this.cacheDir = storageDir.getAbsolutePath();
            wbScope = SocialValues.WB_SCOPE;
            return new SocialOptions(this);
        }
    }

    public static class Builder2 {

        private Builder builder;

        public Builder2(Context context) {
            builder = new Builder(context);
        }

        public Builder2 dd(String ddAppId) {
            builder.ddAppId = ddAppId;
            builder.ddEnable = true;
            return this;
        }

        public Builder2 qq(String qqAppId) {
            builder.qqAppId = qqAppId;
            builder.qqEnable = true;
            return this;
        }

        public Builder2 addPlatform(PlatformFactory factory) {
            builder.factories.append(factory.getPlatformTarget(), factory);
            return this;
        }

        public Builder2 wx(String wxAppId, String wxSecretKey) {
            builder.wxSecretKey = wxSecretKey;
            builder.wxAppId = wxAppId;
            builder.wxEnable = true;
            return this;
        }

        public Builder2 wx(String wxAppId, String wxSecretKey, boolean wxOnlyAuthCode) {
            builder.wxOnlyAuthCode = wxOnlyAuthCode;
            builder.wxSecretKey = wxSecretKey;
            builder.wxAppId = wxAppId;
            builder.wxEnable = true;
            return this;
        }

        public Builder2 wb(String wbAppId) {
            builder.wbAppId = wbAppId;
            builder.wbEnable = true;
            return this;
        }

        public Builder2 wb(String wbAppId, String wbRedirectUrl) {
            builder.wbAppId = wbAppId;
            builder.wbRedirectUrl = wbRedirectUrl;
            builder.wbEnable = true;
            return this;
        }

        public Builder2 appName(String appName) {
            builder.appName = appName;
            return this;
        }

        public Builder2 tokenExpiresHours(int time) {
            builder.tokenExpiresHours = time;
            return this;
        }

        public Builder2 shareSuccessIfStay(boolean shareSuccessIfStay) {
            builder.shareSuccessIfStay = shareSuccessIfStay;
            return this;
        }

        public Builder2 wbProgressColor(int color) {
            builder.wbProgressColor = color;
            return this;
        }

        public Builder2 failImgRes(int failImgRes) {
            builder.failImgRes = failImgRes;
            return this;
        }

        public Builder2 debug(boolean debug) {
            builder.debug = debug;
            return this;
        }

        public Builder2 requestAdapter(IRequestAdapter requestAdapter) {
            builder.requestAdapter = requestAdapter;
            return this;
        }

        public Builder2 jsonAdapter(IJsonAdapter jsonAdapter) {
            builder.jsonAdapter = jsonAdapter;
            return this;
        }

        public Builder2 addShareInterceptor(ShareInterceptor interceptor) {
            builder.addShareInterceptor(interceptor);
            return this;
        }


        public SocialOptions build() {
            return builder.build();
        }
    }

}