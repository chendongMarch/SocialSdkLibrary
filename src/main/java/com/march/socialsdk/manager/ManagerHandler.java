package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.Target;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * CreateAt : 2018/3/1
 * Describe :
 *
 * @author chendong
 */
public class ManagerHandler<Listener> {

    private WeakReference<IPlatform> mPlatformRef;

    private WeakReference<Listener> mListenerRef;

    public ManagerHandler(Listener listener) {
        mListenerRef = new WeakReference<>(listener);
    }

    public @NonNull
    IPlatform newPlatform(Context context, int target) {
        if (SocialSdk.getConfig() == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + " SocialSdk.init() request");
        }
        IPlatform platform = SocialSdk.getPlatform(context, target);
        if (platform == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + "  创建platform失败，请检查参数 " + SocialSdk.getConfig().toString());
        }
        mPlatformRef = new WeakReference<>(platform);
        return platform;
    }

    public IPlatform getPlatform() {
        if (mPlatformRef != null) {
            return mPlatformRef.get();
        }
        return null;
    }

    void finishProcess(Activity activity) {
        if (mPlatformRef != null) {
            if (mPlatformRef.get() != null) {
                mPlatformRef.get().recycle();
            }
            mPlatformRef.clear();
            mPlatformRef = null;
        }
        if (mListenerRef != null) {
            mListenerRef.clear();
            mListenerRef = null;
        }
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }


    @SuppressWarnings("unchecked")
    public Listener wrapListener(final Activity activity, Class<Listener> clz) {
        return (Listener) Proxy.newProxyInstance(clz.getClassLoader(),
                new Class[]{clz},
                new FinishActivityInvocationHandler<>(activity, this, mPlatformRef.get()));
    }


    // 动态代理数据
    static class FinishActivityInvocationHandler<Listener> implements InvocationHandler {

        private WeakReference<Activity> mActivityWeakRef;
        private WeakReference<ManagerHandler> mManagerProxyWeakRef;
        private Listener mListener;

        public FinishActivityInvocationHandler(Activity activity,
                ManagerHandler managerProxy,
                Listener listener) {
            mActivityWeakRef = new WeakReference<>(activity);
            mManagerProxyWeakRef = new WeakReference<>(managerProxy);
            mListener = listener;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }
            if (method.getDeclaringClass() == OnShareListener.class && mListener != null) {
                Object invoke = method.invoke(mListener, args);
                if (TextUtils.equals(method.getName(), "onSuccess")
                        || TextUtils.equals(method.getName(), "onFailure")
                        || TextUtils.equals(method.getName(), "onCancel")) {
                    if (mManagerProxyWeakRef.get() != null) {
                        mManagerProxyWeakRef.get().finishProcess(mActivityWeakRef.get());
                    }
                }
                return invoke;
            }
            return null;
        }
    }

}
