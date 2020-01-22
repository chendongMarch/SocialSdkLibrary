package com.zfy.social.plugin.extension


import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class SocialExt {

    public boolean shareSuccessIfStay = false
    public int tokenExpiresHours = 0

    public boolean useGson = true
    public boolean useOkHttp = true

    public String appName
    public String color

    public boolean local = false

    public boolean debug = false

    public ConfigExt core
    // 微信配置
    public ConfigExt wx
    // qq 配置
    public ConfigExt qq
    // dd 配置
    public ConfigExt dd
    // wb 配置
    public ConfigExt wb


    ConfigExt core(Closure closure) {
        return core(ConfigureUtil.configureUsing(closure))
    }

    void core(Action<? super ConfigExt> action) {
        if (core == null) core = new ConfigExt()
        action.execute(core)
    }

    ConfigExt wx(Closure closure) {
        return wx(ConfigureUtil.configureUsing(closure))
    }

    void wx(Action<? super ConfigExt> action) {
        if (wx == null) wx = new ConfigExt()
        action.execute(wx)
    }

    ConfigExt qq(Closure closure) {
        return qq(ConfigureUtil.configureUsing(closure))
    }

    void qq(Action<? super ConfigExt> action) {
        if (qq == null) qq = new ConfigExt()
        action.execute(qq)
    }

    ConfigExt dd(Closure closure) {
        return dd(ConfigureUtil.configureUsing(closure))
    }

    void dd(Action<? super ConfigExt> action) {
        if (dd == null) dd = new ConfigExt()
        action.execute(dd)
    }

    ConfigExt wb(Closure closure) {
        return wb(ConfigureUtil.configureUsing(closure))
    }

    void wb(Action<? super ConfigExt> action) {
        if (wb == null) wb = new ConfigExt()
        action.execute(wb)
    }

    ConfigExt getCore() {
        if (core == null) {
            core = new ConfigExt()
            core.enable = false
        }
        return core
    }

    void setCore(ConfigExt core) {
        this.core = core
    }

    ConfigExt getWx() {
        if (wx == null) {
            wx = new ConfigExt()
            wx.enable = false
        }
        return wx
    }

    void setWx(ConfigExt wx) {
        this.wx = wx
    }

    ConfigExt getQq() {
        if (qq == null) {
            qq = new ConfigExt()
            qq.enable = false
        }
        return qq
    }

    void setQq(ConfigExt qq) {
        this.qq = qq
    }

    ConfigExt getDd() {
        if (dd == null) {
            dd = new ConfigExt()
            dd.enable = false
        }
        return dd
    }

    void setDd(ConfigExt dd) {
        this.dd = dd
    }

    ConfigExt getWb() {
        if (wb == null) {
            wb = new ConfigExt()
            wb.enable = false
        }
        return wb
    }

    void setWb(ConfigExt wb) {
        this.wb = wb
    }

    boolean getShareSuccessIfStay() {
        return shareSuccessIfStay
    }

    void setShareSuccessIfStay(boolean shareSuccessIfStay) {
        this.shareSuccessIfStay = shareSuccessIfStay
    }

    int getTokenExpiresHours() {
        return tokenExpiresHours
    }

    void setTokenExpiresHours(int tokenExpiresHours) {
        this.tokenExpiresHours = tokenExpiresHours
    }

    boolean getUseGson() {
        return useGson
    }

    void setUseGson(boolean useGson) {
        this.useGson = useGson
    }

    boolean getUseOkHttp() {
        return useOkHttp
    }

    void setUseOkHttp(boolean useOkHttp) {
        this.useOkHttp = useOkHttp
    }

    String getAppName() {
        return appName
    }

    void setAppName(String appName) {
        this.appName = appName
    }

    String getColor() {
        return color
    }

    void setColor(String color) {
        this.color = color
    }

    boolean getLocal() {
        return local
    }

    void setLocal(boolean local) {
        this.local = local
    }

    boolean getDebug() {
        return debug
    }

    void setDebug(boolean debug) {
        this.debug = debug
    }


    @Override
    public String toString() {
        return "SocialExt{" +
                "local=" + local +
                ", debug=" + debug +
                ", core=" + core +
                ", wx=" + wx +
                ", qq=" + qq +
                ", dd=" + dd +
                ", wb=" + wb +
                '}';
    }
}