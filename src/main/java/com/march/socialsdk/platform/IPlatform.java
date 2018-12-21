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

    /**
     * 检测参数配置是否正确
     * @return 参数是否合法
     */
    boolean checkPlatformConfig();

    /**
     * 初始化分享监听
     * @param listener 分享回调
     */
    void initOnShareListener(OnShareListener listener);

    /**
     * @param context 上下文
     * @return 是否安装了 app
     */
    boolean isInstall(Context context);

    /**
     * 发起登录
     * @param activity act
     * @param onLoginListener 登录回调
     */
    void login(Activity activity, OnLoginListener onLoginListener);

    /**
     * 发起分享
     * @param activity act
     * @param shareTarget 分享目标
     * @param shareObj 分享对象
     */
    void share(Activity activity, int shareTarget, ShareObj shareObj);

}
