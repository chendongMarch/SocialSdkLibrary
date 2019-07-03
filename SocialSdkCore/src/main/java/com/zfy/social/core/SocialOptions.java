package com.zfy.social.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.zfy.social.core.adapter.IJsonAdapter;
import com.zfy.social.core.adapter.IRequestAdapter;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.listener.ShareInterceptor;
import com.zfy.social.core.model.SocialBuildConfig;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.util.SocialUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private int wbProgressColor;

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

        SocialBuildConfig buildConfig = SocialUtil.parseBuildConfig(jsonAdapter);
        if (buildConfig != null) {
            if (builder.tokenExpiresHours < 0) {
                builder.tokenExpiresHours = buildConfig.tokenExpireHour;
            }
            // 读取微信参数
            if (buildConfig.wxEnable) {
                builder.wxEnable = true;
                if (TextUtils.isEmpty(builder.wxAppId)) {
                    builder.wxAppId = buildConfig.wxAppId;
                }
                if (TextUtils.isEmpty(builder.wxSecretKey)) {
                    builder.wxSecretKey = buildConfig.wxAppSecret;
                }
                if (builder.wxOnlyAuthCode == null) {
                    builder.wxOnlyAuthCode = buildConfig.wxOnlyAuthCode;
                }
            }
            if (buildConfig.qqEnable) {
                builder.qqEnable = true;
                if (TextUtils.isEmpty(builder.qqAppId)) {
                    builder.qqAppId = buildConfig.qqAppId;
                }
            }
            if (buildConfig.ddEnable) {
                builder.ddEnable = true;
                if (TextUtils.isEmpty(builder.ddAppId)) {
                    builder.ddAppId = buildConfig.ddAppId;
                }
            }
            if (buildConfig.wbEnable) {
                builder.wbEnable = true;
                if (TextUtils.isEmpty(builder.wbAppId)) {
                    builder.wbAppId = buildConfig.wbAppId;
                }
                if (TextUtils.isEmpty(builder.wbRedirectUrl)) {
                    builder.wbRedirectUrl = buildConfig.wbRedirectUrl;
                }
            }
        }
        if (TextUtils.isEmpty(builder.wbRedirectUrl)) {
            builder.wbRedirectUrl = SocialValues.REDIRECT_URL;
        }
        if (builder.wxOnlyAuthCode == null) {
            builder.wxOnlyAuthCode = false;
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
        private Boolean wxOnlyAuthCode;
        // qq 配置
        private String qqAppId;
        // 微博配置
        private String wbAppId;
        private String wbRedirectUrl;
        private String wbScope;
        private int wbProgressColor;
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

        private boolean wxEnable;
        private boolean qqEnable;
        private boolean wbEnable;
        private boolean ddEnable;

        private Context context;

        public Builder(Context context) {
            this.context = context;
            this.shareInterceptors = new ArrayList<>();
            this.factories = new SparseArray<>();
        }

        public Builder dd(String ddAppId) {
            this.ddAppId = ddAppId;
            this.ddEnable = true;
            return this;
        }

        public Builder qq(String qqAppId) {
            this.qqAppId = qqAppId;
            this.qqEnable = true;
            return this;
        }

        public Builder addPlatform(PlatformFactory factory) {
            this.factories.append(factory.getPlatformTarget(), factory);
            return this;
        }

        public Builder wx(String wxAppId, String wxSecretKey) {
            this.wxSecretKey = wxSecretKey;
            this.wxAppId = wxAppId;
            this.wxEnable = true;
            return this;
        }

        public Builder wx(String wxAppId, String wxSecretKey, boolean wxOnlyAuthCode) {
            this.wxOnlyAuthCode = wxOnlyAuthCode;
            this.wxSecretKey = wxSecretKey;
            this.wxAppId = wxAppId;
            this.wxEnable = true;
            return this;
        }

        public Builder wb(String wbAppId) {
            this.wbAppId = wbAppId;
            this.wbEnable = true;
            return this;
        }

        public Builder wb(String wbAppId, String wbRedirectUrl) {
            this.wbAppId = wbAppId;
            this.wbRedirectUrl = wbRedirectUrl;
            this.wbEnable = true;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder tokenExpiresHours(int time) {
            this.tokenExpiresHours = time;
            return this;
        }

        public Builder shareSuccessIfStay(boolean shareSuccessIfStay) {
            this.shareSuccessIfStay = shareSuccessIfStay;
            return this;
        }

        public Builder wbProgressColor(int color) {
            this.wbProgressColor = color;
            return this;
        }

        public Builder failImgRes(int failImgRes) {
            this.failImgRes = failImgRes;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }


        public Builder requestAdapter(IRequestAdapter requestAdapter) {
            this.requestAdapter = requestAdapter;
            return this;
        }

        public Builder jsonAdapter(IJsonAdapter jsonAdapter) {
            this.jsonAdapter = jsonAdapter;
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
}