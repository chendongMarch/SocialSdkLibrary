package com.zfy.social.core.manager;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnShareStateListener;
import com.zfy.social.core.listener.ShareInterceptor;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.model.ShareResult;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.ShareObjCheckUtil;
import com.zfy.social.core.util.SocialUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import bolts.CancellationTokenSource;
import bolts.Task;

/**
 * CreateAt : 2017/5/19
 * Describe : 分享管理类，使用该类进行分享操作
 *
 * @author chendong
 */
public class ShareManager {

    public static final String TAG = ShareManager.class.getSimpleName();

    private static _InternalMgr sMgr;

    // 分享
    public static void share(
            final Activity activity,
            @Target.ShareTarget final int shareTarget,
            final ShareObj shareObj,
            final OnShareStateListener listener
    ) {
        if (sMgr != null) {
            sMgr.onHostActivityDestroy();
        }
        if (sMgr == null) {
            sMgr = new _InternalMgr();
        }
        sMgr.preShare(activity, shareTarget, shareObj, listener);
    }

    private static void clear() {
        if (sMgr != null) {
            sMgr.onHostActivityDestroy();
        }
    }

    // 开始分享
    static void actionShare(Activity activity) {
        if (sMgr != null) {
            sMgr.postShare(activity);
        }
    }

    // 发起分享的页面被销毁
    static void onUIDestroy() {
        if (sMgr != null) {
            sMgr.onUIDestroy();
        }
    }


    private static class _InternalMgr implements LifecycleObserver {

        private OnShareStateListener stateListener;
        private OnShareListenerWrap shareListener;

        private int currentTarget;
        private ShareObj currentObj;
        private CancellationTokenSource cts;

        private WeakReference<Activity> fakeActivity;
        private WeakReference<Activity> oriAct;

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onHostActivityDestroy() {
            onProcessFinished();
            SocialUtil.e("chendong", "页面销毁，回收资源");
        }

        // 流程结束，回收资源
        private void onProcessFinished() {
            if (cts != null) {
                cts.cancel();
            }

            if (fakeActivity != null) {
                GlobalPlatform.release(fakeActivity.get());
                fakeActivity.clear();
            } else {
                GlobalPlatform.release(null);
            }
            if (oriAct != null) {
                oriAct.clear();
            }
            currentTarget = -1;
            cts = null;
            currentObj = null;
            stateListener = null;
            if (shareListener != null) {
                shareListener.listener = null;
            }
            shareListener = null;
            fakeActivity = null;
            SocialUtil.e("chendong", "分享过程结束，回收资源");
        }


        /**
         * 开始分享，供外面调用
         *
         * @param act    发起分享的 activity
         * @param shareTarget 分享目标
         * @param shareObj    分享对象
         * @param listener    分享监听
         */
        private void preShare(
                final Activity act,
                @Target.ShareTarget final int shareTarget,
                final ShareObj shareObj,
                final OnShareStateListener listener
        ) {

            if (act instanceof LifecycleOwner) {
                Lifecycle lifecycle = ((LifecycleOwner) act).getLifecycle();
                if (lifecycle != null) {
                    lifecycle.addObserver(this);
                }
            }

            listener.onState(act, ShareResult.startOf(shareTarget, currentObj));

            if (cts != null) {
                cts.cancel();
            }
            cts = new CancellationTokenSource();
            if (act instanceof LifecycleOwner) {
                ((LifecycleOwner) act).getLifecycle().addObserver(this);
            }
            oriAct = new WeakReference<>(act);
            currentObj = shareObj;
            currentTarget = -1;
            Task.callInBackground(() -> {
                currentObj = execInterceptors(act, currentTarget, currentObj);
                return currentObj;
            }, cts.getToken()).continueWith(task -> {
                if (task.isFaulted()) {
                    throw task.getError();
                }
                if (task.getResult() == null) {
                    throw SocialError.make(SocialError.CODE_COMMON_ERROR, "ShareManager#preShare Result is Null");
                }
                preDoShare(act, shareTarget, task.getResult(), listener);
                return true;
            }, Task.UI_THREAD_EXECUTOR).continueWith(task -> {
                if (task.isFaulted()) {
                    SocialError error;
                    Exception exception = task.getError();
                    if (exception instanceof SocialError) {
                        error = (SocialError) exception;
                    } else {
                        error = SocialError.make(SocialError.CODE_COMMON_ERROR, "ShareManager#preShare() error", exception);
                    }
                    listener.onState(oriAct.get(), ShareResult.failOf(shareTarget, currentObj, error));
                }
                return true;
            });
        }

        /**
         * 准备完成数据，开始分享
         *
         * @param activity        发起分享的 activity
         * @param shareTarget     目标
         * @param shareObj        分享对象
         * @param onShareListener 分享回调
         */
        private void preDoShare(
                Activity activity,
                @Target.ShareTarget int shareTarget,
                ShareObj shareObj,
                OnShareStateListener onShareListener
        ) {

            stateListener = onShareListener;
            try {
                ShareObjCheckUtil.checkShareObjParams(activity, shareTarget, shareObj);
            } catch (Exception e) {
                e.printStackTrace();
                SocialError error = (e instanceof SocialError)
                        ? (SocialError) e
                        : SocialError.make(SocialError.CODE_COMMON_ERROR, "ShareManager#preDoShare check obj", e);
                stateListener.onState(oriAct.get(), ShareResult.failOf(shareTarget, shareObj, error));
                return;
            }

            IPlatform platform = GlobalPlatform.newPlatformByTarget(activity, shareTarget);
            if (!platform.isInstall(activity)) {
                stateListener.onState(oriAct.get(), ShareResult.failOf(shareTarget, shareObj, SocialError.make(SocialError.CODE_NOT_INSTALL)));
                return;
            }
            if (platform.getUIKitClazz() == null) {
                shareListener = new OnShareListenerWrap(stateListener);
                platform.initOnShareListener(shareListener);
                platform.share(activity, shareTarget, shareObj);
            } else {
                GlobalPlatform.savePlatform(platform);
                currentTarget = shareTarget;
                Intent intent = new Intent(activity, platform.getUIKitClazz());
                intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_SHARE);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        /**
         * 激活分享，由透明 Activity 真正的激活分享
         *
         * @param act 透明 activity
         */
        private void postShare(Activity act) {
            stateListener.onState(oriAct.get(), ShareResult.stateOf(LoginResult.STATE_ACTIVE, currentTarget, currentObj));
            fakeActivity = new WeakReference<>(act);
            if (currentTarget == -1) {
                SocialUtil.e(TAG, "shareTarget Type 无效");
                stateListener.onState(act,
                        ShareResult.failOf(currentTarget,
                                currentObj,
                                SocialError.make(SocialError.CODE_COMMON_ERROR, "share target error")));
                return;
            }
            if (currentObj == null) {
                stateListener.onState(act,
                        ShareResult.failOf(currentTarget,
                                null,
                                SocialError.make(SocialError.CODE_COMMON_ERROR, "share object error")));
                return;
            }
            if (stateListener == null) {
                stateListener.onState(act,
                        ShareResult.failOf(currentTarget,
                                currentObj,
                                SocialError.make(SocialError.CODE_COMMON_ERROR, "没有设置 share listener")));
                return;
            }
            if (GlobalPlatform.getCurrentPlatform() == null) {
                stateListener.onState(act,
                        ShareResult.failOf(currentTarget,
                                currentObj,
                                SocialError.make(SocialError.CODE_COMMON_ERROR, "创建的 platform 失效")));
                return;
            }
            shareListener = new OnShareListenerWrap(stateListener);
            GlobalPlatform.getCurrentPlatform().initOnShareListener(shareListener);
            GlobalPlatform.getCurrentPlatform().share(act, currentTarget, currentObj);
        }

        private void onUIDestroy() {
            if (currentTarget != -1 && stateListener != null) {
                if (SocialSdk.opts().isShareSuccessIfStay()) {
                    stateListener.onState(oriAct.get(), ShareResult.successOf(currentTarget, currentObj));
                } else {
                    stateListener.onState(oriAct.get(), ShareResult.failOf(currentTarget, currentObj, SocialError.make(SocialError.CODE_STAY_OTHER_APP)));
                }
            }
        }

        private ShareObj execInterceptors(Context context, int target, ShareObj obj) {
            ShareObj result = obj;
            List<ShareInterceptor> interceptors = SocialSdk.getShareInterceptors();
            if (interceptors != null && interceptors.size() > 0) {
                for (ShareInterceptor interceptor : interceptors) {
                    ShareObj temp = interceptor.intercept(context, target, result);
                    if (temp != null) {
                        result = temp;
                    }
                }
            }
            return result;
        }
    }

    public static class ImgInterceptor implements ShareInterceptor {
        @Override
        public ShareObj intercept(Context context, int target, ShareObj obj) {
            if (target == Target.SHARE_CLIPBOARD || target == Target.SHARE_SMS || target == Target.SHARE_EMAIL) {
                return obj;
            }
            String thumbImagePath = obj.getThumbImagePath();
            boolean imgFail = false;
            if (TextUtils.isEmpty(obj.getThumbImagePath())) {
                // 路径为空
                imgFail = true;
            } else if (FileUtil.isHttpPath(obj.getThumbImagePath())) {
                // 路径不为空并且是网络路径
                File file = SocialSdk.getRequestAdapter().getFile(thumbImagePath);
                if (FileUtil.isExist(file)) {
                    obj.setThumbImagePath(file.getAbsolutePath());
                }
            }
            // 再次校验路径合法
            if (TextUtils.isEmpty(obj.getThumbImagePath())) {
                imgFail = true;
            }
            if (imgFail && SocialSdk.opts().getFailImgRes() > 0) {
                String localPath = FileUtil.mapResId2LocalPath(context, SocialSdk.opts().getFailImgRes());
                if (FileUtil.isExist(localPath)) {
                    obj.setThumbImagePath(localPath);
                }
            }
            return obj;
        }
    }


    // 用于分享结束后，回收资源
    private static class OnShareListenerWrap implements OnShareStateListener {

        private OnShareStateListener listener;

        OnShareListenerWrap(OnShareStateListener listener) {
            this.listener = listener;
        }

        private Activity getAct() {
            if (sMgr != null && sMgr.oriAct != null) {
                return sMgr.oriAct.get();
            }
            return null;
        }


        @Override
        public void onState(Activity activity, ShareResult result) {
            if (listener != null) {
                result.target = sMgr.currentTarget;
                result.shareObj = sMgr.currentObj;
                listener.onState(getAct(), result);
            }
            if (result.state == LoginResult.STATE_SUCCESS
                    || result.state == LoginResult.STATE_FAIL
                    || result.state == LoginResult.STATE_CANCEL) {
                if (listener != null) {
                    listener.onState(getAct(), ShareResult.completeOf(sMgr.currentTarget, sMgr.currentObj));
                }
                listener = null;
                clear();
            }
        }
    }

}
