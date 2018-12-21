package com.zfy.social.wb.uikit;

import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.zfy.social.core.uikit.BaseActionActivity;

/**
 * CreateAt : 2017/1/8
 * Describe : 激活分享登陆的 通用 Activity
 *
 * @author chendong
 */
public class WbActionActivity extends BaseActionActivity implements WbShareCallback {
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
}
