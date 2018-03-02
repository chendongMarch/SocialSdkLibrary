package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.Target;

import java.lang.ref.WeakReference;

/**
 * CreateAt : 2017/5/19
 * Describe : manager 基类
 *
 * @author chendong
 */
public abstract class BaseManager {

    public static final int INVALID_PARAM = -1;

    public static final int ACTION_TYPE_LOGIN = 0;
    public static final int ACTION_TYPE_SHARE = 1;

    public static final String KEY_SHARE_MEDIA_OBJ = "KEY_SHARE_MEDIA_OBJ"; // media obj key
    public static final String KEY_ACTION_TYPE = "KEY_ACTION_TYPE"; // action type

    public static final String KEY_SHARE_TARGET = "KEY_SHARE_TARGET"; // share target
    public static final String KEY_LOGIN_TARGET = "KEY_LOGIN_TARGET"; // login target

    private static WeakReference<IPlatform> sPlatformWeakRef;

    static @NonNull
    IPlatform newPlatform(Context context, int target) {
        if (SocialSdk.getConfig() == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + " SocialSdk.init() request");
        }
        IPlatform platform = SocialSdk.getPlatform(context, target);
        if (platform == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + "  创建platform失败，请检查参数 " + SocialSdk.getConfig().toString());
        }
        sPlatformWeakRef = new WeakReference<>(platform);
        return platform;
    }

    public static IPlatform getPlatform() {
        if (sPlatformWeakRef != null) {
            return sPlatformWeakRef.get();
        }
        return null;
    }

    static void finishProcess(Activity activity) {
        if (sPlatformWeakRef != null && sPlatformWeakRef.get() != null) {
            sPlatformWeakRef.get().recycle();
            sPlatformWeakRef.clear();
            sPlatformWeakRef = null;
        }
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

//
//
//    static Object wrapListener(final Activity activity, Class clz, Object listener) {
//        return Proxy.newProxyInstance(clz.getClassLoader(),
//                new Class[]{clz},
//                new FinishActivityHandler(activity, clz, listener));
//    }
//
//
//    // 动态代理数据
//    protected static class FinishActivityHandler implements InvocationHandler {
//
//        private WeakReference<Activity> mActivityWeakRef;
//        private Object mListener;
//        private Class mClz;
//
//        public FinishActivityHandler(Activity activity, Class clz, Object listener) {
//            mActivityWeakRef = new WeakReference<>(activity);
//            mListener = listener;
//            mClz = clz;
//        }
//
//        @Override
//        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            if (method.getDeclaringClass() == Object.class) {
//                return method.invoke(this, args);
//            }
//            if (method.getDeclaringClass() == mClz && mListener != null) {
//                Object invoke = method.invoke(mListener, args);
//                if (TextUtils.equals(method.getName(), "onSuccess")
//                        || TextUtils.equals(method.getName(), "onFailure")
//                        || TextUtils.equals(method.getName(), "onCancel")) {
//                    finishProcess(mActivityWeakRef.get());
//                    mListener = null;
//                }
//                return invoke;
//            }
//            return null;
//        }
//    }
}
