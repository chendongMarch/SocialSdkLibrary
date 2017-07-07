package com.march.socialsdk.platform;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareMediaObj;

/**
 * CreateAt : 2016/12/3
 * Describe : platform基类
 *
 * @author chendong
 */
public abstract class BasePlatform implements IPlatform {

    public static final String TAG = BasePlatform.class.getSimpleName();

    protected static final int THUMB_IMAGE_SIZE = 32 * 1024;

    protected OnShareListener mOnShareListener;
    protected String mAppId   = "";
    protected String mAppName = null;
    protected Context mContext;

    @Override
    public void initOnShareListener(OnShareListener listener) {
        this.mOnShareListener = listener;
    }

    protected BasePlatform(Context context, String appId, String appName) {
        this.mAppId = appId;
        this.mAppName = appName;
        this.mContext = context;
    }

    public void login(Activity activity, OnLoginListener loginListener) {

    }

    @Override
    public void share(Activity activity, int shareTarget, ShareMediaObj shareMediaObj) {
        if (shareMediaObj == null) return;
        switch (shareMediaObj.getShareObjType()) {
            case ShareMediaObj.SHARE_OPEN_APP:
                shareOpenApp(shareTarget, activity, shareMediaObj);
                break;
            case ShareMediaObj.SHARE_TYPE_TEXT:
                shareText(shareTarget, activity, shareMediaObj);
                break;
            case ShareMediaObj.SHARE_TYPE_IMAGE:
                shareImage(shareTarget, activity, shareMediaObj);
                break;
            case ShareMediaObj.SHARE_TYPE_APP:
                shareApp(shareTarget, activity, shareMediaObj);
                break;
            case ShareMediaObj.SHARE_TYPE_WEB:
                shareWeb(shareTarget, activity, shareMediaObj);
                break;
            case ShareMediaObj.SHARE_TYPE_MUSIC:
                shareMusic(shareTarget, activity, shareMediaObj);
                break;
            case ShareMediaObj.SHARE_TYPE_VIDEO:
                shareVideo(shareTarget, activity, shareMediaObj);
                break;
            case ShareMediaObj.SHARE_TYPE_VOICE:
                shareVoice(shareTarget, activity, shareMediaObj);
                break;
        }
    }

    protected abstract void shareOpenApp(int shareTarget, Activity activity, ShareMediaObj obj);

    protected abstract void shareText(int shareTarget, Activity activity, ShareMediaObj obj);

    protected abstract void shareImage(int shareTarget, Activity activity, ShareMediaObj obj);

    protected abstract void shareApp(int shareTarget, Activity activity, ShareMediaObj obj);

    protected abstract void shareWeb(int shareTarget, Activity activity, ShareMediaObj obj);

    protected abstract void shareMusic(int shareTarget, Activity activity, ShareMediaObj obj);

    protected abstract void shareVideo(int shareTarget, Activity activity, ShareMediaObj obj);

    protected abstract void shareVoice(int shareTarget, Activity activity, ShareMediaObj obj);


    ///////////////////////////////////////////////////////////////////////////
    // life circle
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void onNewIntent(Activity activity) {

    }

    @Override
    public void onResponse(Object resp) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void recycle() {

    }
}
