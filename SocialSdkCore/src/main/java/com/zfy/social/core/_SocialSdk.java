package com.zfy.social.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.SparseArray;

import com.zfy.social.core.adapter.IJsonAdapter;
import com.zfy.social.core.adapter.IRequestAdapter;
import com.zfy.social.core.adapter.impl.DefaultRequestAdapter;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.ShareInterceptor;
import com.zfy.social.core.manager.ShareManager;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.util.SocialUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CreateAt : 2017/5/19
 * Describe : SocialSdk
 *
 * @author chendong
 */
public class _SocialSdk {

    public static final String TAG = _SocialSdk.class.getSimpleName();

    private static _SocialSdk sInst;

    public static _SocialSdk getInst() {
        if(sInst == null){
            sInst = new _SocialSdk();
        }
        return sInst;
    }

    // 配置项
    private SocialOptions opts;

    private ActivityLifecycleImpl appLifecycle;

    void init(Application application, SocialOptions config) {
        opts = config;
        opts.shareInterceptors.add(0, new ShareManager.ImgInterceptor());
        appLifecycle = new ActivityLifecycleImpl();
        application.registerActivityLifecycleCallbacks(appLifecycle);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            // register platform
            for (String clazz : opts.factoryClassList) {
                registerPlatform(clazz);
            }
            // add
            if (opts.useOkHttp) {
                try {
                    Object instance = Class.forName("com.zfy.social.http.OkHttpRequestAdapter").newInstance();
                    if (instance instanceof IRequestAdapter) {
                        opts.reqAdapter = (IRequestAdapter) instance;
                        SocialUtil.e(TAG, "自动注入 IRequestAdapter");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
            if (opts.useGson) {
                try {
                    Object instance = Class.forName("com.zfy.social.json.GsonJsonAdapter").newInstance();
                    if (instance instanceof IJsonAdapter) {
                        opts.jsonAdapter = (IJsonAdapter) instance;
                        SocialUtil.e(TAG, "自动注入 IJsonAdapter");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 添加 platform
    private void registerPlatform(PlatformFactory factory) {
        PlatformFactory platformFactory = opts.factories.get(factory.getPlatformTarget());
        if (platformFactory != null) {
            opts.factories.remove(factory.getPlatformTarget());
        }
        opts.factories.append(factory.getPlatformTarget(), factory);
    }

    // 添加 platform
    public void registerPlatform(String factoryClazz) {
        try {
            Object instance = Class.forName(factoryClazz).newInstance();
            if (instance instanceof PlatformFactory) {
                PlatformFactory factory = (PlatformFactory) instance;
                registerPlatform(factory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取配置项
    public SocialOptions opts() {
        if (opts == null) {
            throw SocialError.make(SocialError.CODE_SDK_INIT_ERROR);
        }
        return opts;
    }

    // 获取网络请求 adapter
    public IRequestAdapter getRequestAdapter() {
        if (opts.reqAdapter == null) {
            opts.reqAdapter = new DefaultRequestAdapter();
        }
        return opts.reqAdapter;
    }

    // 获取 json 解析 adapter
    public IJsonAdapter getJsonAdapter() {
        return opts.jsonAdapter;
    }

    public SparseArray<PlatformFactory> getPlatformFactories() {
        return opts.factories;
    }

    // 获取顶层 activity
    public Activity getTopActivity() {
        Activity topActivity = appLifecycle.getTopActivity();
        if (topActivity != null) {
        } else {
            throw new IllegalStateException("无法获取 Activity");
        }
        return topActivity;
    }

    public List<ShareInterceptor> getShareInterceptors() {
        return opts.shareInterceptors;
    }


    static class ActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {

        final LinkedList<WeakReference<Activity>> mActivityList = new LinkedList<>();

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            WeakReference<Activity> activityWeakReference = find(activity);
            if (activityWeakReference != null) {
                mActivityList.remove(activityWeakReference);
            }
        }

        WeakReference<Activity> find(Activity activity) {
            Activity weakAct;
            for (WeakReference<Activity> activityWeakReference : mActivityList) {
                weakAct = activityWeakReference.get();
                if (weakAct != null && weakAct.equals(activity)) {
                    return activityWeakReference;
                }
            }
            return null;
        }

        private void setTopActivity(final Activity activity) {
            WeakReference<Activity> activityWeakReference = find(activity);
            if (activityWeakReference == null) {
                mActivityList.addLast(new WeakReference<>(activity));
            } else {
                mActivityList.remove(activityWeakReference);
                mActivityList.addLast(activityWeakReference);
            }
        }

        Activity getTopActivity() {
            if (!mActivityList.isEmpty()) {
                WeakReference<Activity> last = mActivityList.getLast();
                if (last != null && last.get() != null) {
                    return last.get();
                }
            }
            // using reflect to get top activity
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
                Field activitiesField = activityThreadClass.getDeclaredField("mActivityList");
                activitiesField.setAccessible(true);
                Map activities = (Map) activitiesField.get(activityThread);
                if (activities == null) return null;
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        Activity activity = (Activity) activityField.get(activityRecord);
                        setTopActivity(activity);
                        return activity;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
