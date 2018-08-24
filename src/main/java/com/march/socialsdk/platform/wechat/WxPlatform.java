package com.march.socialsdk.platform.wechat;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.SocialSdkConfig;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.common.ThumbDataContinuation;
import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.AbsPlatform;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.PlatformCreator;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.util.BitmapUtil;
import com.march.socialsdk.util.FileUtil;
import com.march.socialsdk.util.SocialLogUtil;
import com.march.socialsdk.util.Util;
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
 * [分享与收藏文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317340&token=&lang=zh_CN)
 * [微信登录文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN)
 *
 * 缩略图不超过 32kb
 * 源文件不超过 10M
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
            if (!Util.isAnyEmpty(config.getWxAppId(), config.getWxSecretKey())) {
                platform = new WxPlatform(context, config.getWxAppId(), config.getWxSecretKey(), config.getAppName());
            }
            return platform;
        }
    }


    WxPlatform(Context context, String appId, String wxSecret, String appName) {
        super(appId, appName);
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
            OnLoginListener listener = mWeChatLoginHelper.getOnLoginListener();
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 用户同意  authResp.country;  authResp.lang;  authResp.state;
                    SendAuth.Resp authResp = (SendAuth.Resp) resp;
                    String authCode = authResp.code;
                    if (SocialSdk.getConfig().isOnlyAuthCode()) {
                        listener.onSuccess(new LoginResult(Target.LOGIN_WX, authCode));
                    } else {
                        mWeChatLoginHelper.getAccessTokenByCode(authCode);
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    listener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝授权
                    listener.onCancel();
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
                    mOnShareListener.onFailure(new SocialError(SocialError.CODE_SDK_ERROR, "分享失败"));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 分享被拒绝
                    mOnShareListener.onFailure(new SocialError(SocialError.CODE_SDK_ERROR, "分享被拒绝"));
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
            mOnShareListener.onFailure(new SocialError(SocialError.CODE_SDK_ERROR, TAG + "#sendMsgToWx失败，可能是参数错误"));
        }
    }

    @Override
    protected void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        boolean rst = mWxApi.openWXApp();
        if (rst) {
            mOnShareListener.onSuccess();
        } else {
            mOnShareListener.onFailure(new SocialError(SocialError.CODE_CANNOT_OPEN_ERROR));
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
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
                .continueWith(new ThumbDataContinuation(TAG, "shareImage", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        shareImage(shareTarget, obj.getSummary(), obj.getThumbImagePath(), thumbData);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    private void shareImage(final int shareTarget, String desc, final String localPath, byte[] thumbData) {
        if (shareTarget == Target.SHARE_WX_FRIENDS) {
            if (FileUtil.isGifFile(localPath)) {
                shareEmoji(shareTarget, localPath, desc, thumbData);
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

    private void shareEmoji(int shareTarget, String localPath, String desc, byte[] thumbData) {
        WXEmojiObject emoji = new WXEmojiObject();
        emoji.emojiPath = localPath;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = emoji;
        msg.description = desc;
        msg.thumbData = thumbData;
        sendMsgToWx(msg, shareTarget, "emoji");
    }


    @Override
    public void shareApp(int shareTarget, Activity activity, ShareObj obj) {
        SocialLogUtil.e(TAG, "微信不支持app分享，将以web形式分享");
        shareWeb(shareTarget, activity, obj);
    }

    @Override
    public void shareWeb(final int shareTarget, Activity activity, final ShareObj obj) {
//        com.march.socialsdk.workflow.Task.call(com.march.socialsdk.workflow.Task.BG, new CallAction<byte[]>() {
//            @Override
//            public byte[] call() {
//                return BitmapUtil.getStaticSizeBitmapByteByPath(obj.getThumbImagePath(), THUMB_IMAGE_SIZE);
//            }
//        }).then(com.march.socialsdk.workflow.Task.UI, new TaskAction<byte[], Boolean>() {
//            @Override
//            public Boolean call(byte[] param) {
//                WXWebpageObject webPage = new WXWebpageObject();
//                webPage.webpageUrl = obj.getTargetUrl();
//                WXMediaMessage msg = new WXMediaMessage(webPage);
//                msg.title = obj.getTitle();
//                msg.description = obj.getSummary();
//                msg.thumbData = param;
//                sendMsgToWx(msg, shareTarget, "web");
//                return false;
//            }
//        }).error(new ErrorAction() {
//            @Override
//            public void error(Exception ex) {
//                mOnShareListener.onFailure(new SocialError("share web error", ex));
//            }
//        }).execute();

        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
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
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
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
        if (shareTarget == Target.SHARE_WX_FRIENDS) {
            if (FileUtil.isHttpPath(obj.getMediaPath())) {
                shareWeb(shareTarget, activity, obj);
            } else if (FileUtil.isExist(obj.getMediaPath())) {
                shareVideoByIntent(activity, obj, SocialConstants.WECHAT_PKG, SocialConstants.WX_FRIEND_PAGE);
            } else {
                mOnShareListener.onFailure(new SocialError(SocialError.CODE_FILE_NOT_FOUND));
            }
        } else {
            BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE)
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
