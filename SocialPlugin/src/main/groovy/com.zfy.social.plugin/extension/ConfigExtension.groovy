package com.zfy.social.plugin.extension

class ConfigExtension  {

    String  appId        = ""
    boolean enable       = true
    String  appSecret    = ""
    String  url          = ""
    boolean onlyAuthCode = false


    String getAppId() {
        return appId
    }

    void setAppId(String appId) {
        this.appId = appId
    }

    boolean getEnable() {
        return enable
    }

    void setEnable(boolean enable) {
        this.enable = enable
    }

    String getAppSecret() {
        return appSecret
    }

    void setAppSecret(String appSecret) {
        this.appSecret = appSecret
    }

    String getUrl() {
        return url
    }

    void setUrl(String url) {
        this.url = url
    }

    boolean getOnlyAuthCode() {
        return onlyAuthCode
    }

    void setOnlyAuthCode(boolean onlyAuthCode) {
        this.onlyAuthCode = onlyAuthCode
    }
}