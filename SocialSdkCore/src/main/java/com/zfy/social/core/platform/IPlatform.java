package com.zfy.social.core.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.listener.OnShareStateListener;
import com.zfy.social.core.listener.Recyclable;
import com.zfy.social.core.model.LoginObj;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.uikit.BaseActionActivity;

/**
 * CreateAt : 2016/12/28
 * Describe : 平台接口协议
 *
 * @author chendong
 */

public interface IPlatform extends Recyclable {

    void onActivityResult(BaseActionActivity activity, int requestCode, int resultCode, Intent data);


    /**
     * @return 获取交互的 Activity 的 class
     */
     Class getUIKitClazz();

    /**
     * 接收 Activity Intent
     *
     * @param intent intent
     */
    void handleIntent(Activity intent);

    /**
     * 接收登录分享结果
     *
     * @param resp resp
     */
    void onResponse(Object resp);

    /**
     * 初始化分享监听
     *
     * @param listener 分享回调
     */
    void initOnShareListener(OnShareStateListener listener);

    /**
     * @param context 上下文
     * @return 是否安装了 app
     */
    boolean isInstall(Context context);


    /**
     * 发起登录
     *
     * @param act        act
     * @param listener 登录回调
     */
    void login(Activity act, int target, LoginObj obj, OnLoginStateListener listener);

    /**
     * 发起分享
     *
     * @param activity    act
     * @param shareTarget 分享目标
     * @param shareObj    分享对象
     */
    void share(Activity activity, int shareTarget, ShareObj shareObj);

}
