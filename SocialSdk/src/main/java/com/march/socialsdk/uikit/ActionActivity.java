package com.march.socialsdk.uikit;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.march.socialsdk.helper.PlatformLog;
import com.march.socialsdk.manager.BaseManager;
import com.march.socialsdk.manager.LoginManager;
import com.march.socialsdk.manager.ShareManager;
import com.march.socialsdk.platform.IPlatform;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * CreateAt : 2017/1/8
 * Describe :
 *
 * @author chendong
 */

public class ActionActivity extends Activity
        implements IWeiboHandler.Response, IWXAPIEventHandler {

    public static final String TAG = ActionActivity.class.getSimpleName();

    private int     actionType;
    private boolean isNotFirstResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPlatform() != null)
            getPlatform().onNewIntent(this);
        actionType = getIntent().getIntExtra(BaseManager.KEY_ACTION_TYPE, -1);
        if (actionType == -1) {
            PlatformLog.e(TAG, "actionType无效");
            checkFinish();
            return;
        }
        switch (actionType) {
            case BaseManager.ACTION_TYPE_LOGIN:
                LoginManager._actionLogin(this);
                break;
            case BaseManager.ACTION_TYPE_SHARE:
                ShareManager._actionShare(this);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNotFirstResume) {
            if (getPlatform() != null)
                getPlatform().onNewIntent(this);
            checkFinish();
        } else {
            isNotFirstResume = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getPlatform() != null)
            getPlatform().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        checkFinish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getPlatform() != null)
            getPlatform().onNewIntent(this);
        checkFinish();
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if (getPlatform() != null)
            getPlatform().onResponse(baseResponse);
        checkFinish();
    }

    @Override
    public void onResp(BaseResp resp) {
        //从微信页面返回的数据
        if (getPlatform() != null)
            getPlatform().onResponse(resp);
        Log.e(TAG, "onResp:" + "resp.getType():" + resp.getType() + "resp.errCode:" + resp.errCode + "resp.errStr:" + resp.errStr);
        checkFinish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // 发起微信请求将会经过的方法
        Log.e(TAG, "onReq: " + baseReq.toString());
    }


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
        IPlatform platform = BaseManager.getPlatform();
        if (platform == null) {
            PlatformLog.e(TAG, "platform is null");
            checkFinish();
            return null;
        } else
            return platform;
    }
}
