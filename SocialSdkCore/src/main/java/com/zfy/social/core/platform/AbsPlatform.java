package com.zfy.social.core.platform;


import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnShareListener;
import com.zfy.social.core.manager.GlobalPlatform;
import com.zfy.social.core.model.ShareObj;

/**
 * CreateAt : 2016/12/3
 * Describe : platform基类
 *
 * @author chendong
 */
public abstract class AbsPlatform implements IPlatform {

    public static final String TAG = AbsPlatform.class.getSimpleName();

    protected static final int THUMB_IMAGE_SIZE_32 = 32 * 1024;
    protected static final int THUMB_IMAGE_SIZE_128 = 128 * 1024;

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

    public boolean checkPlatformConfig() {
        return !TextUtils.isEmpty(mAppId) && !TextUtils.isEmpty(mAppName);
    }

    @Override
    public void initOnShareListener(OnShareListener listener) {
        this.mOnShareListener = listener;
    }

    @Override
    public void share(Activity activity, int shareTarget, ShareObj shareObj) {
        if (shareObj == null) {
            mOnShareListener.onFailure(SocialError.make(SocialError.CODE_PARAM_ERROR, "obj is null"));
            return;
        }
        dispatchShare(activity, shareTarget, shareObj);
    }

    /**
     * 分享功能具体实现，由子类接管，方便更好的管理不兼容的类型
     *
     * @param shareTarget 分享的目标 {@link Target}
     * @param activity    activity
     * @param obj         ShareObj
     */
    protected abstract void dispatchShare(Activity activity, int shareTarget, ShareObj obj);


    @Override
    public void actionShare(Activity activity, int shareTarget, ShareObj shareObj) {
        Intent intent = new Intent(activity, getUIKitClazz());
        intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_SHARE);
        intent.putExtra(GlobalPlatform.KEY_SHARE_MEDIA_OBJ, shareObj);
        intent.putExtra(GlobalPlatform.KEY_SHARE_TARGET, shareTarget);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }


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
