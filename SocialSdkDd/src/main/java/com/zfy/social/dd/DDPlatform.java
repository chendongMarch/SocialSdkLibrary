package com.zfy.social.dd;

import android.app.Activity;
import android.content.Context;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.android.dingtalk.share.ddsharemodule.message.DDImageMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDMediaMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDTextMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDWebpageMessage;
import com.android.dingtalk.share.ddsharemodule.message.SendMessageToDD;
import com.zfy.social.core.SocialOptions;
import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginListener;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.AbsPlatform;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.IntentShareUtil;
import com.zfy.social.core.util.SocialUtil;
import com.zfy.social.dd.uikit.DDActionActivity;

/**
 * CreateAt : 2018/2/11
 * Describe : 钉钉分享
 * 文档：https://open-doc.dingtalk.com/doc2/detail.htm?spm=0.0.0.0.MZqdJG&treeId=178&articleId=104982&docType=1
 *
 * @author chendong
 */
public class DDPlatform extends AbsPlatform {

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            SocialOptions config = SocialSdk.opts();
            if (!SocialUtil.isAnyEmpty(config.getDdAppId())) {
                platform = new DDPlatform(context, config.getDdAppId(), config.getAppName(), target);
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_DD;
        }

        @Override
        public boolean checkShareTarget(int shareTarget) {
            return shareTarget == Target.SHARE_DD;
        }
    }

    private IDDShareApi mDdShareApi;

    private DDPlatform(Context context, String appId, String appName, int target) {
        super(context, appId, appName, target);
        mDdShareApi = DDShareApiFactory.createDDShareApi(context, appId, false);
    }

    @Override
    public Class getUIKitClazz() {
        return DDActionActivity.class;
    }

    @Override
    public void recycle() {
        mDdShareApi = null;
    }

    @Override
    public void handleIntent(Activity activity) {
        super.handleIntent(activity);
        mDdShareApi.handleIntent(activity.getIntent(), (IDDAPIEventHandler) activity);
    }

    @Override
    public void onResponse(Object resp) {
        if (!(resp instanceof BaseResp)) {
            return;
        }
        BaseResp baseResp = (BaseResp) resp;
        int errCode = baseResp.mErrCode;
        switch (errCode) {
            case BaseResp.ErrCode.ERR_OK:
                mOnShareListener.onSuccess(mTarget);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_SENT_FAILED:
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                mOnShareListener.onFailure(SocialError.make(SocialError.CODE_SDK_ERROR,
                        "钉钉分享失败, code = " + baseResp.mErrCode + "，msg =" + baseResp.mErrStr));
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                mOnShareListener.onCancel();
                break;
        }
    }

    @Override
    public boolean isInstall(Context context) {
        return mDdShareApi != null && mDdShareApi.isDDAppInstalled() && mDdShareApi.isDDSupportAPI();
    }

    @Override
    public void login(Activity activity, OnLoginListener onLoginListener) {
        throw new UnsupportedOperationException("钉钉不支持登录操作");
    }

    @Override
    protected void dispatchShare(Activity activity, int shareTarget, ShareObj obj) {
        switch (obj.getType()) {
            case ShareObj.SHARE_TYPE_OPEN_APP:
                mDdShareApi.openDDApp();
                break;
            case ShareObj.SHARE_TYPE_TEXT:
                shareText(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_IMAGE:
                shareImage(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_APP:
                shareWeb(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_WEB:
                shareWeb(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_MUSIC:
                shareWeb(shareTarget, activity, obj);
                break;
            case ShareObj.SHARE_TYPE_VIDEO:
                shareVideo(shareTarget, activity, obj);
                break;
        }
    }

    // 分享文字
    private void shareText(int shareTarget, Activity activity, ShareObj obj) {
        //初始化一个DDTextMessage对象
        DDTextMessage textObject = new DDTextMessage();
        textObject.mText = obj.getSummary();
        //用DDTextMessage对象初始化一个DDMediaMessage对象
        DDMediaMessage mediaMessage = new DDMediaMessage();
        mediaMessage.mMediaObject = textObject;
        //构造一个Req
        SendMessageToDD.Req req = new SendMessageToDD.Req();
        req.mMediaMessage = mediaMessage;
        req.mTransaction = buildTransaction("text");
        //调用api接口发送消息到钉钉
        mDdShareApi.sendReq(req);
    }

    // 分享图片
    private void shareImage(int shareTarget, Activity activity, ShareObj obj) {
        //初始化一个DDImageMessage
        DDImageMessage imageObject = new DDImageMessage();
        // 支持网络图片
        // imageObject.mImageUrl = obj.getThumbImagePath();
        imageObject.mImagePath = obj.getThumbImagePath();
        //构造一个mMediaObject对象
        DDMediaMessage mediaMessage = new DDMediaMessage();
        mediaMessage.mMediaObject = imageObject;
        //构造一个Req
        SendMessageToDD.Req req = new SendMessageToDD.Req();
        req.mMediaMessage = mediaMessage;
        req.mTransaction = buildTransaction("image");
        //调用api接口发送消息到支付宝
        mDdShareApi.sendReq(req);
    }

    // 分享网页
    private void shareWeb(int shareTarget, Activity activity, ShareObj obj) {
        //初始化一个DDWebpageMessage并填充网页链接地址
        DDWebpageMessage webPageObject = new DDWebpageMessage();
        webPageObject.mUrl = obj.getTargetUrl();
        //构造一个DDMediaMessage对象
        DDMediaMessage webMessage = new DDMediaMessage();
        webMessage.mMediaObject = webPageObject;
        //填充网页分享必需参数，开发者需按照自己的数据进行填充
        webMessage.mTitle = obj.getTitle();
        webMessage.mContent = obj.getSummary();
        webMessage.mThumbUrl = obj.getThumbImagePath();
        // 网页分享的缩略图也可以使用bitmap形式传输
        // webMessage.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //构造一个Req
        SendMessageToDD.Req webReq = new SendMessageToDD.Req();
        webReq.mMediaMessage = webMessage;
        webReq.mTransaction = buildTransaction("web");
        //调用api接口发送消息到支付宝
        mDdShareApi.sendReq(webReq);
    }

    // 分享视频
    private void shareVideo(int shareTarget, Activity activity, ShareObj obj) {
        if (FileUtil.isHttpPath(obj.getMediaPath())) {
            shareWeb(shareTarget, activity, obj);
        } else if (FileUtil.isExist(obj.getMediaPath())) {
            IntentShareUtil.shareVideo(activity, obj, SocialValues.DD_PKG, SocialValues.DD_FRIEND_PAGE, mOnShareListener,mTarget);
        } else {
            mOnShareListener.onFailure(SocialError.make(SocialError.CODE_FILE_NOT_FOUND));
        }
    }


    private String buildTransaction(String tag) {
        return tag + System.currentTimeMillis();
    }
}
