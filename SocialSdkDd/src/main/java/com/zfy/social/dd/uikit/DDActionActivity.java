package com.zfy.social.dd.uikit;

import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.zfy.social.core.uikit.BaseActionActivity;

/**
 * CreateAt : 2017/1/8
 * Describe : 激活分享登陆的 通用 Activity
 *
 * @author chendong
 */
public class DDActionActivity extends BaseActionActivity implements IDDAPIEventHandler {

    @Override
    public void onReq(com.android.dingtalk.share.ddsharemodule.message.BaseReq baseReq) {
    }

    @Override
    public void onResp(com.android.dingtalk.share.ddsharemodule.message.BaseResp baseResp) {
        handleResp(baseResp);
    }

}
