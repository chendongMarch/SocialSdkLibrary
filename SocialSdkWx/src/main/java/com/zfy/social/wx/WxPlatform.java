package com.zfy.social.wx;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
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
import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.SocialKeys;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.common.ThumbTask;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginListener;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.AbsPlatform;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.util.BitmapUtil;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.SocialUtil;

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

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            SocialOptions config = SocialSdk.getConfig();
            if (!SocialUtil.isAnyEmpty(config.getWxAppId(), config.getWxSecretKey())) {
                platform = new WxPlatform(context, config.getWxAppId(), config.getWxSecretKey(), config.getAppName());
            }
            return platform;
        }

        @Override
        public int getTarget() {
            return Target.PLATFORM_WX;
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
                    mOnShareListener.onFailure(SocialError.make(SocialError.CODE_SDK_ERROR, "分享失败"));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 分享被拒绝
                    mOnShareListener.onFailure(SocialError.make(SocialError.CODE_SDK_ERROR, "分享被拒绝"));
                    break;
            }
        }
    }

    @Override
    public void login(Activity context, OnLoginListener loginListener) {
        if (!mWxApi.isWXAppSupportAPI()) {
            loginListener.onFailure(SocialError.make(SocialError.CODE_VERSION_LOW));
            return;
        }
        mWeChatLoginHelper = new WxLoginHelper(context, mWxApi, mAppId);
        mWeChatLoginHelper.login(mWxSecret, loginListener);
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
            mOnShareListener.onFailure(SocialError.make(SocialError.CODE_SDK_ERROR, TAG + "#sendMsgToWx失败，可能是参数错误"));
        }
    }

    @Override
    protected void dispatchShare(Activity activity, int shareTarget, ShareObj obj) {
        switch (obj.getShareObjType()) {
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
            case ShareObj.SHARE_TYPE_WX_MINI:
                shareMiniProgram(shareTarget, activity, obj);
                break;
        }
    }

    // 分享 app
    private void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        boolean rst = mWxApi.openWXApp();
        if (rst) {
            mOnShareListener.onSuccess();
        } else {
            mOnShareListener.onFailure(SocialError.make(SocialError.CODE_CANNOT_OPEN_ERROR));
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
                .continueWith(new ThumbTask(TAG, "shareImage", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        shareImage(shareTarget, obj.getSummary(), obj.getThumbImagePath(), thumbData);
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
                .continueWith(new ThumbTask(TAG, "shareWeb", mOnShareListener) {
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

    // 分享音乐
    private void shareMusic(final int shareTarget, Activity activity, final ShareObj obj) {
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_32)
                .continueWith(new ThumbTask(TAG, "shareMusic", mOnShareListener) {
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

    // 分享视频
    private void shareVideo(final int shareTarget, Activity activity, final ShareObj obj) {
        if (shareTarget == Target.SHARE_WX_FRIENDS) {
            if (FileUtil.isHttpPath(obj.getMediaPath())) {
                shareWeb(shareTarget, activity, obj);
            } else if (FileUtil.isExist(obj.getMediaPath())) {
                shareVideoByIntent(activity, obj, SocialValues.WECHAT_PKG, SocialValues.WX_FRIEND_PAGE);
            } else {
                mOnShareListener.onFailure(SocialError.make(SocialError.CODE_FILE_NOT_FOUND));
            }
        } else {
            BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_32)
                    .continueWith(new ThumbTask(TAG, "shareVideo", mOnShareListener) {
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

    // 分享小程序
    private void shareMiniProgram(final int shareTarget, Activity activity, final ShareObj obj) {
        Bundle extra = obj.getExtra();
        if (extra == null) {
            mOnShareListener.onFailure(SocialError.make(SocialError.CODE_PARAM_ERROR, "shareMiniProgram extra is null"));
            return;
        }
        int type = extra.getInt(SocialKeys.KEY_WX_MINI_TYPE, -1);
        String originId = extra.getString(SocialKeys.KEY_WX_MINI_ORIGIN_ID, "");
        String pagePath = extra.getString(SocialKeys.KEY_WX_MINI_PATH, "");

        if (type < 0 || SocialUtil.isAnyEmpty(originId, pagePath)) {
            mOnShareListener.onFailure(SocialError.make(SocialError.CODE_PARAM_ERROR,
                    "shareMiniProgram extra = " + extra.toString()));
            return;
        }
        BitmapUtil.getStaticSizeBitmapByteByPathTask(obj.getThumbImagePath(), THUMB_IMAGE_SIZE_128)
                .continueWith(new ThumbTask(TAG, "shareMini", mOnShareListener) {
                    @Override
                    public void onSuccess(byte[] thumbData) {
                        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
                        miniProgramObj.webpageUrl = obj.getTargetUrl();
                        miniProgramObj.miniprogramType = type;
                        miniProgramObj.userName = originId; // 小程序原始id
                        miniProgramObj.path = pagePath; // 小程序页面路径
                        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
                        msg.title = obj.getTitle(); // 小程序消息title
                        msg.description = obj.getSummary(); // 小程序消息desc
                        msg.thumbData = thumbData; // 小程序消息封面图片，小于128k
                        // 目前只能分享给朋友
                        sendMsgToWx(msg, Target.SHARE_WX_FRIENDS, "miniProgram");

                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
