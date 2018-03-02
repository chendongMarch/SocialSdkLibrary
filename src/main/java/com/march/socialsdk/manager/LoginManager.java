package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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
public class LoginManager extends BaseManager {

    public static final String TAG = LoginManager.class.getSimpleName();

    private static WeakReference<OnLoginListener> sListener;

    /**
     * 开始登陆，供外面使用
     *
     * @param context       context
     * @param loginTarget   登陆类型
     * @param loginListener 登陆监听
     */
    public static void login(Context context, @Target.LoginTarget int loginTarget, OnLoginListener loginListener) {
        sListener = new WeakReference<>(loginListener);
        IPlatform platform = newPlatform(context, loginTarget);
        if (!platform.isInstall(context)) {
            loginListener.onFailure(new SocialError(SocialError.CODE_NOT_INSTALL));
            return;
        }
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(KEY_ACTION_TYPE, ACTION_TYPE_LOGIN);
        intent.putExtra(KEY_LOGIN_TARGET, loginTarget);
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
        int actionType = intent.getIntExtra(KEY_ACTION_TYPE, INVALID_PARAM);
        int loginTarget = intent.getIntExtra(KEY_LOGIN_TARGET, INVALID_PARAM);
        if (actionType == INVALID_PARAM) {
            LogUtils.e(TAG, "_actionLogin actionType无效");
            return;
        }
        if (actionType != ACTION_TYPE_LOGIN) {
            return;
        }
        if (loginTarget == INVALID_PARAM) {
            LogUtils.e(TAG, "shareTargetType无效");
            return;
        }
        OnLoginListener listener = sListener.get();
        if (sListener == null || listener == null) {
            LogUtils.e(TAG, "请设置 OnLoginListener");
            return;
        }
        if (getPlatform() == null) {
            return;
        }
        OnLoginListener newLoginListener = wrapListener(activity);
        getPlatform().login(activity, newLoginListener);
    }

    private static OnLoginListener wrapListener(Activity activity) {
        return new FinishLoginListener(activity);
    }

    static class FinishLoginListener implements OnLoginListener {

        private WeakReference<Activity> mActivityWeakRef;

        FinishLoginListener(Activity activity) {
            mActivityWeakRef = new WeakReference<>(activity);
        }

        @Override
        public void onStart() {
            if (sListener != null && sListener.get() != null) {
                sListener.get().onStart();
            }
        }

        private void finish() {
            finishProcess(mActivityWeakRef.get());
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (sListener != null && sListener.get() != null) {
                sListener.get().onSuccess(loginResult);
            }
            finish();
        }

        @Override
        public void onCancel() {
            if (sListener != null && sListener.get() != null) {
                sListener.get().onCancel();
            }
            finish();
        }

        @Override
        public void onFailure(SocialError e) {
            if (sListener != null && sListener.get() != null) {
                sListener.get().onFailure(e);
            }
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
