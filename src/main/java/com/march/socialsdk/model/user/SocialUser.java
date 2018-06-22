package com.march.socialsdk.model.user;

/**
 * CreateAt : 2017/5/19
 * Describe : user 基类
 *
 * @author chendong
 */
public abstract class SocialUser {

    public static final int GENDER_BOY  = 1;
    public static final int GENDER_GIRL = 2;
    public static final int GENDER_UNKONW = 0;

//    private String userId;
//    private String userNickName;
//    private int    userGender;
//    private String userGenderDesc;
//    private String userProvince;
//    private String userCity;
//    private String userHeadUrl;
//    private String userHeadUrlLarge;


    public abstract String getUserId();

    public abstract String getUserNickName();

    public abstract int getUserGender();

    public abstract String getUserProvince();

    public abstract String getUserCity();

    public abstract String getUserHeadUrl();

    public abstract String getUserHeadUrlLarge();


    @Override
    public String toString() {
        return "BaseUser{" +
                "userId='" + getUserId() + '\'' +
                ", userNickName='" + getUserNickName() + '\'' +
                ", userGender=" + getUserGender() +
                ", userProvince='" + getUserProvince() + '\'' +
                ", userCity='" + getUserCity() + '\'' +
                ", userHeadUrl='" + getUserHeadUrl() + '\'' +
                ", userHeadUrlLarge='" + getUserHeadUrlLarge() + '\'' +
                '}';
    }
}
