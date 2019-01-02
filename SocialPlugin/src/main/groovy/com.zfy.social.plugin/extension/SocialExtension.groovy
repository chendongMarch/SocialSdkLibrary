package com.zfy.social.plugin.extension

import com.zfy.social.plugin.extension.ConfigExtension
import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class SocialExtension {

    public boolean local = false

    public ConfigExtension core
    // 微信配置
    public ConfigExtension wx
    // qq 配置
    public ConfigExtension qq
    // dd 配置
    public ConfigExtension dd
    // wb 配置
    public ConfigExtension wb


    ConfigExtension core(Closure closure) {
        return core(ConfigureUtil.configureUsing(closure))
    }

    void core(Action<? super ConfigExtension> action) {
        if (wx == null) wx = new ConfigExtension()
        action.execute(wx)
    }

    ConfigExtension wx(Closure closure) {
        return wx(ConfigureUtil.configureUsing(closure))
    }

    void wx(Action<? super ConfigExtension> action) {
        if (wx == null) wx = new ConfigExtension()
        action.execute(wx)
    }

    ConfigExtension qq(Closure closure) {
        return qq(ConfigureUtil.configureUsing(closure))
    }

    void qq(Action<? super ConfigExtension> action) {
        if (qq == null) qq = new ConfigExtension()
        action.execute(qq)
    }

    ConfigExtension dd(Closure closure) {
        return dd(ConfigureUtil.configureUsing(closure))
    }

    void dd(Action<? super ConfigExtension> action) {
        if (dd == null) dd = new ConfigExtension()
        action.execute(dd)
    }

    ConfigExtension wb(Closure closure) {
        return wb(ConfigureUtil.configureUsing(closure))
    }

    void wb(Action<? super ConfigExtension> action) {
        if (wb == null) wb = new ConfigExtension()
        action.execute(wb)
    }

    ConfigExtension getCore() {
        if (core == null) {
            core = new ConfigExtension()
            core.enable = false
        }
        return core
    }

    void setCore(ConfigExtension core) {
        this.core = core
    }

    ConfigExtension getWx() {
        if (wx == null) {
            wx = new ConfigExtension()
            wx.enable = false
        }
        return wx
    }

    void setWx(ConfigExtension wx) {
        this.wx = wx
    }

    ConfigExtension getQq() {
        if (qq == null) {
            qq = new ConfigExtension()
            qq.enable = false
        }
        return qq
    }

    void setQq(ConfigExtension qq) {
        this.qq = qq
    }

    ConfigExtension getDd() {
        if (dd == null) {
            dd = new ConfigExtension()
            dd.enable = false
        }
        return dd
    }

    void setDd(ConfigExtension dd) {
        this.dd = dd
    }

    ConfigExtension getWb() {
        if (wb == null) {
            wb = new ConfigExtension()
            wb.enable = false
        }
        return wb
    }

    void setWb(ConfigExtension wb) {
        this.wb = wb
    }

    boolean getLocal() {
        return local
    }

    void setLocal(boolean local) {
        this.local = local
    }
}