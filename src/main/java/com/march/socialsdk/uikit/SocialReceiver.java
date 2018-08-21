package com.march.socialsdk.uikit;

import android.app.Activity;

// ding
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
// weibo
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.share.WbShareCallback;
// wechat
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * CreateAt : 2018/8/21
 * Describe : 屏蔽平台对于 ActionActivity 的差异性
 *
 * @author chendong
 */
public abstract class SocialReceiver extends Activity implements
        WbShareCallback,
        IWXAPIEventHandler,
        IDDAPIEventHandler {

    abstract void handleResp(Object resp);

    // ---------- for ding ding ---------
    @Override
    public void onReq(com.android.dingtalk.share.ddsharemodule.message.BaseReq baseReq) {
    }

    @Override
    public void onResp(com.android.dingtalk.share.ddsharemodule.message.BaseResp baseResp) {
        handleResp(baseResp);
    }

    // ---------- for weibo ---------
    @Override
    public void onWbShareSuccess() {
        handleResp(WBConstants.ErrorCode.ERR_OK);
    }

    @Override
    public void onWbShareCancel() {
        handleResp(WBConstants.ErrorCode.ERR_CANCEL);
    }

    @Override
    public void onWbShareFail() {
        handleResp(WBConstants.ErrorCode.ERR_FAIL);
    }

    // ---------- for wechat ---------

    @Override
    public void onResp(com.tencent.mm.opensdk.modelbase.BaseResp resp) {
        handleResp(resp);
    }

    @Override
    public void onReq(com.tencent.mm.opensdk.modelbase.BaseReq baseReq) {

    }

}
