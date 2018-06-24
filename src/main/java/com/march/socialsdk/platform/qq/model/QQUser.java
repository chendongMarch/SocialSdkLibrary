package com.march.socialsdk.platform.qq.model;

import com.march.socialsdk.model.user.SocialUser;

/**
 * CreateAt : 2016/12/3
 * Describe : qq用户信息
 *
 * @author chendong
 */

public class QQUser extends SocialUser {

    ///////////////////////////////////////////////////////////////////////////
    // {
    //    "is_yellow_year_vip":"0",
    //    "ret":0,
    //    "figureurl_qq_1":"http://q.qlogo.cn/qqapp/1104910200/32843B53FCC1AFE63920DBA0C6745A72/40",
    //    "figureurl_qq_2":"http://q.qlogo.cn/qqapp/1104910200/32843B53FCC1AFE63920DBA0C6745A72/100",
    //    "nickname":"_芭君��",
    //    "yellow_vip_level":"0",
    //    "is_lost":0,
    //    "msg":"",
    //    "city":"宁波",
    //    "figureurl_1":"http://qzapp.qlogo.cn/qzapp/1104910200/32843B53FCC1AFE63920DBA0C6745A72/50",
    //    "vip":"0",
    //    "level":"0",
    //    "figureurl_2":"http://qzapp.qlogo.cn/qzapp/1104910200/32843B53FCC1AFE63920DBA0C6745A72/100",
    //    "province":"浙江",
    //    "is_yellow_vip":"0",
    //    "gender":"女",
    //    "figureurl":"http://qzapp.qlogo.cn/qzapp/1104910200/32843B53FCC1AFE63920DBA0C6745A72/30"
    // }
    ///////////////////////////////////////////////////////////////////////////
    private String openId;
    private int    ret;
    private String msg;
    private int    is_lost;
    private String nickname;
    private String gender;
    private String province;
    private String city;
    private String figureurl;
    private String figureurl_1;
    private String figureurl_2;
    private String figureurl_qq_1;// 50.的用户头像
    private String figureurl_qq_2;// 100.用户头像
    private String is_yellow_vip;
    private String vip;
    private String yellow_vip_level;
    private String level;
    private String is_yellow_year_vip;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getIs_lost() {
        return is_lost;
    }

    public void setIs_lost(int is_lost) {
        this.is_lost = is_lost;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getFigureurl() {
        return figureurl;
    }

    public void setFigureurl(String figureurl) {
        this.figureurl = figureurl;
    }

    public String getFigureurl_1() {
        return figureurl_1;
    }

    public void setFigureurl_1(String figureurl_1) {
        this.figureurl_1 = figureurl_1;
    }

    public String getFigureurl_2() {
        return figureurl_2;
    }

    public void setFigureurl_2(String figureurl_2) {
        this.figureurl_2 = figureurl_2;
    }

    public String getFigureurl_qq_1() {
        return figureurl_qq_1;
    }

    public void setFigureurl_qq_1(String figureurl_qq_1) {
        this.figureurl_qq_1 = figureurl_qq_1;
    }

    public String getFigureurl_qq_2() {
        return figureurl_qq_2;
    }

    public void setFigureurl_qq_2(String figureurl_qq_2) {
        this.figureurl_qq_2 = figureurl_qq_2;
    }

    public String getIs_yellow_vip() {
        return is_yellow_vip;
    }

    public void setIs_yellow_vip(String is_yellow_vip) {
        this.is_yellow_vip = is_yellow_vip;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getYellow_vip_level() {
        return yellow_vip_level;
    }

    public void setYellow_vip_level(String yellow_vip_level) {
        this.yellow_vip_level = yellow_vip_level;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getIs_yellow_year_vip() {
        return is_yellow_year_vip;
    }

    public void setIs_yellow_year_vip(String is_yellow_year_vip) {
        this.is_yellow_year_vip = is_yellow_year_vip;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String toString() {
        return "QQUserInfo{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", is_lost=" + is_lost +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", figureurl='" + figureurl + '\'' +
                ", figureurl_1='" + figureurl_1 + '\'' +
                ", figureurl_2='" + figureurl_2 + '\'' +
                ", figureurl_qq_1='" + figureurl_qq_1 + '\'' +
                ", figureurl_qq_2='" + figureurl_qq_2 + '\'' +
                ", is_yellow_vip='" + is_yellow_vip + '\'' +
                ", vip='" + vip + '\'' +
                ", yellow_vip_level='" + yellow_vip_level + '\'' +
                ", level='" + level + '\'' +
                ", is_yellow_year_vip='" + is_yellow_year_vip + '\'' +
                '}';
    }

    @Override
    public String getUserId() {
        return openId;
    }

    @Override
    public String getUserNickName() {
        return nickname;
    }

    @Override
    public int getUserGender() {
        if ("女".equals(gender)) {
            return GENDER_GIRL;
        }
        if ("男".equals(gender)) {
            return GENDER_BOY;
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
        return figureurl_qq_1;
    }

    @Override
    public String getUserHeadUrlLarge() {
        return figureurl_qq_2;
    }


}
