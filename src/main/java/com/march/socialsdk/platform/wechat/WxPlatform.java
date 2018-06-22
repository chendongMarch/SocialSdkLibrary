package com.march.socialsdk.platform.wechat;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.common.ThumbDataContinuation;
import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.model.SocialSdkConfig;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.PlatformCreator;
import com.march.socialsdk.utils.BitmapUtils;
import com.march.socialsdk.utils.CommonUtils;
import com.march.socialsdk.utils.FileUtils;
import com.march.socialsdk.utils.IntentShareUtils;
import com.march.socialsdk.utils.SocialLogUtils;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.AbsPlatform;
import com.march.socialsdk.platform.Target;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXEmojiObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import bolts.Task;

/**
 * CreateAt : 2016/12/3
 * Describe : 微信平台
 *
 * @author chendong
 */
public class WxPlatform extends AbsPlatform {

    public static final String TAG = WxPlatform.class.getSimpleName();

    private WxLoginHelper mWeChatLoginHelper;
    private IWXAPI mWxApi;
    private String mWxSecret;

    public static class Creator implements PlatformCreator {
        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            SocialSdkConfig config = SocialSdk.getConfig();
            if (!CommonUtils.isAnyEmpty(config.getWxAppId(), config.getWxSecretKey())) {
                platform = new WxPlatform(context, config.getWxAppId(), config.getWxSecretKey(), config.getAppName());
            }
            return platform;
        }
    }


    WxPlatform(Context context, String appId, String wxSecret, String appName) {
        super(context, appId, appName);
        this.mWxSecret = wxSecret;
        mWxApi = WXAPIFactory.createWXAPI(context, appId, true);
        mWxApi.registerApp(appId);
    }

    @Override
    public boolean checkPlatformConfig() {
        return super.checkPlatformConfig() && !TextUtils.isEmpty(mWxSecret);
    }

    @Override
    public boolean isInstall(Context context) {
        return mWxApi != null && mWxApi.isWXAppInstalled();
    }

    @Override
    public void recycle() {
        super.recycle();
        mWxApi.detach();
        mWxApi = null;
    }

    @Override
    public void handleIntent(Activity activity) {
        if (activity instanceof IWXAPIEventHandler && mWxApi != null)
            mWxApi.handleIntent(activity.getIntent(), (IWXAPIEventHandler) activity);
    }

    @Override
    public void onResponse(Object resp) {
        if (!(resp instanceof BaseResp)) {
            return;
        }
        BaseResp baseResp = (BaseResp) resp;
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            // 登录
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 用户同意  authResp.country;  authResp.lang;  authResp.state;
                    SendAuth.Resp authResp = (SendAuth.Resp) resp;
                    String auth_code = authResp.code;
                    mWeChatLoginHelper.getAccessTokenByCode(auth_code);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    mWeChatLoginHelper.getOnLoginListener().onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝授权
                    mWeChatLoginHelper.getOnLoginListener().onCancel();
                    break;
            }
        } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            if (mOnShareListener == null) {
                return;
            }
            // 分享
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 分享成功
                    mOnShareListener.onSuccess();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 分享取消
                    mOnShareListener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    // 分享失败
                    mOnShareListener.onFailure(new SocialError("分享失败"));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 分享被拒绝
                    mOnShareListener.onFailure(new SocialError("分享被拒绝"));
                    break;
            }
        }
    }

    @Override
    public void login(Activity context, OnLoginListener loginListener) {
        if (!mWxApi.isWXAppSupportAPI()) {
            loginListener.onFailure(new SocialError(SocialError.CODE_VERSION_LOW));
            return;
        }
        mWeChatLoginHelper = new WxLoginHelper(context, mWxApi, mAppId);
        mWeChatLoginHelper.login(mWxSecret, loginListener);
    }

    @Override
    public int getPlatformType() {
        return Target.PLATFORM_WX;
    }


    private int getShareToWhere(int shareTarget) {
        int where = SendMessageToWX.Req.WXSceneSession;
        switch (shareTarget) {
            case Target.SHARE_WX_FRIENDS:
                where = SendMessageToWX.Req.WXSceneSession;
                break;
            case Target.SHARE_WX_ZONE:
                where = SendMessageToWX.Req.WXSceneTimeline;
                break;
            case Target.SHARE_WX_FAVORITE:
                where = SendMessageToWX.Req.WXSceneFavorite;
                break;
        }
        return where;
    }


    private void sendMsgToWx(WXMediaMessage msg, int shareTarget, String sign) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(sign);
        req.message = msg;
        req.scene = getShareToWhere(shareTarget);
        boolean sendResult = mWxApi.sendReq(req);
        if (!sendResult) {
            mOnShareListener.onFailure(new SocialError("sendMsgToWx失败，可能是参数错误"));
        }
    }

    @Override
    protected void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        boolean rst = mWxApi.openWXApp();
        if (rst) {
            mOnShareListener.onSuccess();
        } else {
            mOnShareListener.onFailure(new SocialError("open app error"));
        }
    }

    @Override
    public void shareText(int shareTarget, Activity activity, ShareObj obj) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = obj.getSummary();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.title = obj.getTitle();
        msg.description = obj.getSummary();
        sendMsgToWx(msg, shareTarget, "text");
    }

    @Override
    public void shareImage(final int shareTarget, final Activity activity, final ShareObj obj) {
        BitmapUtils.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareImage", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        shareImage(shareTarget, obj.getSummary(), obj.getThumbImagePath(), thumbData);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    private void shareImage(final int shareTarget, String desc, final String localPath, byte[] thumbData) {
        if (shareTarget == Target.SHARE_WX_FRIENDS) {
            if (FileUtils.isGifFile(localPath)) {
                SocialLogUtils.e(TAG, "发送给朋友时 Gif 文件以emoji格式分享");
                WXEmojiObject emoji = new WXEmojiObject();
                emoji.emojiPath = localPath;
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = emoji;
                msg.description = desc;
                //这个值似乎有限制,太大无法发送,所有已使用低质量压缩
                msg.thumbData = thumbData;
                sendMsgToWx(msg, shareTarget, "emoji");
            } else {
                shareImage(shareTarget, localPath, thumbData);
            }
        } else {
            shareImage(shareTarget, localPath, thumbData);
        }
    }

    private void shareImage(int shareTarget, String localPath, byte[] thumbData) {
        // 文件大小不大于10485760  路径长度不大于10240
        WXImageObject imgObj = new WXImageObject();
        imgObj.imagePath = localPath;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        msg.thumbData = thumbData;
        sendMsgToWx(msg, shareTarget, "image");
    }


    @Override
    public void shareApp(int shareTarget, Activity activity, ShareObj obj) {
        SocialLogUtils.e(TAG, "微信不支持app分享，将以web形式分享");
        shareWeb(shareTarget, activity, obj);


    }


    @Override
    public void shareWeb(final int shareTarget, Activity activity, final ShareObj obj) {
        BitmapUtils.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareWeb", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WXWebpageObject webPage = new WXWebpageObject();
                        webPage.webpageUrl = obj.getTargetUrl();
                        WXMediaMessage msg = new WXMediaMessage(webPage);
                        msg.title = obj.getTitle();
                        msg.description = obj.getSummary();
                        msg.thumbData = thumbData;
                        sendMsgToWx(msg, shareTarget, "web");
                    }
                }, Task.UI_THREAD_EXECUTOR);

    }


    @Override
    public void shareMusic(final int shareTarget, Activity activity, final ShareObj obj) {
        BitmapUtils.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareMusic", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WXMusicObject music = new WXMusicObject();
                        music.musicUrl = obj.getMediaPath();
                        WXMediaMessage msg = new WXMediaMessage();
                        msg.mediaObject = music;
                        msg.title = obj.getTitle();
                        msg.description = obj.getSummary();
                        msg.thumbData = thumbData;
                        sendMsgToWx(msg, shareTarget, "music");
                    }
                }, Task.UI_THREAD_EXECUTOR);

    }

    @Override
    public void shareVideo(final int shareTarget, Activity activity, final ShareObj obj) {
        if (obj.isShareByIntent() && shareTarget == Target.SHARE_WX_FRIENDS) {
            try {
                IntentShareUtils.shareVideo(activity, obj.getMediaPath(), SocialConstants.WECHAT_PKG, SocialConstants.WX_FRIEND_PAGE);
            } catch (Exception e) {
                this.mOnShareListener.onFailure(new SocialError(SocialError.CODE_SHARE_BY_INTENT_FAIL, e));
            }
        } else {
            BitmapUtils.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                    .continueWith(new ThumbDataContinuation(TAG, "shareVideo", mOnShareListener) {
                        @Override
                        public void onSuccess(byte[] thumbData) {
                            WXVideoObject video = new WXVideoObject();
                            video.videoUrl = obj.getMediaPath();
                            WXMediaMessage msg = new WXMediaMessage(video);
                            msg.title = obj.getTitle();
                            msg.description = obj.getSummary();
                            msg.thumbData = thumbData;
                            sendMsgToWx(msg, shareTarget, "video");
                        }
                    }, Task.UI_THREAD_EXECUTOR);
        }

    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
