package com.zfy.social.core.manager;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;

import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginListener;
import com.zfy.social.core.listener.OnLoginStateListener;
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

    private static _InternalMgr sMgr;

    /**
     * 发起登录
     *
     * @param activity 发起登录的 activity
     * @param target   目标平台
     * @param listener 回调
     */
    public static void login(
            final Activity activity,
            int target,
            final OnLoginStateListener listener
    ) {
        if (sMgr != null) {
            sMgr.onHostActivityDestroy();
        }
        if (sMgr == null) {
            sMgr = new _InternalMgr();
        }
        sMgr.preLogin(activity, target, listener);
    }

    public static void clear() {
        if (sMgr != null) {
            sMgr.onHostActivityDestroy();
        }
    }

    /**
     * 清理全部 token
     * @param context 上下文
     */
    public static void clearAllToken(Context context) {
        AccessToken.clearToken(context, Target.LOGIN_QQ);
        AccessToken.clearToken(context, Target.LOGIN_WX);
        AccessToken.clearToken(context, Target.LOGIN_WB);
    }

    /**
     * 清理指定平台的 token
     * @param context 上下文
     * @param loginTarget 目标平台
     */
    public static void clearToken(Context context, int loginTarget) {
        AccessToken.clearToken(context, loginTarget);
    }

    // 开始分享
    static void actionLogin(Activity activity) {
        if (sMgr != null) {
            sMgr.postLogin(activity);
        }
    }


    private static class _InternalMgr implements LifecycleObserver {

        private OnLoginStateListener stateListener;
        private LoginManager.OnLoginListenerWrap wrapListener;

        private int currentTarget;

        private WeakReference<Activity> fakeActivity;
        private WeakReference<Activity> originActivity;

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onHostActivityDestroy() {
            onProcessFinished();
            SocialUtil.e(TAG, "页面销毁，回收资源");
        }

        // 流程结束，回收资源
        private void onProcessFinished() {
            if (wrapListener != null) {
                wrapListener.clear();
            }
            if (fakeActivity != null) {
                GlobalPlatform.release(fakeActivity.get());
                fakeActivity.clear();
            }
            if (originActivity != null) {
                originActivity.clear();
            }
            currentTarget = -1;
            stateListener = null;
            wrapListener = null;
            fakeActivity = null;
            SocialUtil.e(TAG, "分享过程结束，回收资源");
        }


        /**
         * 开始登录分享，供外面调用
         *
         * @param activity 发起登录的 activity
         * @param listener 分享监听
         */
        private void preLogin(
                final Activity activity,
                final @Target.LoginTarget int target,
                final OnLoginStateListener listener
        ) {

            stateListener = listener;
            currentTarget = target;
            originActivity = new WeakReference<>(activity);
            IPlatform platform = GlobalPlatform.newPlatformByTarget(activity, target);
            if (!platform.isInstall(activity)) {
                stateListener.onState(originActivity.get(), LoginResult.failOf(target, SocialError.make(SocialError.CODE_NOT_INSTALL)));
                return;
            }
            Intent intent = new Intent(activity, platform.getUIKitClazz());
            intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_LOGIN);
            intent.putExtra(GlobalPlatform.KEY_LOGIN_TARGET, target);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);

        }

        /**
         * 激活登录，由透明 Activity 真正的激活登录
         *
         * @param activity 透明 activity
         */
        private void postLogin(Activity activity) {
            stateListener.onState(originActivity.get(), LoginResult.stateOf(LoginResult.STATE_ACTIVE, currentTarget));
            fakeActivity = new WeakReference<>(activity);
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
            if (stateListener == null) {
                SocialUtil.e(TAG, "请设置 OnLoginListener");
                return;
            }
            if (GlobalPlatform.getCurrentPlatform() == null) {
                return;
            }
            wrapListener = new OnLoginListenerWrap(stateListener);
            GlobalPlatform.getCurrentPlatform().login(activity, wrapListener);
        }
    }


    // 用于分享结束后，回收资源
    private static class OnLoginListenerWrap implements OnLoginListener {

        private OnLoginStateListener listener;

        OnLoginListenerWrap(OnLoginStateListener listener) {
            this.listener = listener;
        }


        private Activity getAct() {
            if (sMgr != null && sMgr.originActivity != null) {
                return sMgr.originActivity.get();
            }
            return null;
        }



        @Override
        public void onStart() {
            if (listener != null) {
                listener.onState(getAct(), LoginResult.startOf(sMgr.currentTarget));
            }
        }


        @Override
        public void onSuccess(LoginResult result) {
            if (listener != null) {
                listener.onState(getAct(), result);
                listener.onState(getAct(), LoginResult.completeOf(sMgr.currentTarget));
            }
            clear();
            sMgr.onProcessFinished();
        }

        @Override
        public void onCancel() {
            if (listener != null) {
                listener.onState(getAct(), LoginResult.cancelOf(sMgr.currentTarget));
                listener.onState(getAct(), LoginResult.completeOf(sMgr.currentTarget));
            }
            clear();
            sMgr.onProcessFinished();
        }


        @Override
        public void onFailure(SocialError e) {
            if (listener != null) {
                listener.onState(getAct(), LoginResult.failOf(sMgr.currentTarget, e));
                listener.onState(getAct(), LoginResult.completeOf(sMgr.currentTarget));
            }
            clear();
            sMgr.onProcessFinished();
        }

        private void clear() {
            listener = null;
        }
    }


}
