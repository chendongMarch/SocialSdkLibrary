package com.march.socialsdk.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.utils.TokenStoreUtils;
import com.march.socialsdk.utils.LogUtils;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.uikit.ActionActivity;

import java.lang.ref.WeakReference;

/**
 * CreateAt : 2017/5/19
 * Describe : 登陆管理类，使用该类进行登陆操作
 *
 * @author chendong
 */
public class LoginManager {

    public static final String TAG = LoginManager.class.getSimpleName();

    static OnLoginListener sListener;

    /**
     * 开始登陆，供外面使用
     *
     * @param context       context
     * @param loginTarget   登陆类型
     * @param loginListener 登陆监听
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void login(Context context, @Target.LoginTarget int loginTarget, OnLoginListener loginListener) {
        sListener = loginListener;
        IPlatform platform = PlatformManager.newPlatform(context, loginTarget);
        if (!platform.isInstall(context)) {
            loginListener.onFailure(new SocialError(SocialError.CODE_NOT_INSTALL));
            return;
        }
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(PlatformManager.KEY_ACTION_TYPE, PlatformManager.ACTION_TYPE_LOGIN);
        intent.putExtra(PlatformManager.KEY_LOGIN_TARGET, loginTarget);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }


    /**
     * 激活登陆
     *
     * @param activity activity
     */
    public static void _actionLogin(final Activity activity) {
        Intent intent = activity.getIntent();
        int actionType = intent.getIntExtra(PlatformManager.KEY_ACTION_TYPE, PlatformManager.INVALID_PARAM);
        int loginTarget = intent.getIntExtra(PlatformManager.KEY_LOGIN_TARGET, PlatformManager.INVALID_PARAM);
        if (actionType == PlatformManager.INVALID_PARAM) {
            LogUtils.e(TAG, "_actionLogin actionType无效");
            return;
        }
        if (actionType != PlatformManager.ACTION_TYPE_LOGIN) {
            return;
        }
        if (loginTarget == PlatformManager.INVALID_PARAM) {
            LogUtils.e(TAG, "shareTargetType无效");
            return;
        }
        if (sListener == null) {
            LogUtils.e(TAG, "请设置 OnLoginListener");
            return;
        }
        if (PlatformManager.getPlatform() == null) {
            return;
        }
        PlatformManager.getPlatform().login(activity, new FinishLoginListener(activity));
    }


    static class FinishLoginListener implements OnLoginListener {

        private WeakReference<Activity> mActivityWeakRef;

        FinishLoginListener(Activity activity) {
            mActivityWeakRef = new WeakReference<>(activity);
        }

        @Override
        public void onStart() {
            if (sListener != null) sListener.onStart();
        }

        private void finish() {
            PlatformManager.release(mActivityWeakRef.get());
            sListener = null;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (sListener != null) sListener.onSuccess(loginResult);
            finish();
        }

        @Override
        public void onCancel() {
            if (sListener != null) sListener.onCancel();
            finish();
        }

        @Override
        public void onFailure(SocialError e) {
            if (sListener != null) sListener.onFailure(e);
            finish();
        }
    }

    public static void clearAllToken(Context context) {
        TokenStoreUtils.clearToken(context, Target.LOGIN_QQ);
        TokenStoreUtils.clearToken(context, Target.LOGIN_WX);
        TokenStoreUtils.clearToken(context, Target.LOGIN_WB);
    }

    public static void clearToken(Context context, @Target.LoginTarget int loginTarget) {
        TokenStoreUtils.clearToken(context, loginTarget);
    }
}
