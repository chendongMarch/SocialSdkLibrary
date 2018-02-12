package com.march.socialsdk.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.utils.CommonUtils;
import com.march.socialsdk.utils.FileUtils;
import com.march.socialsdk.utils.LogUtils;
import com.march.socialsdk.utils.ShareObjCheckUtils;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.uikit.ActionActivity;

import java.io.File;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * CreateAt : 2017/5/19
 * Describe : 分享管理类，使用该类进行分享操作
 *
 * @author chendong
 */
public class ShareManager extends BaseManager {

    public static final String TAG = ShareManager.class.getSimpleName();

    private static OnShareListener sOnShareListener;


    /**
     * 开始分享，供外面调用
     *
     * @param context         context
     * @param shareTarget     分享目标
     * @param shareObj        分享对象
     * @param onShareListener 分享监听
     */
    public static void share(final Context context, @Target.ShareTarget final int shareTarget,
            final ShareObj shareObj, final OnShareListener onShareListener) {
        Task.callInBackground(new Callable<ShareObj>() {
            @Override
            public ShareObj call() throws Exception {
                prepareImageInBackground(shareObj);
                ShareObj temp = null;
                try {
                    temp = onShareListener.onPrepareInBackground(shareTarget, shareObj);
                } catch (Exception e) {
                    LogUtils.t(e);
                }
                if (temp != null) {
                    return temp;
                } else {
                    return shareObj;
                }
            }
        }).continueWith(new Continuation<ShareObj, Boolean>() {
            @Override
            public Boolean then(Task<ShareObj> task) throws Exception {
                if (task.isFaulted() || task.getResult() == null) {
                    if (onShareListener != null) {
                        SocialException exception = new SocialException("onPrepareInBackground error", task.getError());
                        onShareListener.onFailure(exception);
                    }
                    return null;
                }
                doShare(context, shareTarget, task.getResult(), onShareListener);
                return true;
            }
        }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Boolean, Boolean>() {
            @Override
            public Boolean then(Task<Boolean> task) throws Exception {
                if (task.isFaulted()) {
                    SocialException exception = new SocialException("ShareManager.share() error", task.getError());
                    onShareListener.onFailure(exception);
                }
                return true;
            }
        });
    }


    // 如果是网络图片先下载
    private static void prepareImageInBackground(ShareObj shareObj) {
        String thumbImagePath = shareObj.getThumbImagePath();
        // 路径不为空 & 是网络路径
        if (!TextUtils.isEmpty(thumbImagePath) && FileUtils.isHttpPath(thumbImagePath)) {
            File file = SocialSdk.getRequestAdapter().getFile(thumbImagePath);
            if (FileUtils.isExist(file)) {
                shareObj.setThumbImagePath(file.getAbsolutePath());
            }
        }
    }


    // 开始分享
    private static boolean doShare(Context context, @Target.ShareTarget int shareTarget, ShareObj shareObj, OnShareListener onShareListener) {
        if (!ShareObjCheckUtils.checkObjValid(shareObj, shareTarget)) {
            onShareListener.onFailure(new SocialException(SocialException.CODE_SHARE_OBJ_VALID));
            return true;
        }

        sOnShareListener = onShareListener;
        buildPlatform(context, shareTarget);
        if (!getCurrentPlatform().isInstall()) {
            onShareListener.onFailure(new SocialException(SocialException.CODE_NOT_INSTALL));
            return true;
        }
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(KEY_ACTION_TYPE, ACTION_TYPE_SHARE);
        intent.putExtra(KEY_SHARE_MEDIA_OBJ, shareObj);
        intent.putExtra(KEY_SHARE_TARGET, shareTarget);
        context.startActivity(intent);
        if (context instanceof Activity)
            ((Activity) context).overridePendingTransition(0, 0);
        return false;
    }


    /**
     * 激活分享
     *
     * @param activity activity
     */
    public static void _actionShare(Activity activity) {
        Intent intent = activity.getIntent();
        int actionType = intent.getIntExtra(KEY_ACTION_TYPE, INVALID_PARAM);
        int shareTarget = intent.getIntExtra(KEY_SHARE_TARGET, INVALID_PARAM);
        ShareObj shareObj = intent.getParcelableExtra(KEY_SHARE_MEDIA_OBJ);
        if (actionType != ACTION_TYPE_SHARE)
            return;
        if (shareTarget == INVALID_PARAM) {
            LogUtils.e(TAG, "shareTargetType无效");
            return;
        }
        if (shareObj == null) {
            LogUtils.e(TAG, "shareObj == null");
            return;
        }
        if (sOnShareListener == null) {
            LogUtils.e(TAG, "请设置 OnShareListener");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            LogUtils.e(TAG, "没有获取到读存储卡的权限，这可能导致某些分享不能进行");
        }

        if (getCurrentPlatform() == null)
            return;
        getCurrentPlatform().initOnShareListener(getOnShareListenerWrap(activity));
        getCurrentPlatform().share(activity, shareTarget, shareObj);
    }

    private static OnShareListener getOnShareListenerWrap(final Activity activity) {
        return new OnShareListener() {
            @Override
            public void onStart(int shareTarget, ShareObj obj) {
                sOnShareListener.onStart(shareTarget, obj);
            }

            @Override
            public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Exception {
                return sOnShareListener.onPrepareInBackground(shareTarget, obj);
            }

            @Override
            public void onSuccess() {
                sOnShareListener.onSuccess();
                finishProcess(activity);
            }

            @Override
            public void onFailure(SocialException e) {
                sOnShareListener.onFailure(e);
                finishProcess(activity);
            }

            @Override
            public void onCancel() {
                sOnShareListener.onCancel();
                finishProcess(activity);
            }
        };
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
    public static boolean openApp(Context context, @Target.ShareTarget int target) {
        String pkgName = null;
        switch (target) {
            case Target.SHARE_QQ_FRIENDS:
            case Target.SHARE_QQ_ZONE:
                pkgName = SocialConstants.QQ_PKG;
                break;
            case Target.SHARE_WX_FRIENDS:
            case Target.SHARE_WX_ZONE:
            case Target.SHARE_WX_FAVORITE:
                pkgName = SocialConstants.WECHAT_PKG;
                break;
            case Target.SHARE_WB_NORMAL:
            case Target.SHARE_WB_OPENAPI:
                pkgName = SocialConstants.SINA_PKG;
                break;
            case Target.SHARE_DD:
                pkgName = SocialConstants.DD_PKG;
                break;
        }
        return !TextUtils.isEmpty(pkgName) && CommonUtils.openApp(context, pkgName);
    }
}
