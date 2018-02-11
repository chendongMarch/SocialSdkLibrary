package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.Target;

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

    private static IPlatform sPlatform;

    static IPlatform buildPlatform(Context activity, int target) {
        if (SocialSdk.getConfig() == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + " SocialSdl.init() request");
        }

        sPlatform = SocialSdk.getPlatform(activity,target);

        if (sPlatform == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + "  创建platform失败，请检查参数 " + SocialSdk.getConfig().toString());
        }
        return sPlatform;
    }

    public static IPlatform getCurrentPlatform() {
        return sPlatform;
    }

    static void finishProcess(Activity activity) {
        if (sPlatform != null) {
            sPlatform.recycle();
            sPlatform = null;
        }
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }
}
