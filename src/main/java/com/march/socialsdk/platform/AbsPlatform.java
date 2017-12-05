package com.march.socialsdk.platform;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.helper.IntentShareHelper;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareObj;
import com.tencent.tauth.UiError;

/**
 * CreateAt : 2016/12/3
 * Describe : platform基类
 *
 * @author chendong
 */
public abstract class AbsPlatform implements IPlatform {

    public static final String TAG = AbsPlatform.class.getSimpleName();

    protected static final int THUMB_IMAGE_SIZE = 32 * 1024;

    protected OnShareListener mOnShareListener;
    protected String mAppId = "";
    protected String mAppName = null;
    protected Context mContext;

    protected AbsPlatform(Context context, String appId, String appName) {
        this.mAppId = appId;
        this.mAppName = appName;
        this.mContext = context;
    }

    @Override
    public boolean checkPlatformConfig() {
        return !TextUtils.isEmpty(mAppId) && !TextUtils.isEmpty(mAppName) && mContext != null;
    }

    @Override
    public void initOnShareListener(OnShareListener listener) {
        this.mOnShareListener = listener;
    }

    @Override
    public boolean isInstall() {
        return false;
    }

    @Override
    public void login(Activity activity, OnLoginListener onLoginListener) {

    }

    @Override
    public void share(Activity activity, int shareTarget, ShareObj shareMediaObj) {
        if (shareMediaObj == null) return;
        switch (shareMediaObj.getShareObjType()) {
            case ShareObj.SHARE_OPEN_APP:
                shareOpenApp(shareTarget, activity, shareMediaObj);
                break;
            case ShareObj.SHARE_TYPE_TEXT:
                shareText(shareTarget, activity, shareMediaObj);
                break;
            case ShareObj.SHARE_TYPE_IMAGE:
                shareImage(shareTarget, activity, shareMediaObj);
                break;
            case ShareObj.SHARE_TYPE_APP:
                shareApp(shareTarget, activity, shareMediaObj);
                break;
            case ShareObj.SHARE_TYPE_WEB:
                shareWeb(shareTarget, activity, shareMediaObj);
                break;
            case ShareObj.SHARE_TYPE_MUSIC:
                shareMusic(shareTarget, activity, shareMediaObj);
                break;
            case ShareObj.SHARE_TYPE_VIDEO:
                shareVideo(shareTarget, activity, shareMediaObj);
                break;
            case ShareObj.SHARE_TYPE_VOICE:
                shareVoice(shareTarget, activity, shareMediaObj);
                break;
        }
    }

    protected void shareVideoByIntent(Activity activity, ShareObj obj, String pkg, String page) {
        try {
            IntentShareHelper.shareVideo(activity, obj.getMediaPath(), pkg, page);
        } catch (Exception e) {
            if (this.mOnShareListener != null) {
                this.mOnShareListener.onFailure(new SocialException(SocialException.CODE_SHARE_BY_INTENT_FAIL, e));
            }
        }
    }

    protected void shareTextByIntent(Activity activity, ShareObj obj, String pkg, String page) {
        try {
            IntentShareHelper.shareText(activity, obj.getTitle(), obj.getSummary(), pkg, page);
        } catch (Exception e) {
            if (this.mOnShareListener != null) {
                this.mOnShareListener.onFailure(new SocialException(SocialException.CODE_SHARE_BY_INTENT_FAIL, e));
            }
        }
    }

    protected abstract void shareOpenApp(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareText(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareImage(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareApp(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareWeb(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareMusic(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareVideo(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareVoice(int shareTarget, Activity activity, ShareObj obj);


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
