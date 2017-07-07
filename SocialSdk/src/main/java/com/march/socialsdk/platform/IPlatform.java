package com.march.socialsdk.platform;

import android.app.Activity;

import com.march.socialsdk.listener.PlatformLifeCircle;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareMediaObj;

/**
 * CreateAt : 2016/12/28
 * Describe : 平台接口协议
 *
 * @author chendong
 */

public interface IPlatform extends PlatformLifeCircle {

    void initOnShareListener(OnShareListener listener);

    boolean isInstall();

    void login(Activity activity, OnLoginListener onLoginListener);

    void share(Activity activity, int shareTarget, ShareMediaObj shareMediaObj);

}
