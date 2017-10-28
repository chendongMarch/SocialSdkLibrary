package com.march.socialsdk.platform;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CreateAt : 28/10/2017
 * Describe :
 *
 * @author chendong
 */
public class Target {

    public static final int LOGIN_QQ = 0x21; // qq 登录
    public static final int LOGIN_WX = 0x22; // 微信登录
    public static final int LOGIN_WB = 0x23; // 微博登录


    public static final int SHARE_QQ_FRIENDS  = 0x31; // qq好友
    public static final int SHARE_QQ_ZONE     = 0x32; // qq空间
    public static final int SHARE_WX_FRIENDS  = 0x33; // 微信好友
    public static final int SHARE_WX_ZONE     = 0x34; // 微信朋友圈
    public static final int SHARE_WX_FAVORITE = 0x35; // 微信收藏
    public static final int SHARE_WB_NORMAL   = 0x36; // 新浪微博
    public static final int SHARE_WB_OPENAPI  = 0x37; // 新浪微博openApi分享，暂不支持


    @IntDef({Target.SHARE_QQ_FRIENDS, Target.SHARE_QQ_ZONE,
                    Target.SHARE_WX_FRIENDS, Target.SHARE_WX_ZONE, Target.SHARE_WX_FAVORITE,
                    Target.SHARE_WB_NORMAL, Target.SHARE_WB_OPENAPI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShareTarget {

    }


    @IntDef({Target.LOGIN_QQ, Target.LOGIN_WB, Target.LOGIN_WX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginTarget {

    }
}
