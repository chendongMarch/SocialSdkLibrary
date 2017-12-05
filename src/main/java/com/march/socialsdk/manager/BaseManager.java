package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.helper.CommonHelper;
import com.march.socialsdk.model.SocialSdkConfig;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.platform.weibo.WbPlatform;
import com.march.socialsdk.platform.tencent.QQPlatform;
import com.march.socialsdk.platform.wechat.WxPlatform;

/**
 * CreateAt : 2017/5/19
 * Describe : manager 基类
 *
 * @author chendong
 */
public abstract class BaseManager {

    public static final int INVALID_PARAM = -1;

    public static final int ACTION_TYPE_LOGIN = 0;
    public static final int ACTION_TYPE_SHARE = 1;

    public static final String KEY_SHARE_MEDIA_OBJ = "KEY_SHARE_MEDIA_OBJ"; // media obj key
    public static final String KEY_ACTION_TYPE = "KEY_ACTION_TYPE"; // action type

    public static final String KEY_SHARE_TARGET = "KEY_SHARE_TARGET"; // share target
    public static final String KEY_LOGIN_TARGET = "KEY_LOGIN_TARGET"; // login target

    private static IPlatform mPlatform;

    static IPlatform buildPlatform(Context activity, int target) {
        if (SocialSdk.getConfig() == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + " SocialSdl.init() request");
        }
        mPlatform = null;
        switch (target) {
            case Target.LOGIN_QQ:
            case Target.SHARE_QQ_FRIENDS:
            case Target.SHARE_QQ_ZONE:
                initQQPlatform(activity);
                break;
            case Target.LOGIN_WX:
            case Target.SHARE_WX_FRIENDS:
            case Target.SHARE_WX_ZONE:
            case Target.SHARE_WX_FAVORITE:
                initWxPlatform(activity);
                break;
            case Target.LOGIN_WB:
            case Target.SHARE_WB_NORMAL:
            case Target.SHARE_WB_OPENAPI:
                initSinaPlatform(activity);
                break;
            default:
                initQQPlatform(activity);
                break;
        }
        if (mPlatform == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + "  创建platform失败，请检查参数 " + SocialSdk.getConfig().toString());
        }
        return mPlatform;
    }

    public static IPlatform getPlatform() {
        return mPlatform;
    }

    private static void initQQPlatform(Context context) {
        SocialSdkConfig config = SocialSdk.getConfig();
        if (!CommonHelper.isAnyEmpty(config.getQqAppId(), config.getAppName()))
            mPlatform = new QQPlatform(context, config.getQqAppId(), config.getAppName());
    }

    private static void initWxPlatform(Context context) {
        SocialSdkConfig config = SocialSdk.getConfig();
        if (!CommonHelper.isAnyEmpty(config.getWxAppId(), config.getWxSecretKey()))
            mPlatform = new WxPlatform(context, config.getWxAppId(), config.getWxSecretKey(), config.getAppName());
    }

    private static void initSinaPlatform(Context context) {
        SocialSdkConfig config = SocialSdk.getConfig();
        if (!CommonHelper.isAnyEmpty(config.getSinaAppId(), config.getAppName()
                , config.getSinaRedirectUrl(), config.getSinaScope()))
            mPlatform = new WbPlatform(context, config.getSinaAppId(), config.getAppName()
                    , config.getSinaRedirectUrl(), config.getSinaScope());
    }

    private static void recycler() {
        if (mPlatform != null) {
            mPlatform.recycle();
            mPlatform = null;
        }
    }

    static void finishProcess(Activity activity) {
        recycler();
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }


}
