package com.march.socialsdk.platform;

import android.app.Activity;
import android.content.Context;

import com.march.socialsdk.listener.PlatformLifecycle;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareObj;

/**
 * CreateAt : 2016/12/28
 * Describe : 平台接口协议
 *
 * @author chendong
 */

public interface IPlatform extends PlatformLifecycle {

    // 检测参数配置
    boolean checkPlatformConfig();

    // 初始化分享监听
    void initOnShareListener(OnShareListener listener);

    // 是否安装
    boolean isInstall(Context context);

    // 发起登录
    void login(Activity activity, OnLoginListener onLoginListener);

    // 发起分享
    void share(Activity activity, int shareTarget, ShareObj shareMediaObj);

}
