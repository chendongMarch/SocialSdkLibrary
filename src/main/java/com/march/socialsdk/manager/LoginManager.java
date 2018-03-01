package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.utils.TokenStoreUtils;
import com.march.socialsdk.utils.LogUtils;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.uikit.ActionActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * CreateAt : 2017/5/19
 * Describe : 登陆管理类，使用该类进行登陆操作
 *
 * @author chendong
 */
public class LoginManager extends BaseManager {

    public static final String TAG = LoginManager.class.getSimpleName();

    private static WeakReference<OnLoginListener> sOnLoginListenerWeakRef;

    /**
     * 开始登陆，供外面使用
     *
     * @param context       context
     * @param loginTarget   登陆类型
     * @param loginListener 登陆监听
     */
    public static void login(Context context, @Target.LoginTarget int loginTarget, OnLoginListener loginListener) {
        sOnLoginListenerWeakRef = new WeakReference<>(loginListener);
        buildPlatform(context, loginTarget);
        if (getCurrentPlatform() == null) {
            return;
        }
        if (!getCurrentPlatform().isInstall(context)) {
            loginListener.onFailure(new SocialError(SocialError.CODE_NOT_INSTALL));
            return;
        }
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(KEY_ACTION_TYPE, ACTION_TYPE_LOGIN);
        intent.putExtra(KEY_LOGIN_TARGET, loginTarget);
        context.startActivity(intent);
        if (context instanceof Activity)
            ((Activity) context).overridePendingTransition(0, 0);
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
        if (sOnLoginListenerWeakRef == null) {
            LogUtils.e(TAG, "请设置 OnLoginListener");
        }
        if (getCurrentPlatform() == null) {
            return;
        }
        getCurrentPlatform().login(activity, getOnLoginListenerWrap(activity));
    }


    private static OnLoginListener getOnLoginListenerWrap(final Activity activity) {
        return (OnLoginListener) Proxy.newProxyInstance(OnLoginListener.class.getClassLoader(),
                new Class[]{OnLoginListener.class},
                new FinishActivityInvocationHandler(activity));
    }

    // 动态代理数据
    static class FinishActivityInvocationHandler implements InvocationHandler {

        private WeakReference<Activity> mActivityWeakRef;
        private OnLoginListener mOnLoginListener;

        FinishActivityInvocationHandler(Activity activity) {
            mActivityWeakRef = new WeakReference<>(activity);
            mOnLoginListener = sOnLoginListenerWeakRef.get();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }
            if (method.getDeclaringClass() == OnShareListener.class && mOnLoginListener != null) {
                Object invoke = method.invoke(mOnLoginListener, args);
                if (TextUtils.equals(method.getName(), "onSuccess")
                        || TextUtils.equals(method.getName(), "onFailure")
                        || TextUtils.equals(method.getName(), "onCancel")) {
                    finishProcess(mActivityWeakRef.get());
                    sOnLoginListenerWeakRef.clear();
                }
                return invoke;
            }
            return null;
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
