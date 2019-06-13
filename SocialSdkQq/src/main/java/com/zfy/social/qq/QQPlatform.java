package com.zfy.social.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zfy.social.core.SocialOptions;
import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.model.LoginObj;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.AbsPlatform;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.uikit.BaseActionActivity;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.IntentShareUtil;
import com.zfy.social.core.util.SocialUtil;
import com.zfy.social.qq.uikit.QQActionActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2016/12/3
 * Describe : qq平台
 * 问题汇总：com.tencentApi.tauth.AuthActivity 需要添加（data android:scheme="tencent110557146"）否则会一直返回分享取消
 * qq空间支持本地视频分享，网络视频使用web形式分享
 * qq好友不支持本地视频分享，支持网络视频分享
 *
 * 登录分享文档 http://wiki.open.qq.com/wiki/QQ%E7%94%A8%E6%88%B7%E8%83%BD%E5%8A%9B
 * @author chendong
 */
public class QQPlatform extends AbsPlatform {

    public static final String TAG = QQPlatform.class.getSimpleName();

    private Tencent mTencentApi;
    private QQLoginHelper mQQLoginHelper;
    private IUiListenerWrap mIUiListenerWrap;

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            SocialOptions config = SocialSdk.opts();
            if (!SocialUtil.isAnyEmpty(config.getQqAppId(), config.getAppName())) {
                platform = new QQPlatform(context, config.getQqAppId(), config.getAppName(), target);
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_QQ;
        }


        @Override
        public boolean checkShareTarget(int shareTarget) {
            return shareTarget == Target.SHARE_QQ_FRIENDS
                    || shareTarget == Target.SHARE_QQ_ZONE;
        }

        @Override
        public boolean checkLoginTarget(int loginTarget) {
            return loginTarget == Target.LOGIN_QQ;
        }
    }

    private QQPlatform(Context context, String appId, String appName, int target) {
        super(context, appId, appName, target);
        mTencentApi = Tencent.createInstance(appId, context);
        mIUiListenerWrap = new IUiListenerWrap();
    }


    @Override
    public Class getUIKitClazz() {
        return QQActionActivity.class;
    }

    @Override
    public void recycle() {
        mTencentApi.releaseResource();
        mTencentApi = null;
    }

    @Override
    public boolean isInstall(Context context) {
        return isQQInstalled(context);
    }

    // sdk 里面的方法，没法判断 tim 和 qq 轻聊版
    public boolean isQQInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        List packages = pm.getInstalledPackages(0);
        if (packages == null) {
            return false;
        }
        for (int i = 0; i < packages.size(); ++i) {
            String pkName = ((PackageInfo) packages.get(i)).packageName;
            if (SocialUtil.isAnyEq(pkName, SocialValues.QQ_PKG, SocialValues.TIM_PKG, SocialValues.QQLITE_PKG)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(BaseActionActivity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            if (mIUiListenerWrap != null)
                Tencent.handleResultData(data, mIUiListenerWrap);
        } else if (requestCode == Constants.REQUEST_LOGIN) {
            if (mQQLoginHelper != null)
                mQQLoginHelper.handleResultData(data);
        }
    }

    @Override
    public void login(Activity act, int target, LoginObj obj, OnLoginStateListener listener) {
        if (!mTencentApi.isSupportSSOLogin(act)) {
            // 下载最新版
            listener.onState(null, LoginResult.failOf(target, SocialError.make(SocialError.CODE_VERSION_LOW)));
            return;
        }
        mQQLoginHelper = new QQLoginHelper(act, mTencentApi, listener);
        mQQLoginHelper.login();
    }

    @Override
    protected void dispatchShare(Activity activity, int shareTarget, ShareObj obj) {
        switch (obj.getType()) {
            case ShareObj.SHARE_TYPE_OPEN_APP:
                shareOpenApp(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_TEXT:
                shareText(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_IMAGE:
                shareImage(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_APP:
                shareApp(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_WEB:
                shareWeb(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_MUSIC:
                shareMusic(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_VIDEO:
                shareVideo(shareTarget, activity, obj);
                break;
        }
    }


    private Bundle buildCommonBundle(String title, String summary, String targetUrl, int shareTarget) {
        final Bundle params = new Bundle();
        if (!TextUtils.isEmpty(title))
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        if (!TextUtils.isEmpty(summary))
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        if (!TextUtils.isEmpty(targetUrl))
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        if (!TextUtils.isEmpty(mAppName))
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mAppName);
        // 加了这个会自动打开qq空间发布
        if (shareTarget == Target.SHARE_QQ_ZONE)
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

        params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, "ask return key");
        return params;
    }

    // 打开 app
    private void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        boolean rst = SocialUtil.openApp(activity, SocialValues.QQ_PKG);
        if (rst) {
            onShareSuccess();
        } else {
            onShareFail(SocialError.make(SocialError.CODE_CANNOT_OPEN_ERROR, TAG + "#shareOpenApp#open app error"));
        }
    }


    // 分享文字
    private void shareText(int shareTarget, Activity activity, ShareObj shareMediaObj) {
        if (shareTarget == Target.SHARE_QQ_FRIENDS) {
            try {
                IntentShareUtil.shareQQText(activity, shareMediaObj);
            } catch (SocialError e) {
                e.printStackTrace();
                onShareFail(e);
            }
        } else if (shareTarget == Target.SHARE_QQ_ZONE) {
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareMediaObj.getSummary());
            mTencentApi.publishToQzone(activity, params, mIUiListenerWrap);
        }
    }

    // 分享图片
    private void shareImage(int shareTarget, Activity activity, ShareObj shareMediaObj) {
        if (shareTarget == Target.SHARE_QQ_FRIENDS) {
            // 可以兼容分享图片和gif
            Bundle params = buildCommonBundle("", shareMediaObj.getSummary(), "", shareTarget);
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareMediaObj.getThumbImagePath());
            mTencentApi.shareToQQ(activity, params, mIUiListenerWrap);
        } else if (shareTarget == Target.SHARE_QQ_ZONE) {
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareMediaObj.getSummary());
            ArrayList<String> imageUrls = new ArrayList<>();
            imageUrls.add(shareMediaObj.getThumbImagePath());
            params.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL, imageUrls);
            mTencentApi.publishToQzone(activity, params, mIUiListenerWrap);
        }
    }

    // 分享 app
    private void shareApp(int shareTarget, Activity activity, ShareObj obj) {
        if (shareTarget == Target.SHARE_QQ_FRIENDS) {
            Bundle params = buildCommonBundle(obj.getTitle(), obj.getSummary(), obj.getTargetUrl(), shareTarget);
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
            if (!TextUtils.isEmpty(obj.getThumbImagePath()))
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, obj.getThumbImagePath());
            mTencentApi.shareToQQ(activity, params, mIUiListenerWrap);
        } else if (shareTarget == Target.SHARE_QQ_ZONE) {
            shareWeb(shareTarget, activity, obj);
        }
    }

    // 分享网页
    private void shareWeb(int shareTarget, Activity activity, ShareObj obj) {
        if (shareTarget == Target.SHARE_QQ_FRIENDS) {
            // 分享图文
            final Bundle params = buildCommonBundle(obj.getTitle(), obj.getSummary(), obj.getTargetUrl(), shareTarget);
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            // 本地或网络路径
            if (!TextUtils.isEmpty(obj.getThumbImagePath()))
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, obj.getThumbImagePath());
            mTencentApi.shareToQQ(activity, params, mIUiListenerWrap);
        } else {
            final ArrayList<String> imageUrls = new ArrayList<>();
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, mAppName);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, obj.getSummary());
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, obj.getTitle());
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, obj.getTargetUrl());
            imageUrls.add(obj.getThumbImagePath());
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            mTencentApi.shareToQzone(activity, params, mIUiListenerWrap);
        }
    }

    // 分享音乐
    private void shareMusic(int shareTarget, Activity activity, ShareObj obj) {
        if (shareTarget == Target.SHARE_QQ_FRIENDS) {
            Bundle params = buildCommonBundle(obj.getTitle(), obj.getSummary(), obj.getTargetUrl(), shareTarget);
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
            if (!TextUtils.isEmpty(obj.getThumbImagePath()))
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, obj.getThumbImagePath());
            params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, obj.getMediaPath());
            mTencentApi.shareToQQ(activity, params, mIUiListenerWrap);
        } else if (shareTarget == Target.SHARE_QQ_ZONE) {
            shareWeb(shareTarget, activity, obj);
        }
    }

    // 分享视频
    private void shareVideo(int shareTarget, Activity activity, ShareObj obj) {
        if (shareTarget == Target.SHARE_QQ_FRIENDS) {
            if (FileUtil.isHttpPath(obj.getMediaPath())) {
                SocialUtil.e(TAG, "qq不支持分享网络视频，使用web分享代替");
                obj.setTargetUrl(obj.getMediaPath());
                shareWeb(shareTarget, activity, obj);
            } else if (FileUtil.isExist(obj.getMediaPath())){
                try {
                    IntentShareUtil.shareQQVideo(activity, obj);
                } catch (SocialError e) {
                    e.printStackTrace();
                    onShareFail(e);
                }
            } else{
                onShareFail(SocialError.make(SocialError.CODE_FILE_NOT_FOUND));
            }
        } else if (shareTarget == Target.SHARE_QQ_ZONE) {
            // qq 空间支持本地文件发布
            if (FileUtil.isHttpPath(obj.getMediaPath())) {
                SocialUtil.e(TAG, "qq空间网络视频，使用web形式分享");
                shareWeb(shareTarget, activity, obj);
            } else if (FileUtil.isExist(obj.getMediaPath())) {
                SocialUtil.e(TAG, "qq空间本地视频分享");
                final Bundle params = new Bundle();
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO);
                params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, obj.getMediaPath());
                mTencentApi.publishToQzone(activity, params, mIUiListenerWrap);
            } else {
                onShareFail(SocialError.make(SocialError.CODE_FILE_NOT_FOUND));
            }
        }
    }

    private class IUiListenerWrap implements IUiListener {

        @Override
        public void onComplete(Object o) {
            onShareSuccess();
        }

        @Override
        public void onError(UiError uiError) {
            onShareFail(SocialError.make(SocialError.CODE_SDK_ERROR, TAG + "#IUiListenerWrap#分享失败 " + parseUiError(uiError)));
        }

        @Override
        public void onCancel() {
            onShareCancel();
        }
    }

    static String parseUiError(UiError e) {
        return "code = " + e.errorCode + " ,msg = " + e.errorMessage + " ,detail=" + e.errorDetail;
    }

}
