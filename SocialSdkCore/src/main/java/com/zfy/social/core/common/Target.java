package com.zfy.social.core.common;

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

    public static final int PLATFORM_QQ = 100; // qq 登录
    public static final int PLATFORM_WX = 101; // 微信登录
    public static final int PLATFORM_WB = 102; // 微博登录
    public static final int PLATFORM_DD = 103; // 微博登录
    public static final int PLATFORM_SMS = 104; // 短信
    public static final int PLATFORM_CLIPBOARD = 105; // 粘贴板
    public static final int PLATFORM_EMAIL = 106; // 邮件

    public static final int LOGIN_QQ = 200; // qq 登录
    public static final int LOGIN_WX = 201; // 微信登录
    public static final int LOGIN_WB = 202; // 微博登录
    public static final int LOGIN_WX_SCAN = 203; // 微信扫码登录


    public static final int SHARE_QQ_FRIENDS = 300; // qq好友
    public static final int SHARE_QQ_ZONE = 301; // qq空间
    public static final int SHARE_WX_FRIENDS = 302; // 微信好友
    public static final int SHARE_WX_ZONE = 303; // 微信朋友圈
    public static final int SHARE_WX_FAVORITE = 304; // 微信收藏
    public static final int SHARE_WB = 305; // 新浪微博
    public static final int SHARE_DD = 306; // 钉钉分享
    public static final int SHARE_SMS = 307; // 短信分享
    public static final int SHARE_EMAIL = 308; // 邮件分享
    public static final int SHARE_CLIPBOARD = 309; // 粘贴板分享


    @IntDef({Target.SHARE_QQ_FRIENDS, Target.SHARE_QQ_ZONE,
            Target.SHARE_WX_FRIENDS, Target.SHARE_WX_ZONE, Target.SHARE_WX_FAVORITE,
            Target.SHARE_WB, Target.SHARE_DD,
            Target.SHARE_EMAIL, Target.SHARE_CLIPBOARD, Target.SHARE_SMS,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShareTarget {

    }


    @IntDef({Target.LOGIN_QQ, Target.LOGIN_WB, Target.LOGIN_WX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginTarget {

    }

    @IntDef({Target.PLATFORM_WX, Target.PLATFORM_QQ,
            Target.PLATFORM_WB, Target.PLATFORM_DD,
            Target.PLATFORM_SMS, Target.PLATFORM_EMAIL, Target.PLATFORM_CLIPBOARD,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlatformTarget {

    }


    public static String toDesc(int target) {
        String result;
        switch (target) {
            case Target.LOGIN_QQ:
                result = "qq登录";
                break;
            case Target.LOGIN_WX:
                result = "微信登录";
                break;
            case Target.LOGIN_WB:
                result = "微博登录";
                break;
            case Target.SHARE_QQ_FRIENDS:
                result = "qq好友分享";
                break;
            case Target.SHARE_QQ_ZONE:
                result = "qq空间分享";
                break;
            case Target.SHARE_WX_FRIENDS:
                result = "微信好友分享";
                break;
            case Target.SHARE_WX_ZONE:
                result = "微信空间分享";
                break;
            case Target.SHARE_WB:
                result = "微博普通分享";
                break;
            case Target.SHARE_DD:
                result = "钉钉分享";
                break;
            case Target.SHARE_EMAIL:
                result = "邮件分享";
                break;
            case Target.SHARE_SMS:
                result = "短信分享";
                break;
            case Target.SHARE_CLIPBOARD:
                result = "粘贴板分享";
                break;
            default:
                result = "未知类型";
                break;
        }
        return result;
    }
}
