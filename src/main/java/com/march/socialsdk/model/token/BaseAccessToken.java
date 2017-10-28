package com.march.socialsdk.model.token;

import com.march.socialsdk.manager.LoginManager;
import com.march.socialsdk.platform.Target;

/**
 * CreateAt : 2017/5/21
 * Describe : token基类
 *
 * @author chendong
 */
public abstract class BaseAccessToken {

    private String openid;//授权用户唯一标识。
    private String unionid;
    private String access_token;//接口调用凭证
    private long   expires_in;//access_token接口调用凭证超时时间，单位（秒）。

    public boolean isValid() {
        if (getLoginTarget() == Target.LOGIN_WX) {
            return getAccess_token() != null && getUnionid() != null;
        } else
            return getAccess_token() != null && getOpenid() != null;
    }

    public String getSocialId() {
        if (getLoginTarget() == Target.LOGIN_WX) {
            return unionid;
        } else
            return openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public abstract int getLoginTarget();


    @Override
    public String toString() {
        return "BaseAccessToken{" +
                "openid='" + openid + '\'' +
                ", unionid='" + unionid + '\'' +
                ", access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }
}
