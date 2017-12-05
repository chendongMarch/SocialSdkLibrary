package com.march.socialsdk.platform.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.march.socialsdk.common.ThumbDataContinuation;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.helper.BitmapHelper;
import com.march.socialsdk.helper.OtherHelper;
import com.march.socialsdk.helper.FileHelper;
import com.march.socialsdk.helper.PlatformLog;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.manager.ShareManager;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.AbsPlatform;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.platform.weibo.extend.StatusesAPI;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import bolts.Continuation;
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

    private IWeiboShareAPI mWeiboShareAPI;
    private AuthInfo       mAuthInfo;
    private SsoHandler     mSsoHandler;
    private StatusesAPI    mStatusesAPI;

    // open Api分享时的监听
    private RequestListener requestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            mOnShareListener.onSuccess();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            mOnShareListener.onFailure(new SocialException("open api分享图片失败", e));
        }
    };

    public WbPlatform(Context context, String appId, String appName, String redirectUrl, String scope) {
        super(context, appId, appName);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, appId);
        mWeiboShareAPI.registerApp();
        mAuthInfo = new AuthInfo(context, appId, redirectUrl, scope);
    }

    @Override
    public boolean isInstall() {
        return mWeiboShareAPI != null && mWeiboShareAPI.isWeiboAppInstalled();
    }

    @Override
    public void recycle() {
        super.recycle();
        mStatusesAPI = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null)
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    public void onNewIntent(Activity activity) {
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        if (!(activity instanceof IWeiboHandler.Response)) {
            PlatformLog.e(TAG, "微博接受回调的IWeiboHandler.Response必须是发起分享的Activity");
            return;
        }
        IWeiboHandler.Response shareResponse = (IWeiboHandler.Response) activity;
        mWeiboShareAPI.handleWeiboResponse(activity.getIntent(), shareResponse);
    }

    @Override
    public void onResponse(Object resp) {
        if (resp == null || !(resp instanceof BaseResponse))
            return;
        BaseResponse baseResp = (BaseResponse) resp;
        switch (baseResp.errCode) {
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
                mOnShareListener.onFailure(new SocialException("微博分享失败"));
                break;
        }
    }


    @Override
    public void login(Activity activity, OnLoginListener loginListener) {
        if (mSsoHandler == null)
            mSsoHandler = new SsoHandler(activity, mAuthInfo);
        WbLoginHelper mLoginHelper = new WbLoginHelper(activity, mAppId);
        mLoginHelper.login(activity, mSsoHandler, loginListener);
    }


    private boolean isSupportShare() {
        return mWeiboShareAPI != null
                && mWeiboShareAPI.isWeiboAppSupportAPI()
                && mWeiboShareAPI.getWeiboAppSupportAPI() != -1;

    }

    // 授权，只拿token
    private void justAuth(final Activity activity, final Runnable runnable) {
        if (mSsoHandler != null && mStatusesAPI != null) {
            if (runnable != null)
                runnable.run();
            return;
        }
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
        WbAuthHelper.auth(activity, mSsoHandler, new WbAuthHelper.OnAuthOverListener() {
            @Override
            public void onAuth(Oauth2AccessToken token) {
                PlatformLog.e(TAG, token.toString());
                mStatusesAPI = new StatusesAPI(activity, mAppId, token);
                if (runnable != null)
                    runnable.run();
            }

            @Override
            public void onException(SocialException e) {
                mOnShareListener.onFailure(e);
            }

            @Override
            public void onCancel() {
                mOnShareListener.onCancel();
            }
        });
    }

    // 用openApi实现可以支持5M以下图片文件分享，微博应用名称点亮可点击
    // openApi分享本地图片
    public void shareImageOpenApi(final Activity activity, final ShareObj obj) {
        justAuth(activity, new Runnable() {
            @Override
            public void run() {
                if (FileHelper.isGifFile(obj.getThumbImagePath())) {
                    shareGifOpenApi(obj);
                } else {
                    shareJpgPngOpenApi(obj);
                }
            }
        });
    }

    // openApi分享图片
    private void shareJpgPngOpenApi(final ShareObj obj) {
        final Callable<Bitmap> getBitmapCallable = new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return BitmapHelper.getBitmapByPath(obj.getThumbImagePath(), 5 * 1024 * 1024);
            }
        };
        final Continuation<Bitmap, Object> shareImageContinuation = new Continuation<Bitmap, Object>() {
            @Override
            public Object then(final Task<Bitmap> task) throws Exception {
                if (task.isFaulted() || task.getResult() == null) {
                    mOnShareListener.onFailure(new SocialException("sina openApi分享jpg,png失败", task.getError()));
                    return null;
                }
                mStatusesAPI.upload(obj.getSummary(), task.getResult(), null, null, requestListener);
                return null;
            }
        };
        // 分享普通图片
        Task.callInBackground(getBitmapCallable).continueWith(shareImageContinuation, Task.UI_THREAD_EXECUTOR);

    }

    // openApi 分享gif
    private void shareGifOpenApi(final ShareObj obj) {
        final Callable<ByteArrayOutputStream> getBaosCallable = new Callable<ByteArrayOutputStream>() {
            @Override
            public ByteArrayOutputStream call() throws Exception {
                return FileHelper.getOutputStreamFromFile(obj.getThumbImagePath());
            }
        };
        final Continuation<ByteArrayOutputStream, Object> shareGifContinuation = new Continuation<ByteArrayOutputStream, Object>() {
            @Override
            public Object then(final Task<ByteArrayOutputStream> task) throws Exception {
                if (task.isFaulted() || task.getResult() == null) {
                    mOnShareListener.onFailure(new SocialException("sina openApi分享gif失败", task.getError()));
                    return null;
                }
                mStatusesAPI.upload(obj.getSummary(), task.getResult(), requestListener);
                return null;
            }
        };
        // 分享gif
        Task.callInBackground(getBaosCallable)
                .continueWith(shareGifContinuation, Task.UI_THREAD_EXECUTOR);
    }


    // openApi 分享网络图片，需要高级权限
    public void shareNetImage(final Activity activity, final String text, final String url) {
        justAuth(activity, new Runnable() {
            @Override
            public void run() {
                mStatusesAPI.uploadUrlText(text, url, null, null, null, requestListener);
            }
        });
    }

    private void sendWeiboMultiMsg(Activity activity, WeiboMultiMessage message) {
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = message;
        boolean isSendSuccess = mWeiboShareAPI.sendRequest(activity, request);
        if (!isSendSuccess) {
            mOnShareListener.onFailure(new SocialException("sina分享发送失败，检查参数"));
        }
    }

    @Override
    protected void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        boolean rst = OtherHelper.openApp(mContext, SocialConstants.SINA_PKG);
        if (rst) {
            mOnShareListener.onSuccess();
        } else {
            mOnShareListener.onFailure(new SocialException("open app error"));
        }
    }

    @Override
    public void shareText(int shareTarget, Activity activity, final ShareObj obj) {
        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
        multiMessage.textObject = getTextObj(obj.getSummary());
        sendWeiboMultiMsg(activity, multiMessage);
    }


    @Override
    public void shareImage(int shareTarget, final Activity activity, final ShareObj obj) {
        if (shareTarget == Target.SHARE_WB_OPENAPI) {
            shareImageOpenApi(activity, obj);
        } else {
            BitmapHelper.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                    .continueWith(new ThumbDataContinuation(TAG, "shareImage", mOnShareListener) {
                        @Override
                        public void onSuccess(byte[] thumbData) {
                            WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                            multiMessage.imageObject = getImageObj(obj.getThumbImagePath(), thumbData);
                            multiMessage.textObject = getTextObj(obj.getSummary());
                            sendWeiboMultiMsg(activity, multiMessage);
                        }
                    }, Task.UI_THREAD_EXECUTOR);
        }
    }

    @Override
    public void shareApp(int shareTarget, Activity activity, ShareObj obj) {
        PlatformLog.e(TAG, "sina不支持app分享，将以web形式分享");
        shareWeb(shareTarget, activity, obj);
    }

    @Override
    public void shareWeb(int shareTarget, final Activity activity, final ShareObj obj) {
        BitmapHelper.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareWeb", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                        checkAddTextAndImageObj(multiMessage, obj, thumbData);
                        multiMessage.mediaObject = getWebObj(obj.getTitle(), thumbData, obj.getTargetUrl(), obj.getSummary());
                        sendWeiboMultiMsg(activity, multiMessage);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void shareMusic(int shareTarget, final Activity activity, final ShareObj obj) {
        BitmapHelper.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareMusic", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                        checkAddTextAndImageObj(multiMessage, obj, thumbData);
                        multiMessage.mediaObject = getMusicObj(obj.getTitle(), thumbData, obj.getTargetUrl(), obj.getSummary(), obj.getMediaPath(), obj.getDuration());
                        sendWeiboMultiMsg(activity, multiMessage);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void shareVideo(int shareTarget, final Activity activity, final ShareObj obj) {
        if(obj.isShareByIntent())
        BitmapHelper.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareVideo", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                        checkAddTextAndImageObj(multiMessage, obj, thumbData);
                        multiMessage.mediaObject = getVideoObj(obj.getTitle(), thumbData, obj.getTargetUrl(), obj.getSummary(), obj.getMediaPath(), obj.getDuration());
                        sendWeiboMultiMsg(activity, multiMessage);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void shareVoice(int shareTarget, final Activity activity, final ShareObj obj) {
        BitmapHelper.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareVoice", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                        checkAddTextAndImageObj(multiMessage, obj, thumbData);
                        multiMessage.mediaObject = getVoiceObj(obj.getTitle(), thumbData, obj.getTargetUrl(), obj.getSummary(), obj.getMediaPath(), obj.getDuration());
                        sendWeiboMultiMsg(activity, multiMessage);
                    }
                }, Task.UI_THREAD_EXECUTOR);
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


    private void initCommonParams(BaseMediaObject mediaObject, String title, byte[] data, String actionUrl, String defaultText) {
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = defaultText;
        // 设置 Bitmap 类型的图片到视频对象里
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.thumbData = data;
        mediaObject.actionUrl = actionUrl;
    }


    private WebpageObject getWebObj(String title, byte[] thumbData, String actionUrl, String defaultText) {
        WebpageObject webpageObject = new WebpageObject();
        initCommonParams(webpageObject, title, thumbData, actionUrl, defaultText);
        webpageObject.defaultText = defaultText;
        return webpageObject;
    }


    private MusicObject getMusicObj(String title, byte[] thumbData, String actionUrl, String defaultText, String dataUrl, int duration) {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        initCommonParams(musicObject, title, thumbData, actionUrl, defaultText);
        musicObject.dataUrl = dataUrl;
        musicObject.dataHdUrl = dataUrl;
        musicObject.duration = duration == 0 ? 10 : duration;
        musicObject.defaultText = defaultText + " " + actionUrl;
        return musicObject;
    }

    private VideoObject getVideoObj(String title, byte[] thumbData, String actionUrl, String defaultText, String dataUrl, int duration) {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        initCommonParams(videoObject, title, thumbData, actionUrl, defaultText);
        videoObject.h5Url = dataUrl;
        videoObject.dataUrl = dataUrl;
        videoObject.dataHdUrl = dataUrl;
        videoObject.duration = duration == 0 ? 10 : duration;
        videoObject.defaultText = defaultText;
        return videoObject;
    }


    private VoiceObject getVoiceObj(String title, byte[] thumbData, String actionUrl, String defaultText, String dataUrl, int duration) {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
        initCommonParams(voiceObject, title, thumbData, actionUrl, defaultText);
        voiceObject.h5Url = dataUrl;
        voiceObject.dataUrl = dataUrl;
        voiceObject.dataHdUrl = dataUrl;
        voiceObject.duration = duration == 0 ? 10 : duration;
        voiceObject.defaultText = defaultText;
        return voiceObject;
    }

}
