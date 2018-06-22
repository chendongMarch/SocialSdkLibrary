package com.march.socialsdk.platform.wechat.model;

import com.march.socialsdk.model.token.AccessToken;
import com.march.socialsdk.platform.Target;

/**
 * CreateAt : 2016/12/3
 * Describe : 微信的token
 *
 * @author chendong
 */
public class WeChatAccessToken extends AccessToken {

    //正确返回
    private String refresh_token;//用户刷新access_token。

    private String scope;//用户授权的作用域，使用逗号（,）分隔

    //错误返回
    private long errcode;

    private String errmsg;


    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getErrcode() {
        return errcode;
    }

    public void setErrcode(long errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }


    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isNoError() {
        return errcode == 0;
    }

    @Override
    public int getLoginTarget() {
        return Target.LOGIN_WX;
    }

}