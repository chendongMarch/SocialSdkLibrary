package com.zfy.social.wx;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMiniProgramMsg;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXEmojiObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zfy.social.core.SocialOptions;
import com.zfy.social.core._SocialSdk;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.common.ThumbTask;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.model.LoginObj;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.AbsPlatform;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.util.BitmapUtil;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.IntentShareUtil;
import com.zfy.social.core.util.SocialUtil;
import com.zfy.social.wx.uikit.WxActionActivity;

import bolts.Task;

/**
 * CreateAt : 2016/12/3
 * Describe : 微信平台
 * 缩略图不超过 32kb
 * 源文件不超过 10M
 *
 * @author chendong
 */
public class WxPlatform extends AbsPlatform {

    public static final String TAG = WxPlatform.class.getSimpleName();

    private WxLoginHelper mWeChatLoginHelper;
    private IWXAPI mWxApi;
    private String mWxSecret;

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            SocialOptions opts = _SocialSdk.getInst().opts();
            if (!SocialUtil.isAnyEmpty(opts.getWxAppId(), opts.getWxSecretKey())) {
                platform = new WxPlatform(context, opts.getWxAppId(), opts.getAppName(), target, opts.getWxSecretKey());
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_WX;
        }


        @Override
        public boolean checkShareTarget(int shareTarget) {
            return shareTarget == Target.SHARE_WX_FAVORITE
                    || shareTarget == Target.SHARE_WX_FRIENDS
                    || shareTarget == Target.SHARE_WX_ZONE;
        }

        @Override
        public boolean checkLoginTarget(int loginTarget) {
            return loginTarget == Target.LOGIN_WX || loginTarget == Target.LOGIN_WX_SCAN;
        }
    }

    private WxPlatform(Context context, String appId, String appName, int target, String wxSecret) {
        super(context, appId, appName, target);
        this.mWxSecret = wxSecret;
        mWxApi = WXAPIFactory.createWXAPI(context, appId, true);
        mWxApi.registerApp(appId);
    }

    @Override
    public boolean checkPlatformConfig() {
        return super.checkPlatformConfig() && !TextUtils.isEmpty(mWxSecret);
    }

    @Override
    public Class getUIKitClazz() {
        return WxActionActivity.class;
    }

    @Override
    public boolean isInstall(Context context) {
        return mWxApi != null && mWxApi.isWXAppInstalled();
    }

    @Override
    public void recycle() {
        mWxApi.detach();
        if (mWeChatLoginHelper != null) {
            mWeChatLoginHelper.recycle();
        }
        mWxApi = null;
    }

    @Override
    public void handleIntent(Activity activity) {
        if (activity instanceof IWXAPIEventHandler && mWxApi != null) {
            mWxApi.handleIntent(activity.getIntent(), (IWXAPIEventHandler) activity);
        }
    }

    @Override
    public void onResponse(Object resp) {
        if (!(resp instanceof BaseResp)) {
            return;
        }
        BaseResp baseResp = (BaseResp) resp;
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            // 登录
            OnLoginStateListener listener = mWeChatLoginHelper.getListener();
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 用户同意  authResp.country;  authResp.lang;  authResp.state;
                    SendAuth.Resp authResp = (SendAuth.Resp) resp;
                    String authCode = authResp.code;
                    if (_SocialSdk.getInst().opts().isWxOnlyAuthCode()) {
                        listener.onState(null, LoginResult.successOf(Target.LOGIN_WX, authCode));
                    } else {
                        mWeChatLoginHelper.getAccessTokenByCode(authCode);
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    listener.onState(null, LoginResult.cancelOf(-1));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝授权
                    listener.onState(null, LoginResult.cancelOf(-1));
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
                    onShareSuccess();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 分享取消
                    onShareCancel();
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    // 分享失败
                    onShareFail(SocialError.make(SocialError.CODE_SDK_ERROR, "分享失败"));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 分享被拒绝
                    onShareFail(SocialError.make(SocialError.CODE_SDK_ERROR, "分享被拒绝"));
                    break;
            }
        }
    }


    @Override
    public void login(Activity act, int target, LoginObj obj, OnLoginStateListener listener) {
        if (obj != null && obj.getAppSecret() != null) {
            mWxSecret = obj.getAppSecret();
        }
        mWeChatLoginHelper = new WxLoginHelper(act, mWxApi, target, mAppId, mWxSecret, obj);
        mWeChatLoginHelper.requestAuthCode(listener);

    }

    // 获取分享目标
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

    // 发送分享
    private void sendMsgToWx(WXMediaMessage msg, int shareTarget, String sign) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(sign);
        req.message = msg;
        req.scene = getShareToWhere(shareTarget);
        boolean sendResult = mWxApi.sendReq(req);
        if (!sendResult) {
            onShareFail(SocialError.make(SocialError.CODE_SDK_ERROR, TAG + "#sendMsgToWx失败，可能是参数错误"));
        }
    }

    @Override
    protected void dispatchShare(Activity activity, int shareTarget, ShareObj obj) {
        if (obj.isWxMini()) {
            shareMiniProgram(shareTarget, activity, obj);
            return;
        }
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

    // 分享 app
    private void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        boolean rst = mWxApi.openWXApp();
        if (rst) {
            onShareSuccess();
        } else {
            onShareFail(SocialError.make(SocialError.CODE_CANNOT_OPEN_ERROR));
        }

    }

    // 分享 文字
    private void shareText(int shareTarget, Activity activity, ShareObj obj) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = obj.getSummary();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.title = obj.getTitle();
        msg.description = obj.getSummary();
        sendMsgToWx(msg, shareTarget, "text");
    }

    // 分享 图片
    private void shareImage(final int shareTarget, final Activity activity, final ShareObj obj) {
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_32)
                .continueWith(new ThumbTask(TAG, "shareImage") {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        shareImage(shareTarget, obj.getSummary(), obj.getThumbImagePath(), thumbData);
                    }

                    @Override
                    public void onFail(SocialError error) {
                        onShareFail(error);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    // 分享图片
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

    // 分享图片
    private void shareImage(int shareTarget, String localPath, byte[] thumbData) {
        // 文件大小不大于10485760  路径长度不大于10240
        WXImageObject imgObj = new WXImageObject();
        imgObj.imagePath = localPath;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        msg.thumbData = thumbData;
        sendMsgToWx(msg, shareTarget, "image");
    }

    // 分享 emoji
    private void shareEmoji(int shareTarget, String localPath, String desc, byte[] thumbData) {
        WXEmojiObject emoji = new WXEmojiObject();
        emoji.emojiPath = localPath;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = emoji;
        msg.description = desc;
        msg.thumbData = thumbData;
        sendMsgToWx(msg, shareTarget, "emoji");
    }

    // 分享 app
    private void shareApp(int shareTarget, Activity activity, ShareObj obj) {
        SocialUtil.e(TAG, "微信不支持app分享，将以web形式分享");
        shareWeb(shareTarget, activity, obj);
    }

    // 分享 web
    private void shareWeb(final int shareTarget, Activity activity, final ShareObj obj) {
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_32)
                .continueWith(new ThumbTask(TAG, "shareWeb") {
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

                    @Override
                    public void onFail(SocialError error) {
                        onShareFail(error);
                    }
                }, Task.UI_THREAD_EXECUTOR);

    }

    // 分享音乐
    private void shareMusic(final int shareTarget, Activity activity, final ShareObj obj) {
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_32)
                .continueWith(new ThumbTask(TAG, "shareMusic") {
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

                    @Override
                    public void onFail(SocialError error) {
                        onShareFail(error);
                    }
                }, Task.UI_THREAD_EXECUTOR);

    }

    // 分享视频
    private void shareVideo(final int shareTarget, Activity activity, final ShareObj obj) {
        if (shareTarget == Target.SHARE_WX_FRIENDS) {
            if (FileUtil.isHttpPath(obj.getMediaPath())) {
                shareWeb(shareTarget, activity, obj);
            } else if (FileUtil.isExist(obj.getMediaPath())) {
                try {
                    IntentShareUtil.shareVideo(activity, obj, SocialValues.WECHAT_PKG, SocialValues.WX_FRIEND_PAGE);
                } catch (SocialError e) {
                    e.printStackTrace();
                    onShareFail(e);
                }
            } else {
                onShareFail(SocialError.make(SocialError.CODE_FILE_NOT_FOUND));
            }
        } else {
            if (FileUtil.isExist(obj.getMediaPath())) {
                onShareFail(SocialError.make(SocialError.CODE_NOT_SUPPORT, "微信朋友圈不支持本地视频分享"));
                return;
            }
            BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_32)
                    .continueWith(new ThumbTask(TAG, "shareVideo") {
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

                        @Override
                        public void onFail(SocialError error) {
                            onShareFail(error);
                        }
                    }, Task.UI_THREAD_EXECUTOR);
        }
    }

    private void subscribeMiniProgram(final int shareTarget, Activity activity, final ShareObj obj) {
        SubscribeMiniProgramMsg.Req req = new SubscribeMiniProgramMsg.Req();
        req.miniProgramAppId = obj.getWxMiniOriginId();
        mWxApi.sendReq(req);
    }

    // 分享小程序
    private void shareMiniProgram(final int shareTarget, Activity activity, final ShareObj obj) {
        int wxMiniType = obj.getWxMiniType();
        String originId = obj.getWxMiniOriginId();
        String pagePath = obj.getWxMiniPagePath();

        if (wxMiniType < 0 || SocialUtil.isAnyEmpty(originId, pagePath)) {
            onShareFail(SocialError.make(SocialError.CODE_PARAM_ERROR, "shareMiniProgram extra = " + obj.toString()));
            return;
        }

        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_128)
                .continueWith(new ThumbTask(TAG, "shareMini") {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
                        miniProgramObj.webpageUrl = obj.getTargetUrl();
                        miniProgramObj.miniprogramType = wxMiniType;
                        miniProgramObj.userName = originId; // 小程序原始id
                        miniProgramObj.path = pagePath; // 小程序页面路径
                        miniProgramObj.withShareTicket = true;
                        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
                        msg.title = obj.getTitle(); // 小程序消息title
                        msg.description = obj.getSummary(); // 小程序消息desc
                        msg.thumbData = thumbData; // 小程序消息封面图片，小于128k
                        // 目前只能分享给朋友
                        sendMsgToWx(msg, Target.SHARE_WX_FRIENDS, "miniProgram");
                    }

                    @Override
                    public void onFail(SocialError error) {
                        onShareFail(error);
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
