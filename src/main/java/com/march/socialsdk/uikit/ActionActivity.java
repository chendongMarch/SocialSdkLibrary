package com.march.socialsdk.uikit;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.march.socialsdk.manager.PlatformManager;
import com.march.socialsdk.platform.IPlatform;

/**
 * CreateAt : 2017/1/8
 * Describe : 激活分享登陆的 通用 Activity
 *
 * @author chendong
 */
public class ActionActivity extends SocialReceiver {

    public static final String TAG = ActionActivity.class.getSimpleName();

    private boolean mIsNotFirstResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // for wx & dd
        if (getPlatform() != null) {
            getPlatform().handleIntent(this);
        }
        PlatformManager.action(this, getIntent().getIntExtra(PlatformManager.KEY_ACTION_TYPE, -1));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getPlatform() != null)
            getPlatform().handleIntent(this);
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
    void handleResp(Object resp) {
        IPlatform platform = getPlatform();
        if (platform != null) {
            platform.onResponse(resp);
        }
        checkFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getPlatform() != null)
            getPlatform().onActivityResult(requestCode, resultCode, data);
        checkFinish();
    }


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
            checkFinish();
            return null;
        } else
            return platform;
    }

}
