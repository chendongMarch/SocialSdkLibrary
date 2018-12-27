package com.zfy.social.core.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginListener;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.token.AccessToken;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.util.SocialUtil;

import java.lang.ref.WeakReference;
/**
 * CreateAt : 2017/5/19
 * Describe : 登陆管理类，使用该类进行登陆操作
 *
 * @author chendong
 */
public class LoginManager {

    public static final String TAG = LoginManager.class.getSimpleName();

    private static OnLoginListener sListener;

    /**
     * 开始登陆，供外面使用
     *
     * @param activity       context
     * @param loginTarget   登陆类型
     * @param loginListener 登陆监听
     */
    public static void login(Activity activity, @Target.LoginTarget int loginTarget, OnLoginListener loginListener) {
        loginListener.onStart();
        sListener = loginListener;
        IPlatform platform = GlobalPlatform.makePlatform(activity, loginTarget);
        if (!platform.isInstall(activity)) {
            loginListener.onFailure(SocialError.make(SocialError.CODE_NOT_INSTALL));
            return;
        }
        Intent intent = new Intent(activity, platform.getUIKitClazz());
        intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_LOGIN);
        intent.putExtra(GlobalPlatform.KEY_LOGIN_TARGET, loginTarget);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }


    /**
     * 激活登陆
     *
     * @param activity activity
     */
    static void _actionLogin(final Activity activity) {
        Intent intent = activity.getIntent();
        int actionType = intent.getIntExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.INVALID_PARAM);
        int loginTarget = intent.getIntExtra(GlobalPlatform.KEY_LOGIN_TARGET, GlobalPlatform.INVALID_PARAM);
        if (actionType == GlobalPlatform.INVALID_PARAM) {
            SocialUtil.e(TAG, "_actionLogin actionType无效");
            return;
        }
        if (actionType != GlobalPlatform.ACTION_TYPE_LOGIN) {
            return;
        }
        if (loginTarget == GlobalPlatform.INVALID_PARAM) {
            SocialUtil.e(TAG, "shareTargetType无效");
            return;
        }
        if (sListener == null) {
            SocialUtil.e(TAG, "请设置 OnLoginListener");
            return;
        }
        if (GlobalPlatform.getPlatform() == null) {
            return;
        }
        GlobalPlatform.getPlatform().login(activity, new OnLoginListenerWrap(activity));
    }


    static class OnLoginListenerWrap implements OnLoginListener {

        private WeakReference<Activity> mActivityWeakRef;

        OnLoginListenerWrap(Activity activity) {
            mActivityWeakRef = new WeakReference<>(activity);
        }

        @Override
        public void onStart() {
            if (sListener != null) {
                sListener.onStart();
            }
        }

        private void finish() {
            GlobalPlatform.release(mActivityWeakRef.get());
            sListener = null;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (sListener != null) {
                sListener.onSuccess(loginResult);
            }
            finish();
        }

        @Override
        public void onCancel() {
            if (sListener != null) {
                sListener.onCancel();
            }
            finish();
        }

        @Override
        public void onFailure(SocialError e) {
            if (sListener != null) {
                sListener.onFailure(e);
            }
            finish();
        }
    }

    public static void clearAllToken(Context context) {
        AccessToken.clearToken(context, Target.LOGIN_QQ);
        AccessToken.clearToken(context, Target.LOGIN_WX);
        AccessToken.clearToken(context, Target.LOGIN_WB);
    }

    public static void clearToken(Context context, @Target.LoginTarget int loginTarget) {
        AccessToken.clearToken(context, loginTarget);
    }

}
