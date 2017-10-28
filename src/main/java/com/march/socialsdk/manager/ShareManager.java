package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.helper.OtherHelper;
import com.march.socialsdk.helper.PlatformLog;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.uikit.ActionActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    public static final int TARGET_QQ_FRIENDS      = 0x31;// qq好友
    public static final int TARGET_QQ_ZONE         = 0x32;// qq空间
    public static final int TARGET_WECHAT_FRIENDS  = 0x33;// 微信好友
    public static final int TARGET_WECHAT_ZONE     = 0x34;// 微信朋友圈
    public static final int TARGET_WECHAT_FAVORITE = 0x35;// 微信收藏
    public static final int TARGET_SINA            = 0x36;// 新浪微博
    public static final int TARGET_SINA_OPENAPI    = 0x37;// 新浪微博openApi分享，暂不支持

    private static OnShareListener sOnShareListener;

    @IntDef({TARGET_QQ_FRIENDS, TARGET_QQ_ZONE,
                    TARGET_WECHAT_FRIENDS, TARGET_WECHAT_ZONE, TARGET_WECHAT_FAVORITE,
                    TARGET_SINA, TARGET_SINA_OPENAPI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShareTargetType {

    }

    /**
     * 开始分享，供外面调用
     *
     * @param context         context
     * @param shareTarget     分享目标
     * @param shareMediaObj   分享对象
     * @param onShareListener 分享监听
     */
    public static void share(final Context context, @ShareTargetType final int shareTarget,
                             final ShareObj shareMediaObj, final OnShareListener onShareListener) {
        Task.callInBackground(new Callable<ShareObj>() {
            @Override
            public ShareObj call() throws Exception {
                ShareObj temp = null;
                try {
                    temp = onShareListener.onPrepareInBackground(shareTarget, shareMediaObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (temp != null) {
                    return temp;
                } else {
                    return shareMediaObj;
                }
            }
        }).continueWith(new Continuation<ShareObj, Object>() {
            @Override
            public Object then(Task<ShareObj> task) throws Exception {
                if (task.isFaulted() || task.getResult() == null) {
                    if (onShareListener != null) {
                        SocialException exception = new SocialException("onPrepareInBackground error", task.getError());
                        onShareListener.onFailure(exception);
                    }
                    return null;
                }
                doShare(context, shareTarget, task.getResult(), onShareListener);
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }


    // 开始分享
    private static boolean doShare(Context context, @ShareTargetType int shareTarget, ShareObj shareMediaObj, OnShareListener onShareListener) {

        if (!shareMediaObj.isValid(shareTarget)) {
            onShareListener.onFailure(new SocialException(SocialException.CODE_SHARE_OBJ_VALID));
            return true;
        }
        sOnShareListener = onShareListener;
        buildPlatform(context, shareTarget);
        if (!getPlatform().isInstall()) {
            onShareListener.onFailure(new SocialException(SocialException.CODE_NOT_INSTALL));
            return true;
        }
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(KEY_ACTION_TYPE, ACTION_TYPE_SHARE);
        intent.putExtra(KEY_SHARE_MEDIA_OBJ, shareMediaObj);
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
        ShareObj shareMediaObj = intent.getParcelableExtra(KEY_SHARE_MEDIA_OBJ);
        if (actionType != ACTION_TYPE_SHARE)
            return;
        if (shareTarget == INVALID_PARAM) {
            PlatformLog.e(TAG, "shareTargetType无效");
            return;
        }
        if (shareMediaObj == null) {
            PlatformLog.e(TAG, "shareMediaObj == null");
            return;
        }
        if (sOnShareListener == null) {
            PlatformLog.e(TAG, "请设置 OnShareListener");
            return;
        }
        if (getPlatform() == null)
            return;
        getPlatform().initOnShareListener(getOnShareListenerWrap(activity));
        getPlatform().share(activity, shareTarget, shareMediaObj);
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
     * @param context ctx
     * @param phone 手机号
     * @param msg 内容
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
     * @param context ctx
     * @param mailto email
     * @param subject 主题
     * @param msg 内容
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
     * @param context ctx
     * @param target 平台
     * @return 是否成功打开
     */
    public static boolean openApp(Context context, @ShareTargetType int target) {
        String pkgName = null;
        switch (target) {
            case TARGET_QQ_FRIENDS:
            case TARGET_QQ_ZONE:
                pkgName = SocialConstants.QQ_PKG;
                break;
            case TARGET_WECHAT_FRIENDS:
            case TARGET_WECHAT_ZONE:
            case TARGET_WECHAT_FAVORITE:
                pkgName = SocialConstants.WECHAT_PKG;
                break;
            case TARGET_SINA:
            case TARGET_SINA_OPENAPI:
                pkgName = SocialConstants.SINA_PKG;
                break;
        }
        return !TextUtils.isEmpty(pkgName) && OtherHelper.openApp(context, pkgName);
    }
}
