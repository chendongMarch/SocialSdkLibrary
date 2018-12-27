package com.zfy.social.core.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnShareListener;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.system.SystemPlatform;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.ShareObjCheckUtil;
import com.zfy.social.core.util.SocialUtil;

import java.io.File;
import java.lang.ref.WeakReference;

import bolts.Task;

import static com.zfy.social.core.manager.GlobalPlatform.KEY_ACTION_TYPE;

/**
 * CreateAt : 2017/5/19
 * Describe : 分享管理类，使用该类进行分享操作
 *
 * @author chendong
 */
public class ShareManager {

    public static final String TAG = ShareManager.class.getSimpleName();

    private static OnShareListener sListener;

    /**
     * 开始分享，供外面调用
     *
     * @param activity         context
     * @param shareTarget     分享目标
     * @param shareObj        分享对象
     * @param listener 分享监听
     */
    public static void share(final Activity activity, @Target.ShareTarget final int shareTarget, final ShareObj shareObj, final OnShareListener listener) {
        listener.onStart(shareTarget, shareObj);
        Task.callInBackground(() -> {
            prepareImageInBackground(activity, shareObj);
            ShareObj temp;
            try {
                temp = listener.onPrepareInBackground(shareTarget, shareObj);
            } catch (Exception e) {
                throw SocialError.make(SocialError.CODE_PREPARE_BG_ERROR, "onPrepareInBackground error");
            }
            if (temp != null) {
                return temp;
            } else {
                return shareObj;
            }
        }).continueWith(task -> {
            if (task.isFaulted()) {
                throw task.getError();
            }
            if (task.getResult() == null) {
                throw SocialError.make(SocialError.CODE_COMMON_ERROR, "ShareManager#share Result is Null");
            }
            doShare(activity, shareTarget, task.getResult(), listener);
            return true;
        }, Task.UI_THREAD_EXECUTOR).continueWith(task -> {
            if (task.isFaulted()) {
                SocialError error;
                Exception exception = task.getError();
                if (exception instanceof SocialError) {
                    error = (SocialError) exception;
                } else {
                    error = SocialError.make(SocialError.CODE_COMMON_ERROR, "ShareManager#share() error", exception);
                }
                listener.onFailure(error);
            }
            return true;
        });
    }

    // 如果是网络图片先下载
    private static void prepareImageInBackground(Context context, ShareObj shareObj) {
        String thumbImagePath = shareObj.getThumbImagePath();
        // 图片路径为网络路径，下载为本地图片
        if (!TextUtils.isEmpty(thumbImagePath) && FileUtil.isHttpPath(thumbImagePath)) {
            File file = SocialSdk.getRequestAdapter().getFile(thumbImagePath);
            if (FileUtil.isExist(file)) {
                shareObj.setThumbImagePath(file.getAbsolutePath());
            } else if (SocialSdk.getConfig().getFailImgRes() > 0) {
                String localPath = FileUtil.mapResId2LocalPath(context, SocialSdk.getConfig().getFailImgRes());
                if (FileUtil.isExist(localPath)) {
                    shareObj.setThumbImagePath(localPath);
                }
            }
        }
    }


    // 开始分享
    private static void doShare(Activity activity, @Target.ShareTarget int shareTarget, ShareObj shareObj, OnShareListener onShareListener) {
        try {
            ShareObjCheckUtil.checkShareObjParams(activity, shareTarget, shareObj);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SocialError) {
                onShareListener.onFailure((SocialError) e);
            } else {
                onShareListener.onFailure(SocialError.make(SocialError.CODE_COMMON_ERROR, "ShareManager#doShare check obj", e));
            }
            return;
        }
        sListener = onShareListener;
        IPlatform platform = GlobalPlatform.makePlatform(activity, shareTarget);
        if (!platform.isInstall(activity)) {
            onShareListener.onFailure(SocialError.make(SocialError.CODE_NOT_INSTALL));
            return;
        }
        if (platform instanceof SystemPlatform) {
            platform.initOnShareListener(sListener);
            platform.share(activity, shareTarget, shareObj);
        } else {
            Intent intent = new Intent(activity, platform.getUIKitClazz());
            intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_SHARE);
            intent.putExtra(GlobalPlatform.KEY_SHARE_MEDIA_OBJ, shareObj);
            intent.putExtra(GlobalPlatform.KEY_SHARE_TARGET, shareTarget);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
    }

    /**
     * 激活分享
     *
     * @param activity activity
     */
    static void _actionShare(Activity activity) {
        Intent intent = activity.getIntent();
        int actionType = intent.getIntExtra(KEY_ACTION_TYPE, GlobalPlatform.INVALID_PARAM);
        int shareTarget = intent.getIntExtra(GlobalPlatform.KEY_SHARE_TARGET, GlobalPlatform.INVALID_PARAM);
        ShareObj shareObj = intent.getParcelableExtra(GlobalPlatform.KEY_SHARE_MEDIA_OBJ);
        if (actionType != GlobalPlatform.ACTION_TYPE_SHARE) {
            SocialUtil.e(TAG, "actionType 错误");
            return;
        }
        if (shareTarget == GlobalPlatform.INVALID_PARAM) {
            SocialUtil.e(TAG, "shareTarget Type 无效");
            return;
        }
        if (shareObj == null) {
            SocialUtil.e(TAG, "shareObj == null");
            return;
        }
        if (sListener == null) {
            SocialUtil.e(TAG, "请设置 OnShareListener");
            return;
        }
        if (GlobalPlatform.getPlatform() == null) {
            return;
        }
        GlobalPlatform.getPlatform().initOnShareListener(new OnShareListenerWrap(activity));
        GlobalPlatform.getPlatform().share(activity, shareTarget, shareObj);
    }


    // 用于分享结束后，回收资源
    static class OnShareListenerWrap implements OnShareListener {

        private WeakReference<Activity> mActivityRef;

        OnShareListenerWrap(Activity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void onStart(int shareTarget, ShareObj obj) {
            if (sListener != null) {
                sListener.onStart(shareTarget, obj);
            }
        }

        @Override
        public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Exception {
            if (sListener != null) {
                return sListener.onPrepareInBackground(shareTarget, obj);
            }
            return null;
        }

        private void finish() {
            GlobalPlatform.release(mActivityRef.get());
            sListener = null;
        }

        @Override
        public void onSuccess(int target) {
            if (sListener != null) {
                sListener.onSuccess(target);
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

}
