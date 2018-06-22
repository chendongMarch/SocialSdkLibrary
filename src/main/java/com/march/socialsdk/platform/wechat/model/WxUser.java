package com.march.socialsdk.platform.wechat.model;

import com.march.socialsdk.model.user.SocialUser;

import java.util.List;

/**
 * CreateAt : 2016/12/3
 * Describe : 微信用户数据
 *
 * @author chendong
 */

public class WxUser extends SocialUser {

    ///////////////////////////////////////////////////////////////////////////
    // openid是同一个公众账号用户的唯一标识，在同一个公众账号中不可能重复，不同公众账号有可能重复
    // unionid是公众账号的标识，是唯一的，即使在不同的公众账号也不可能重复
    //
    // {
    //    "openid":"odrwuwW6SPY0BKCmQQtKUm6mXUHY",
    //    "nickname":"chendong",
    //    "sex":1,
    //    "language":"zh_CN",
    //    "city":"Hangzhou",
    //    "province":"Zhejiang",
    //    "country":"CN",
    //    "headimgurl":"http://wx.qlogo.cn/mmopen/RtetbSu00GkZDcMjouv1LOZ4QrHGYR8dwvY4OGwvOkBsdVHUUXlTIaAS6SBFj9YubHHd1IGia5hcDv9rob8ib0KfDsicibB4PZkL/0",
    //    "privilege":[],
    //    "unionid":"oUgYEwlgZ4VCWc89Hfipjn1NjfqM"
    // }
    ///////////////////////////////////////////////////////////////////////////


    private String       unionid;
    private String       openid;
    private String       nickname;
    private int          sex;
    private String       province;
    private String       city;
    private String       country;
    private String       headimgurl;
    private List<String> privilege;

    private int    errcode;
    private String errmsg;

    public int getErrcode() {
        return errcode;
    }

    public boolean isNoError() {
        return errcode == 0;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public List<String> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<String> privilege) {
        this.privilege = privilege;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    @Override
    public String toString() {
        return "WxUserInfo{" +
                "openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", privilege=" + privilege +
                ", unionid='" + unionid + '\'' +
                '}';
    }

    @Override
    public String getUserId() {
        return unionid;
    }

    @Override
    public String getUserNickName() {
        return nickname;
    }

    @Override
    public int getUserGender() {
        if (sex == 1) {
            return GENDER_BOY;
        }
        if (sex == 0) {
            return GENDER_GIRL;
        }
        return GENDER_UNKONW;
    }

    @Override
    public String getUserProvince() {
        return province;
    }

    @Override
    public String getUserCity() {
        return city;
    }

    @Override
    public String getUserHeadUrl() {
        return headimgurl;
    }

    @Override
    public String getUserHeadUrlLarge() {
        return headimgurl;
    }

}