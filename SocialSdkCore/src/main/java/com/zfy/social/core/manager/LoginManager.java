package com.zfy.social.core.manager;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;

import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.model.LoginObj;
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
     * @param act 发起登录的 activity
     * @param target   目标平台
     * @param listener 回调
     */
    public static void login(Activity act, int target, OnLoginStateListener listener) {
        login(act, target, null, listener);
    }

    /**
     * 发起登录
     *
     * @param act      发起登录的 activity
     * @param target   目标平台
     * @param listener 回调
     */
    public static void login(Activity act, int target, LoginObj obj, OnLoginStateListener listener) {
        if (sMgr != null) {
            sMgr.onHostActivityDestroy();
        }

        if (sMgr == null) {
            sMgr = new _InternalMgr();
        }
        sMgr.preLogin(act, target, obj, listener);
    }


    public static void clear() {
        if (sMgr != null) {
            sMgr.onHostActivityDestroy();
        }
        GlobalPlatform.release(null);
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
        private LoginObj currentObj;

        private WeakReference<Activity> fakeActivity;
        private WeakReference<Activity> originActivity;

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onHostActivityDestroy() {
            onProcessFinished();
        }

        // 流程结束，回收资源
        private void onProcessFinished() {
            if (wrapListener != null) {
                wrapListener.listener = null;
            }
            if (fakeActivity != null) {
                GlobalPlatform.release(fakeActivity.get());
                fakeActivity.clear();
            } else {
                GlobalPlatform.release(null);
            }
            if (originActivity != null) {
                originActivity.clear();
            }
            currentTarget = -1;
            currentObj = null;
            stateListener = null;
            wrapListener = null;
            fakeActivity = null;
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
                final LoginObj obj,
                final OnLoginStateListener listener) {

            currentObj = obj;
            stateListener = listener;
            currentTarget = target;
            originActivity = new WeakReference<>(activity);
            IPlatform platform = GlobalPlatform.newPlatformByTarget(activity, target);
            GlobalPlatform.savePlatform(platform);


            if (target == Target.LOGIN_WX_SCAN) {
                wrapListener = new OnLoginListenerWrap(stateListener);
                GlobalPlatform.getCurrentPlatform().login(activity, target, obj, wrapListener);
                return;
            }

            if (!platform.isInstall(activity)) {
                stateListener.onState(originActivity.get(), LoginResult.failOf(target, SocialError.make(SocialError.CODE_NOT_INSTALL)));
                return;
            }

            Intent intent = new Intent(activity, platform.getUIKitClazz());
            intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_LOGIN);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);

        }

        /**
         * 激活登录，由透明 Activity 真正的激活登录
         *
         * @param act 透明 activity
         */
        private void postLogin(Activity act) {
            stateListener.onState(originActivity.get(), LoginResult.stateOf(LoginResult.STATE_ACTIVE, currentTarget));
            fakeActivity = new WeakReference<>(act);

            if (currentTarget == -1) {
                stateListener.onState(act,
                        LoginResult.failOf(currentTarget,
                                SocialError.make(SocialError.CODE_COMMON_ERROR, "login target error")));
                return;
            }

            if (stateListener == null) {
                stateListener.onState(act,
                        LoginResult.failOf(currentTarget,
                                SocialError.make(SocialError.CODE_COMMON_ERROR, "没有设置 login listener")));
                return;
            }
            if (GlobalPlatform.getCurrentPlatform() == null) {
                stateListener.onState(act,
                        LoginResult.failOf(currentTarget,
                                SocialError.make(SocialError.CODE_COMMON_ERROR, "创建的 platform 失效")));
                return;
            }

            wrapListener = new OnLoginListenerWrap(stateListener);
            GlobalPlatform.getCurrentPlatform().login(act, currentTarget, currentObj, wrapListener);
        }
    }


    // 用于分享结束后，回收资源
    private static class OnLoginListenerWrap implements OnLoginStateListener {

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
        public void onState(Activity activity, LoginResult result) {
            if (listener != null) {
                result.target = sMgr.currentTarget;
                listener.onState(getAct(), result);
            }

            if (result.state == LoginResult.STATE_SUCCESS
                    || result.state == LoginResult.STATE_FAIL
                    || result.state == LoginResult.STATE_CANCEL) {
                if (listener != null) {
                    listener.onState(getAct(), LoginResult.completeOf(sMgr.currentTarget));
                }
                listener = null;
                clear();
            }
        }
    }

}
