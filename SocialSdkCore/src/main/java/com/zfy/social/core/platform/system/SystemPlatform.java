package com.zfy.social.core.platform.system;

import android.app.Activity;
import android.content.Context;

import com.zfy.social.core.listener.OnLoginListener;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.AbsPlatform;

/**
 * CreateAt : 2018/12/26
 * Describe :
 *
 * @author chendong
 */
public abstract class SystemPlatform extends AbsPlatform {

    public SystemPlatform(String appId, String appName) {
        super(appId, appName);
    }

    @Override
    public void actionShare(Activity activity, int shareTarget, ShareObj shareObj) {
        dispatchShare(activity, shareTarget, shareObj);
    }

    @Override
    public Class getUIKitClazz() {
        return null;
    }

    @Override
    public boolean isInstall(Context context) {
        return false;
    }

    @Override
    public void login(Activity activity, OnLoginListener onLoginListener) {

    }

    @Override
    public void recycle() {

    }
}
