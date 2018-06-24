package com.march.socialsdk.uikit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.march.socialsdk.manager.PlatformManager;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.utils.SocialLogUtils;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * CreateAt : 2017/1/8
 * Describe : 激活分享登陆的 通用 Activity
 *
 * @author chendong
 */
public class ActionActivity extends Activity implements WbShareCallback, IWXAPIEventHandler, IDDAPIEventHandler {

    public static final String TAG = ActionActivity.class.getSimpleName();

    private boolean mIsNotFirstResume = false;
    private int mActionType = -1;

    private void logMsg(String msg) {
        // LogUtils.e(TAG, "ActionActivity - " + msg + "  " + hashCode());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for wx & dd
        if (getPlatform() != null) {
            getPlatform().handleIntent(this);
        }
        mActionType = getIntent().getIntExtra(PlatformManager.KEY_ACTION_TYPE, -1);
        PlatformManager.action(this,mActionType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsNotFirstResume) {
            if (getPlatform() != null) {
                getPlatform().handleIntent(this);
            }
            // 留在目标 app 后在返回会再次 resume
            checkFinish();
        } else {
            mIsNotFirstResume = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlatformManager.release(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        logMsg("handleIntent");
        setIntent(intent);
        if (getPlatform() != null)
            getPlatform().handleIntent(this);
    }

    public void onRespHandler(Object resp) {
        IPlatform platform = getPlatform();
        if (platform != null) {
            platform.onResponse(resp);
        }
        checkFinish();
    }

    //////////////////////////////  -- qq --  //////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logMsg("onActivityResult");
        if (getPlatform() != null)
            getPlatform().onActivityResult(requestCode, resultCode, data);
        checkFinish();
    }

    //////////////////////////////  -- 微博 --  //////////////////////////////

    @Override
    public void onWbShareSuccess() {
        onRespHandler(WBConstants.ErrorCode.ERR_OK);
    }

    @Override
    public void onWbShareCancel() {
        onRespHandler(WBConstants.ErrorCode.ERR_CANCEL);
    }

    @Override
    public void onWbShareFail() {
        onRespHandler(WBConstants.ErrorCode.ERR_FAIL);
    }

    //////////////////////////////  -- 微信 --  //////////////////////////////

    @Override
    public void onResp(BaseResp resp) {
        logMsg("Wx onResp");
        onRespHandler(resp);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        logMsg("Wx onReq");
    }


    //////////////////////////////  -- 钉钉 --  //////////////////////////////

    @Override
    public void onReq(com.android.dingtalk.share.ddsharemodule.message.BaseReq baseReq) {
        SocialLogUtils.e(TAG, "dd onReq: ", baseReq);
    }

    @Override
    public void onResp(com.android.dingtalk.share.ddsharemodule.message.BaseResp baseResp) {
        onRespHandler(baseResp);
    }

    //////////////////////////////  -- help --  //////////////////////////////

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void checkFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!isFinishing() && !isDestroyed()) {
                finish();
                overridePendingTransition(0, 0);
            }
        } else {
            if (!isFinishing()) {
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    private IPlatform getPlatform() {
        IPlatform platform = PlatformManager.getPlatform();
        if (platform == null) {
            // LogUtils.e(TAG, "platform is null");
            checkFinish();
            return null;
        } else
            return platform;
    }

}
