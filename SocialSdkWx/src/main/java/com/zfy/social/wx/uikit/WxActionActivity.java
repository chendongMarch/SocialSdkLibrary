package com.zfy.social.wx.uikit;

import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.zfy.social.core.uikit.BaseActionActivity;

/**
 * CreateAt : 2017/1/8
 * Describe : 激活分享登陆的 通用 Activity
 *
 * @author chendong
 */
public class WxActionActivity extends BaseActionActivity implements IWXAPIEventHandler {


    @Override
    public void onResp(com.tencent.mm.opensdk.modelbase.BaseResp resp) {
        handleResp(resp);
    }

    @Override
    public void onReq(com.tencent.mm.opensdk.modelbase.BaseReq baseReq) {

    }
}
