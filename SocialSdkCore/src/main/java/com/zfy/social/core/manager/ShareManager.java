package com.zfy.social.core.manager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnShareListener;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.model.ShareObjChecker;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.uikit.ActionActivity;
import com.zfy.social.core.util.FileUtil;
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

    static OnShareListener sListener;

    /**
     * 开始分享，供外面调用
     *
     * @param context         context
     * @param shareTarget     分享目标
     * @param shareObj        分享对象
     * @param onShareListener 分享监听
     */
    public static void share(final Activity context, @Target.ShareTarget final int shareTarget,
            final ShareObj shareObj, final OnShareListener onShareListener) {
        onShareListener.onStart(shareTarget, shareObj);
        Task.callInBackground(() -> {
            prepareImageInBackground(context, shareObj);
            ShareObj temp = null;
            try {
                temp = onShareListener.onPrepareInBackground(shareTarget, shareObj);
            } catch (Exception e) {
                SocialUtil.t(TAG, e);
            }
            if (temp != null) {
                return temp;
            } else {
                return shareObj;
            }
        }).continueWith(task -> {
            if (task.isFaulted() || task.getResult() == null) {
                SocialError exception = SocialError.make(SocialError.CODE_COMMON_ERROR, "onPrepareInBackground error", task.getError());
                onShareListener.onFailure(exception);
                return null;
            }
            doShare(context, shareTarget, task.getResult(), onShareListener);
            return true;
        }, Task.UI_THREAD_EXECUTOR).continueWith(task -> {
            if (task.isFaulted()) {
                SocialError exception = SocialError.make(SocialError.CODE_COMMON_ERROR, "ShareManager.share() error", task.getError());
                onShareListener.onFailure(exception);
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
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void doShare(Context context, @Target.ShareTarget int shareTarget, ShareObj shareObj, OnShareListener onShareListener) {
        // 对象是否完整
        if (!ShareObjChecker.checkObjValid(shareObj, shareTarget)) {
            onShareListener.onFailure(SocialError.make(SocialError.CODE_SHARE_OBJ_VALID, ShareObjChecker.getErrMsg()));
            return;
        }
        // 是否有存储权限，读取缩略图片需要存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            onShareListener.onFailure(SocialError.make(SocialError.CODE_STORAGE_READ_ERROR));
            return;
        }
        // 微博、本地、视频 需要写存储的权限
        if (shareTarget == Target.SHARE_WB
                && shareObj.getShareObjType() == ShareObj.SHARE_TYPE_VIDEO
                && !FileUtil.isHttpPath(shareObj.getMediaPath())
                && !SocialUtil.hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            onShareListener.onFailure(SocialError.make(SocialError.CODE_STORAGE_WRITE_ERROR));
            return;
        }
        sListener = onShareListener;
        IPlatform platform = GlobalPlatform.makePlatform(context, shareTarget);
        if (!platform.isInstall(context)) {
            onShareListener.onFailure(SocialError.make(SocialError.CODE_NOT_INSTALL));
            return;
        }
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_SHARE);
        intent.putExtra(GlobalPlatform.KEY_SHARE_MEDIA_OBJ, shareObj);
        intent.putExtra(GlobalPlatform.KEY_SHARE_TARGET, shareTarget);
        context.startActivity(intent);
        if(context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
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
        if (actionType != GlobalPlatform.ACTION_TYPE_SHARE)
            return;
        if (shareTarget == GlobalPlatform.INVALID_PARAM) {
            SocialUtil.e(TAG, "shareTargetType无效");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            SocialUtil.e(TAG, "没有获取到读存储卡的权限，这可能导致某些分享不能进行");
        }
        if (GlobalPlatform.getPlatform() == null)
            return;
        GlobalPlatform.getPlatform().initOnShareListener(new FinishShareListener(activity));
        GlobalPlatform.getPlatform().share(activity, shareTarget, shareObj);
    }

    static class FinishShareListener implements OnShareListener {

        private WeakReference<Activity> mActivityRef;

        FinishShareListener(Activity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void onStart(int shareTarget, ShareObj obj) {
            if (sListener != null) sListener.onStart(shareTarget, obj);
        }

        @Override
        public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Exception {
            if (sListener != null)
                return sListener.onPrepareInBackground(shareTarget, obj);
            return null;
        }

        private void finish() {
            GlobalPlatform.release(mActivityRef.get());
            sListener = null;
        }

        @Override
        public void onSuccess() {
            if (sListener != null) sListener.onSuccess();
            finish();
        }


        @Override
        public void onCancel() {
            if (sListener != null) sListener.onCancel();
            finish();
        }


        @Override
        public void onFailure(SocialError e) {
            if (sListener != null) sListener.onFailure(e);
            finish();
        }
    }


    /**
     * 发送短信分享
     *
     * @param context ctx
     * @param phone   手机号
     * @param msg     内容
     */
    public static void sendSms(Context context, String phone, String msg) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (TextUtils.isEmpty(phone))
            phone = "";
        intent.setData(Uri.parse("smsto:" + phone));
        intent.putExtra("sms_body", msg);
        intent.setType("vnd.android-dir/mms-sms");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发送邮件分享
     *
     * @param context ctx
     * @param mailto  email
     * @param subject 主题
     * @param msg     内容
     */
    public static void sendEmail(Context context, String mailto, String subject, String msg) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        if (TextUtils.isEmpty(mailto))
            mailto = "";
        intent.setData(Uri.parse("mailto:" + mailto));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开平台 app
     *
     * @param context ctx
     * @param target  平台
     * @return 是否成功打开
     */
    public static boolean openApp(Context context, int target) {
        int platform = Target.mapPlatform(target);
        String pkgName = null;
        switch (platform) {
            case Target.SHARE_QQ_FRIENDS:
            case Target.SHARE_QQ_ZONE:
                pkgName = SocialValues.QQ_PKG;
                break;
            case Target.SHARE_WX_FRIENDS:
            case Target.SHARE_WX_ZONE:
            case Target.SHARE_WX_FAVORITE:
                pkgName = SocialValues.WECHAT_PKG;
                break;
            case Target.SHARE_WB:
                pkgName = SocialValues.SINA_PKG;
                break;
            case Target.SHARE_DD:
                pkgName = SocialValues.DD_PKG;
                break;
        }
        return !TextUtils.isEmpty(pkgName) && SocialUtil.openApp(context, pkgName);
    }
}
