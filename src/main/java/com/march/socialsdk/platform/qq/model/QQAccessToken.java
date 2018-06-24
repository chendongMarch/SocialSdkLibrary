package com.march.socialsdk.platform.qq.model;

import com.march.socialsdk.model.token.AccessToken;
import com.march.socialsdk.platform.Target;

/**
 * CreateAt : 2016/12/6
 * Describe : qq的token
 *
 * @author chendong
 */

public class QQAccessToken extends AccessToken {

    private int    ret;
    private String pay_token;
    private String pf;
    private String pfkey;
    private String msg;
    private String login_cost;
    private String query_authority_cost;
    private String authority_cost;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getPay_token() {
        return pay_token;
    }

    public void setPay_token(String pay_token) {
        this.pay_token = pay_token;
    }


    public String getPf() {
        return pf;
    }

    public void setPf(String pf) {
        this.pf = pf;
    }

    public String getPfkey() {
        return pfkey;
    }

    public void setPfkey(String pfkey) {
        this.pfkey = pfkey;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLogin_cost() {
        return login_cost;
    }

    public void setLogin_cost(String login_cost) {
        this.login_cost = login_cost;
    }

    public String getQuery_authority_cost() {
        return query_authority_cost;
    }

    public void setQuery_authority_cost(String query_authority_cost) {
        this.query_authority_cost = query_authority_cost;
    }

    public String getAuthority_cost() {
        return authority_cost;
    }

    public void setAuthority_cost(String authority_cost) {
        this.authority_cost = authority_cost;
    }

    @Override
    public int getLoginTarget() {
        return Target.LOGIN_QQ;
    }
}
