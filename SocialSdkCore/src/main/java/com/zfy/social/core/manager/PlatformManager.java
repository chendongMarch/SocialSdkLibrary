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
        return GlobalPlatform.makePlatform(context, target);
    }

}
