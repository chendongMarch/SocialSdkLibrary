package com.zfy.social.core.manager;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.zfy.social.core.platform.IPlatform;

/**
 * CreateAt : 2018/12/21
 * Describe : 获取平台对象
 *
 * @author chendong
 */
public class PlatformManager {

    @CheckResult
    public static @NonNull
    IPlatform getPlatform(Context context, int target) {
        return GlobalPlatform.newPlatformByTarget(context, target);
    }

    public static boolean isInstall(Context context, int target) {
        IPlatform iPlatform = GlobalPlatform.newPlatformByTarget(context, target);
        boolean install = iPlatform.isInstall(context);
        iPlatform.recycle();
        return install;
    }

}
