package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
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
public class ManagerProxy<Listener> {

    private IPlatform mPlatform;
    private Listener mListener;

    public IPlatform newPlatform(Context context, int target) {
        if (SocialSdk.getConfig() == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + " SocialSdk.init() request");
        }
        mPlatform = SocialSdk.getPlatform(context, target);
        if (mPlatform == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + "  创建platform失败，请检查参数 " + SocialSdk.getConfig().toString());
        }
        return mPlatform;
    }

    public IPlatform getPlatform() {
        return mPlatform;
    }

    void finishProcess(Activity activity) {
        if (mPlatform != null) {
            mPlatform.recycle();
            mPlatform = null;
        }
        mListener = null;
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }


    @SuppressWarnings("unchecked")
    public Listener getOnShareListenerProxy(final Activity activity, Class<Listener> clz) {
        return (Listener) Proxy.newProxyInstance(clz.getClassLoader(),
                new Class[]{clz},
                new FinishActivityInvocationHandler<>(activity, this, mListener));
    }


    // 动态代理数据
    static class FinishActivityInvocationHandler<Listener> implements InvocationHandler {

        private WeakReference<Activity> mActivityWeakRef;
        private WeakReference<ManagerProxy> mManagerProxyWeakRef;
        private Listener mListener;

        public FinishActivityInvocationHandler(Activity activity,
                ManagerProxy managerProxy,
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
