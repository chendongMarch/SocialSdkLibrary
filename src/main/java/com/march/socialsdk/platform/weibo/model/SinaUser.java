
package com.march.socialsdk.platform.weibo.model;

import com.march.socialsdk.model.user.SocialUser;

/**
 * 用户信息结构体。
 *
 * @author SINA
 * @since 2013-11-24
 */
public class SinaUser extends SocialUser {

    ///////////////////////////////////////////////////////////////////////////
// {
//     "id": 5215607283,
//     "idstr": "5215607283",
//     "class": 1,
//     "screen_name": "chbf8853",
//     "name": "chbf8853",
//     "province": "11",
//     "city": "5",
//     "location": "北京 朝阳区",
//     "description": "测试测试测试",
//     "url": "",
//     "profile_image_url": "http://tva2.sinaimg.cn/crop.0.0.1006.1006.50/005GY9I7jw8fah6n7bo0wj30ry0rydjo.jpg",
//     "cover_image_phone": "http://ww1.sinaimg.cn/crop.0.0.640.640.640/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg",
//     "profile_url": "u/5215607283",
//     "domain": "",
//     "weihao": "",
//     "gender": "m",
//     "followers_count": 12,
//     "friends_count": 29,
//     "pagefriends_count": 0,
//     "statuses_count": 158,
//     "favourites_count": 0,
//     "created_at": "Sun Jul 13 10:51:57 +0800 2014",
//     "following": false,
//     "allow_all_act_msg": false,
//     "geo_enabled": true,
//     "verified": false,
//     "verified_type": -1,
//     "remark": "",
//
//     "status": {
//        "created_at": "Wed Mar 15 23:30:44 +0800 2017",
//        "id": 4085706685258539,
//        "mid": "4085706685258539",
//        "idstr": "4085706685258539",
//        "text": "转发微博",
//        "source_allowclick": 1,
//        "source_type": 1,
//        "source": "<a href=\"http://app.weibo.com/t/feed/66wUF7\" rel=\"nofollow\">乐2 Pro</a>",
//        "favorited": false,
//        "truncated": false,
//        "in_reply_to_status_id": "",
//        "in_reply_to_user_id": "",
//        "in_reply_to_screen_name": "",
//        "pic_urls": [],
//
//        "ptype": 0,
//        "allow_all_comment": true,
//        "avatar_large": "http://tva2.sinaimg.cn/crop.0.0.1006.1006.180/005GY9I7jw8fah6n7bo0wj30ry0rydjo.jpg",
//        "avatar_hd": "http://tva2.sinaimg.cn/crop.0.0.1006.1006.1024/005GY9I7jw8fah6n7bo0wj30ry0rydjo.jpg",
//        "verified_reason": "",
//        "verified_trade": "",
//        "verified_reason_url": "",
//        "verified_source": "",
//        "verified_source_url": "",
//        "follow_me": false,
//        "online_status": 0,
//        "bi_followers_count": 3,
//        "lang": "zh-cn",
//        "star": 0,
//        "mbtype": 0,
//        "mbrank": 0,
//        "block_word": 0,
//        "block_app": 0,
//        "credit_score": 80,
//        "user_ability": 0,
//        "urank": 9
//}
///////////////////////////////////////////////////////////////////////////
    // 用户UID（int64）
    private String  id;
    // 字符串型的用户 UID
    private String  idstr;
    // 用户昵称
    private String  screen_name;
    // 友好显示名称
    private String  name;
    // 用户所在省级ID
    private int     province;
    // 用户所在城市ID
    private int     city;
    // 用户所在地
    private String  location;
    // 用户个人描述
    private String  description;
    // 用户博客地址
    private String  url;
    // 用户头像地址，50×50像素
    private String  profile_image_url;
    // 用户的微博统一URL地址
    private String  profile_url;
    // 用户的个性化域名
    private String  domain;
    //
    private String  weihao;
    // 性别，m：男、f：女、n：未知
    private String  gender;
    // 粉丝数
    private int     followers_count;
    // 关注数
    private int     friends_count;
    // 微博数
    private int     statuses_count;
    // 收藏数
    private int     favourites_count;
    // 用户创建（注册）时间
    private String  created_at;
    // 暂未支持
    private boolean following;
    // 是否允许所有人给我发私信，true：是，false：否
    private boolean allow_all_act_msg;
    // 是否允许标识用户的地理位置，true：是，false：否
    private boolean geo_enabled;
    // 是否是微博认证用户，即加V用户，true：是，false：否
    private boolean verified;
    // 暂未支持
    private int     verified_type;
    // 用户备注信息，只有在查询用户关系时才返回此字段
    private String  remark;
    // 用户的最近一条微博信息字段
    // private Status status;

    // 是否允许所有人对我的微博进行评论，true：是，false：否
    private boolean allow_all_comment;
    // 用户大头像地址
    private String  avatar_large;
    // 用户高清大头像地址
    private String  avatar_hd;
    // 认证原因
    private String  verified_reason;
    // 该用户是否关注当前登录用户，true：是，false：否
    private boolean follow_me;
    // 用户的在线状态，0：不在线、1：在线
    private int     online_status;
    // 用户的互粉数
    private int     bi_followers_count;
    // 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语
    private String  lang;

    /**
     * 注意：以下字段暂时不清楚具体含义，OpenAPI 说明文档暂时没有同步更新对应字段
     */
    private String star;
    private String mbtype;
    private String mbrank;
    private String block_word;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWeihao() {
        return weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(int friends_count) {
        this.friends_count = friends_count;
    }

    public int getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(int statuses_count) {
        this.statuses_count = statuses_count;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public void setFavourites_count(int favourites_count) {
        this.favourites_count = favourites_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isAllow_all_act_msg() {
        return allow_all_act_msg;
    }

    public void setAllow_all_act_msg(boolean allow_all_act_msg) {
        this.allow_all_act_msg = allow_all_act_msg;
    }

    public boolean isGeo_enabled() {
        return geo_enabled;
    }

    public void setGeo_enabled(boolean geo_enabled) {
        this.geo_enabled = geo_enabled;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getVerified_type() {
        return verified_type;
    }

    public void setVerified_type(int verified_type) {
        this.verified_type = verified_type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isAllow_all_comment() {
        return allow_all_comment;
    }

    public void setAllow_all_comment(boolean allow_all_comment) {
        this.allow_all_comment = allow_all_comment;
    }

    public String getAvatar_large() {
        return avatar_large;
    }

    public void setAvatar_large(String avatar_large) {
        this.avatar_large = avatar_large;
    }

    public String getAvatar_hd() {
        return avatar_hd;
    }

    public void setAvatar_hd(String avatar_hd) {
        this.avatar_hd = avatar_hd;
    }

    public String getVerified_reason() {
        return verified_reason;
    }

    public void setVerified_reason(String verified_reason) {
        this.verified_reason = verified_reason;
    }

    public boolean isFollow_me() {
        return follow_me;
    }

    public void setFollow_me(boolean follow_me) {
        this.follow_me = follow_me;
    }

    public int getOnline_status() {
        return online_status;
    }

    public void setOnline_status(int online_status) {
        this.online_status = online_status;
    }

    public int getBi_followers_count() {
        return bi_followers_count;
    }

    public void setBi_followers_count(int bi_followers_count) {
        this.bi_followers_count = bi_followers_count;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getMbtype() {
        return mbtype;
    }

    public void setMbtype(String mbtype) {
        this.mbtype = mbtype;
    }

    public String getMbrank() {
        return mbrank;
    }

    public void setMbrank(String mbrank) {
        this.mbrank = mbrank;
    }

    public String getBlock_word() {
        return block_word;
    }

    public void setBlock_word(String block_word) {
        this.block_word = block_word;
    }

    @Override
    public String toString() {
        return "WbUserInfo{" +
                "id='" + id + '\'' +
                ", idstr='" + idstr + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", name='" + name + '\'' +
                ", province=" + province +
                ", city=" + city +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", profile_image_url='" + profile_image_url + '\'' +
                ", profile_url='" + profile_url + '\'' +
                ", domain='" + domain + '\'' +
                ", weihao='" + weihao + '\'' +
                ", gender='" + gender + '\'' +
                ", followers_count=" + followers_count +
                ", friends_count=" + friends_count +
                ", statuses_count=" + statuses_count +
                ", favourites_count=" + favourites_count +
                ", created_at='" + created_at + '\'' +
                ", following=" + following +
                ", allow_all_act_msg=" + allow_all_act_msg +
                ", geo_enabled=" + geo_enabled +
                ", verified=" + verified +
                ", verified_type=" + verified_type +
                ", remark='" + remark + '\'' +
                ", allow_all_comment=" + allow_all_comment +
                ", avatar_large='" + avatar_large + '\'' +
                ", avatar_hd='" + avatar_hd + '\'' +
                ", verified_reason='" + verified_reason + '\'' +
                ", follow_me=" + follow_me +
                ", online_status=" + online_status +
                ", bi_followers_count=" + bi_followers_count +
                ", lang='" + lang + '\'' +
                ", star='" + star + '\'' +
                ", mbtype='" + mbtype + '\'' +
                ", mbrank='" + mbrank + '\'' +
                ", block_word='" + block_word + '\'' +
                '}';
    }

    @Override
    public String getUserId() {
        return id;
    }

    @Override
    public String getUserNickName() {
        return screen_name;
    }

    @Override
    public int getUserGender() {
        if ("m".equals(gender))
            return GENDER_BOY;
        if ("f".equals(gender))
            return GENDER_GIRL;
        return GENDER_UNKONW;
    }

    @Override
    public String getUserProvince() {
        return province + "";
    }

    @Override
    public String getUserCity() {
        return city + "";
    }

    @Override
    public String getUserHeadUrl() {
        return avatar_large;
    }

    @Override
    public String getUserHeadUrlLarge() {
        return avatar_hd;
    }
}
