package com.march.socialsdk.platform;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.util.IntentShareUtil;

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
    protected String          mAppId;
    protected String          mAppName;
    protected int             mTarget;

    public void setTarget(int target) {
        mTarget = target;
    }

    public AbsPlatform(String appId, String appName) {
        this.mAppId = appId;
        this.mAppName = appName;
    }

    @Override
    public boolean checkPlatformConfig() {
        return !TextUtils.isEmpty(mAppId) && !TextUtils.isEmpty(mAppName);
    }

    @Override
    public void initOnShareListener(OnShareListener listener) {
        this.mOnShareListener = listener;
    }

    @Override
    public boolean isInstall(Context context) {
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
        }
    }

    protected void shareVideoByIntent(Activity activity, ShareObj obj, String pkg, String page) {
        boolean result = IntentShareUtil.shareVideo(activity, obj.getMediaPath(), pkg, page);
        if (result) {
            this.mOnShareListener.onSuccess();
        } else {
            this.mOnShareListener.onFailure(new SocialError(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareVideo by intent" + pkg + "  " + page + " failure"));
        }
    }

    protected void shareTextByIntent(Activity activity, ShareObj obj, String pkg, String page) {
        boolean result = IntentShareUtil.shareText(activity, obj.getTitle(), obj.getSummary(), pkg, page);
        if (result) {
            this.mOnShareListener.onSuccess();
        } else {
            this.mOnShareListener.onFailure(new SocialError(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareText by intent" + pkg + "  " + page + " failure"));
        }
    }

    protected abstract void shareOpenApp(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareText(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareImage(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareApp(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareWeb(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareMusic(int shareTarget, Activity activity, ShareObj obj);

    protected abstract void shareVideo(int shareTarget, Activity activity, ShareObj obj);


    ///////////////////////////////////////////////////////////////////////////
    // life circle
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void handleIntent(Activity activity) {

    }

    @Override
    public void onResponse(Object resp) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
