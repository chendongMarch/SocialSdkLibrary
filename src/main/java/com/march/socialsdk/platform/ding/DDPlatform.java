package com.march.socialsdk.platform.ding;

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
import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.SocialSdkConfig;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.AbsPlatform;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.PlatformCreator;
import com.march.socialsdk.util.FileUtil;
import com.march.socialsdk.util.Util;

/**
 * CreateAt : 2018/2/11
 * Describe : 钉钉分享
 * 文档：https://open-doc.dingtalk.com/doc2/detail.htm?spm=0.0.0.0.MZqdJG&treeId=178&articleId=104982&docType=1
 * @author chendong
 */
public class DDPlatform extends AbsPlatform {

    private IDDShareApi mDdShareApi;

    public static class Creator implements PlatformCreator {
        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            SocialSdkConfig config = SocialSdk.getConfig();
            if (!Util.isAnyEmpty(config.getDdAppId())) {
                platform = new DDPlatform(context, config.getDdAppId(), config.getAppName());
            }
            return platform;
        }
    }

    DDPlatform(Context context, String appId, String appName) {
        super(appId, appName);
        mDdShareApi = DDShareApiFactory.createDDShareApi(context, appId, false);
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
                mOnShareListener.onSuccess();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_SENT_FAILED:
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                mOnShareListener.onFailure(new SocialError(SocialError.CODE_SDK_ERROR,
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
    protected void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {
        mDdShareApi.openDDApp();
    }

    @Override
    protected void shareText(int shareTarget, Activity activity, ShareObj obj) {
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

    @Override
    protected void shareImage(int shareTarget, Activity activity, ShareObj obj) {
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


    @Override
    protected void shareApp(int shareTarget, Activity activity, ShareObj obj) {
        shareWeb(shareTarget, activity, obj);
    }

    @Override
    protected void shareWeb(int shareTarget, Activity activity, ShareObj obj) {
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

    @Override
    protected void shareMusic(int shareTarget, Activity activity, ShareObj obj) {
        shareWeb(shareTarget, activity, obj);
    }

    @Override
    protected void shareVideo(int shareTarget, Activity activity, ShareObj obj) {
        if (FileUtil.isHttpPath(obj.getMediaPath())) {
            shareWeb(shareTarget, activity, obj);
        } else if (FileUtil.isExist(obj.getMediaPath())) {
            shareVideoByIntent(activity, obj, SocialConstants.DD_PKG, SocialConstants.DD_FRIEND_PAGE);
        } else {
            mOnShareListener.onFailure(new SocialError(SocialError.CODE_FILE_NOT_FOUND));
        }
    }


    private String buildTransaction(String tag) {
        return tag + System.currentTimeMillis();
    }
}
