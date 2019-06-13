package com.zfy.social.core.platform.system;

import android.content.Context;

import com.zfy.social.core.platform.AbsPlatform;

/**
 * CreateAt : 2018/12/26
 * Describe :
 *
 * @author chendong
 */
public abstract class SystemPlatform extends AbsPlatform {


    public SystemPlatform(Context context, String appId, String appName, int target) {
        super(context, appId, appName, target);
    }

    @Override
    public Class getUIKitClazz() {
        return null;
    }

    @Override
    public boolean isInstall(Context context) {
        return true;
    }


    @Override
    public void recycle() {

    }
}
