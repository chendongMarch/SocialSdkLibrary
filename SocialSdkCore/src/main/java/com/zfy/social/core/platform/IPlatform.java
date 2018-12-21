package com.zfy.social.core.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zfy.social.core.listener.OnLoginListener;
import com.zfy.social.core.listener.OnShareListener;
import com.zfy.social.core.listener.Recyclable;
import com.zfy.social.core.model.ShareObj;

/**
 * CreateAt : 2016/12/28
 * Describe : 平台接口协议
 *
 * @author chendong
 */

public interface IPlatform extends Recyclable {

    /**
     * 接收 Activity onActivityResult
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

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
