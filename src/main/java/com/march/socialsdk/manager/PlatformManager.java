package com.march.socialsdk.manager;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.march.socialsdk.platform.IPlatform;

/**
 * CreateAt : 2018/12/21
 * Describe :
 *
 * @author chendong
 */
public class PlatformManager {

    @CheckResult
    public static @NonNull
    IPlatform getPlatform(Context context, int target) {
        return GlobalPlatform.makePlatform(context, target);
    }

    public static void recycle() {
        GlobalPlatform.release(null);
    }

}
