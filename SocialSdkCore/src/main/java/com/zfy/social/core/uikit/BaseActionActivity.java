package com.zfy.social.core.uikit;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.zfy.social.core.manager.GlobalPlatform;
import com.zfy.social.core.platform.IPlatform;

/**
 * CreateAt : 2017/1/8
 * Describe : 激活分享登陆的 通用 Activity
 *
 * @author chendong
 */
public class BaseActionActivity extends Activity {

    public static final String TAG = BaseActionActivity.class.getSimpleName();

    private boolean mIsNotFirstResume = false;

    protected void handleResp(Object resp) {
        IPlatform platform = getPlatform();
        if (platform != null) {
            platform.onResponse(resp);
        }
        checkFinish(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for wx & dd
        if (getPlatform() != null) {
            getPlatform().handleIntent(this);
        }
        GlobalPlatform.dispatchAction(this, getIntent().getIntExtra(GlobalPlatform.KEY_ACTION_TYPE, -1));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getPlatform() != null) {
            getPlatform().handleIntent(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsNotFirstResume) {
            if (getPlatform() != null) {
                getPlatform().handleIntent(this);
            }
            // 留在目标 app 后在返回会再次 resume
            checkFinish(true);
        } else {
            mIsNotFirstResume = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalPlatform.release(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getPlatform() != null) {
            getPlatform().onActivityResult(this, requestCode, resultCode, data);
        }
        checkFinish(false);
    }


    public void checkFinish(boolean postFinish) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!isFinishing() && !isDestroyed()) {
                if (postFinish) {
                    GlobalPlatform.dispatchUIDestroy();
                }
                finish();
                overridePendingTransition(0, 0);
            }
        } else {
            if (!isFinishing()) {
                if (postFinish) {
                    GlobalPlatform.dispatchUIDestroy();
                }
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    private IPlatform getPlatform() {
        IPlatform platform = GlobalPlatform.getCurrentPlatform();
        if (platform == null) {
            checkFinish(false);
            return null;
        } else
            return platform;
    }
}
