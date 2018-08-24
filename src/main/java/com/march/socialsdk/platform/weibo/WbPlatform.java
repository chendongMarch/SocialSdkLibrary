package com.march.socialsdk.platform.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.SocialSdkConfig;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.common.ThumbDataContinuation;
import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.AbsPlatform;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.PlatformCreator;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.util.BitmapUtil;
import com.march.socialsdk.util.FileUtil;
import com.march.socialsdk.util.SocialLogUtil;
import com.march.socialsdk.util.Util;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import java.io.File;

import bolts.Task;

/**
 * CreateAt : 2016/12/3
 * Describe : sina平台实现
 * 文本相同的分享不允许重复发送，会发送不出去
 * 分享支持的检测
 *
 * @author chendong
 */
public class WbPlatform extends AbsPlatform {

    private static final String TAG = WbPlatform.class.getSimpleName();

    private WbShareHandler     mShareHandler;
    private WbLoginHelper      mLoginHelper;
    private OpenApiShareHelper mOpenApiShareHelper;

    public static class Creator implements PlatformCreator {
        @Override
        public IPlatform create(Context context, int target) {
            WbPlatform platform = null;
            SocialSdkConfig config = SocialSdk.getConfig();
            String appId = config.getSinaAppId();
            String appName = config.getAppName();
            String redirectUrl = config.getSinaRedirectUrl();
            String scope = config.getSinaScope();
            if (!Util.isAnyEmpty(appId, appName, redirectUrl, scope)) {
                platform = new WbPlatform(context, appId, appName, redirectUrl, scope);
                platform.setTarget(target);
            }
            return platform;
        }
    }

    WbPlatform(Context context, String appId, String appName, String redirectUrl, String scope) {
        super(appId, appName);
        AuthInfo authInfo = new AuthInfo(context, appId, redirectUrl, scope);
        WbSdk.install(context, authInfo);
    }

    @Override
    public boolean isInstall(Context context) {
        if (mTarget == Target.LOGIN_WB) {
            // 支持网页授权，所以不需要安装 app
            return true;
        }
        return WbSdk.isWbInstall(context);
    }

    @Override
    public void recycle() {

        mShareHandler = null;
        if (mLoginHelper != null) {
            mLoginHelper.recycle();
        }
        mLoginHelper = null;
        mOpenApiShareHelper = null;
    }

    // 延迟获取 ShareHandler
    private WbShareHandler makeWbShareHandler(Activity activity) {
        if (mShareHandler == null) {
            mShareHandler = new WbShareHandler(activity);
            mShareHandler.registerApp();
        }
        return mShareHandler;
    }

    // 延迟创建 login helper
    private WbLoginHelper makeLoginHelper(Activity activity) {
        if (mLoginHelper == null) {
            mLoginHelper = new WbLoginHelper(activity);
        }
        return mLoginHelper;
    }

    // 延迟创建 openApi 辅助
    private OpenApiShareHelper makeOpenApiShareHelper(Activity activity) {
        if (mOpenApiShareHelper == null) {
            mOpenApiShareHelper = new OpenApiShareHelper(makeLoginHelper(activity), mOnShareListener);
        }
        return mOpenApiShareHelper;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLoginHelper != null)
            mLoginHelper.authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    public void handleIntent(Activity activity) {
        if (mOnShareListener != null && activity instanceof WbShareCallback && mShareHandler != null) {
            makeWbShareHandler(activity).doResultIntent(activity.getIntent(), (WbShareCallback) activity);
        }
    }

    @Override
    public void onResponse(Object resp) {
        if (resp instanceof Integer && mOnShareListener != null) {
            switch ((int) resp) {
                case WBConstants.ErrorCode.ERR_OK:
                    // 分享成功
                    mOnShareListener.onSuccess();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    // 分享取消
                    mOnShareListener.onCancel();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    // 分享失败
                    mOnShareListener.onFailure(new SocialError(SocialError.CODE_SDK_ERROR, TAG + "#微博分享失败"));
                    break;
            }
        }
    }

    @Override
    public void login(Activity activity, OnLoginListener loginListener) {
        makeLoginHelper(activity).login(activity, loginListener);
    }

    @Override
    protected void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        boolean rst = Util.openApp(activity, SocialConstants.SINA_PKG);
        if (rst) {
            mOnShareListener.onSuccess();
        } else {
            mOnShareListener.onFailure(new SocialError(SocialError.CODE_CANNOT_OPEN_ERROR, "open app error"));
        }
    }

    @Override
    public void shareText(int shareTarget, Activity activity, final ShareObj obj) {
        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
        multiMessage.textObject = getTextObj(obj.getSummary());
        makeWbShareHandler(activity).shareMessage(multiMessage, false);
    }

    @Override
    public void shareImage(int shareTarget, final Activity activity, final ShareObj obj) {
        if (FileUtil.isGifFile(obj.getThumbImagePath())) {
            makeOpenApiShareHelper(activity).post(activity, obj);
        } else {
            BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                    .continueWith(new ThumbDataContinuation(TAG, "shareImage", mOnShareListener) {
                        @Override
                        public void onSuccess(byte[] thumbData) {
                            WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                            multiMessage.imageObject = getImageObj(obj.getThumbImagePath(), thumbData);
                            multiMessage.textObject = getTextObj(obj.getSummary());
                            makeWbShareHandler(activity).shareMessage(multiMessage, false);
                        }
                    }, Task.UI_THREAD_EXECUTOR);
        }

    }

    @Override
    public void shareApp(int shareTarget, Activity activity, ShareObj obj) {
        SocialLogUtil.e(TAG, "sina不支持app分享，将以web形式分享");
        shareWeb(shareTarget, activity, obj);
    }

    @Override
    public void shareWeb(int shareTarget, final Activity activity, final ShareObj obj) {
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareWeb", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                        checkAddTextAndImageObj(multiMessage, obj, thumbData);
                        multiMessage.mediaObject = getWebObj(obj, thumbData);
                        makeWbShareHandler(activity).shareMessage(multiMessage, false);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void shareMusic(int shareTarget, final Activity activity, final ShareObj obj) {
        shareWeb(shareTarget, activity, obj);
    }

    @Override
    public void shareVideo(int shareTarget, final Activity activity, final ShareObj obj) {
        String mediaPath = obj.getMediaPath();
        if (FileUtil.isExist(mediaPath)) {
            WeiboMultiMessage multiMessage = new WeiboMultiMessage();
            checkAddTextAndImageObj(multiMessage, obj, null);
            multiMessage.videoSourceObject = getVideoObj(obj, null);
            makeWbShareHandler(activity).shareMessage(multiMessage, false);
        } else {
            shareWeb(shareTarget, activity, obj);
        }
    }


    /**
     * 根据ShareMediaObj配置来检测是不是添加文字和照片
     *
     * @param thumbData    图片数组
     * @param multiMessage msg
     * @param obj          share
     */
    private void checkAddTextAndImageObj(WeiboMultiMessage multiMessage, ShareObj obj, byte[] thumbData) {
        if (obj.isSinaWithPicture())
            multiMessage.imageObject = getImageObj(obj.getThumbImagePath(), thumbData);
        if (obj.isSinaWithSummary())
            multiMessage.textObject = getTextObj(obj.getSummary());
    }


    private TextObject getTextObj(String summary) {
        TextObject textObject = new TextObject();
        textObject.text = summary;
        return textObject;
    }


    private ImageObject getImageObj(String localPath, byte[] data) {
        ImageObject imageObject = new ImageObject();
        //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        imageObject.imageData = data;
        imageObject.imagePath = localPath;
        return imageObject;
    }


    private WebpageObject getWebObj(ShareObj obj, byte[] thumbData) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = obj.getTitle();
        mediaObject.description = obj.getSummary();
        // 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.thumbData = thumbData;
        mediaObject.actionUrl = obj.getTargetUrl();
        mediaObject.defaultText = obj.getSummary();
        return mediaObject;
    }


    private VideoSourceObject getVideoObj(ShareObj obj, byte[] thumbData) {
        VideoSourceObject mediaObject = new VideoSourceObject();
        mediaObject.videoPath = Uri.fromFile(new File(obj.getMediaPath()));
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = obj.getTitle();
        mediaObject.description = obj.getSummary();
        // 注意：最终压缩过的缩略图大小不得超过 32kb。
//        mediaObject.thumbData = thumbData;
        mediaObject.actionUrl = obj.getTargetUrl();
        mediaObject.during = obj.getDuration() == 0 ? 10 : obj.getDuration();
        return mediaObject;
    }

}
