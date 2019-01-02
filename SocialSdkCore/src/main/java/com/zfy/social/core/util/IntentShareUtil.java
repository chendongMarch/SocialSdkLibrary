package com.zfy.social.core.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.common._Consumer;
import com.zfy.social.core.common._Predicate;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnShareListener;
import com.zfy.social.core.model.ShareObj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 28/10/2017
 * Describe : 激活本地分享
 *
 * @author chendong
 */
public class IntentShareUtil {

    public static final String TAG = IntentShareUtil.class.getSimpleName();

    public static final int SHARE_REQ_CODE = 0x123;

    private static boolean shareText(Context context, String title, String text, String pkg, String targetActivity) {
        Intent sendIntent = new Intent();
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.putExtra(Intent.EXTRA_TITLE, title);
        sendIntent.setType("text/plain");
        return activeShare(context, sendIntent, pkg, targetActivity);
    }

    private static boolean shareImage(Context context, String path, String pkg, String targetActivity) {
        //由文件得到uri
        Uri imageUri = SocialUtil.fromFile(context, new File(path));
        Intent shareIntent = new Intent();
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        // shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("image/*");
        return activeShare(context, shareIntent, pkg, targetActivity);
    }


    private static boolean shareVideo(Context activity, String path, String pkg, String targetActivity) {
        //由文件得到uri
        Uri videoUri = SocialUtil.fromFile(activity, new File(path));
        Intent shareIntent = new Intent();
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
        // shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("video/*");
        printActivitySupport(activity, shareIntent);
        return activeShare(activity, shareIntent, pkg, targetActivity);
    }


    public static boolean activeMultiFilterShare(Context context, Intent sendIntent, _Predicate<ResolveInfo> predicate, _Consumer<Intent> consumer) {
        try {
            List<Intent> queryIntents = new ArrayList<>();
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(sendIntent, 0);
            if (resolveInfos == null || resolveInfos.isEmpty()) {
                return false;
            }
            for (ResolveInfo resolveInfo : resolveInfos) {
                if (resolveInfo != null && predicate.test(resolveInfo)) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    consumer.accept(shareIntent);
                    shareIntent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    shareIntent.setPackage(packageName);
                    queryIntents.add(shareIntent);
                }
            }
            if (queryIntents.isEmpty()) {
                return false;
            }
            Intent chooserIntent = Intent.createChooser(queryIntents.remove(0), "请选择");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, queryIntents.toArray(new Intent[0]));
            context.startActivity(chooserIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean activeMultiFilterShare2(Activity context, Intent sendIntent, _Predicate<ResolveInfo> predicate, _Consumer<Intent> consumer) {
        try {
            List<Intent> queryIntents = new ArrayList<>();
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(sendIntent, 0);
            if (resolveInfos == null || resolveInfos.isEmpty()) {
                return false;
            }
            for (ResolveInfo resolveInfo : resolveInfos) {
                if (resolveInfo != null && predicate.test(resolveInfo)) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    consumer.accept(shareIntent);
                    shareIntent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    shareIntent.setPackage(packageName);
                    queryIntents.add(shareIntent);
                }
            }
            if (queryIntents.isEmpty()) {
                return false;
            }
            /// Intent chooserIntent = Intent.createChooser(queryIntents.remove(queryIntents.size() - 1), "请选择");
            /// chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, queryIntents.toArray(new Intent[queryIntents.size()]));
            /// context.startActivity(chooserIntent);
            Intent pickIntent = new Intent();
            pickIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_TITLE, "请选择");
//            pickIntent.putExtra(Intent.EXTRA_INTENT, queryIntents.remove(queryIntents.size() - 1));
            pickIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, queryIntents.toArray(new Intent[queryIntents.size()]));
            // Call StartActivityForResult so we can get the app name selected by the user
            context.startActivityForResult(pickIntent, 100);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    private static boolean activeShare(Context context, Intent sendIntent, String pkg, String targetActivity) {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(targetActivity)) {
            sendIntent.setClassName(pkg, targetActivity);
        }
        try {
            Intent chooserIntent = Intent.createChooser(sendIntent, "请选择");
            if (chooserIntent == null) {
                return false;
            }
            context.startActivity(chooserIntent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void printActivitySupport(Context context, Intent intent) {
        List<ResolveInfo> resolveInfos = context.getPackageManager()
                .queryIntentActivities(intent,PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo resolveInfo : resolveInfos) {
            SocialUtil.e(TAG,resolveInfo.activityInfo.packageName + " - " + resolveInfo.activityInfo.name);
        }
    }

    public static void shareVideo(Context context, ShareObj obj, String pkg, String page, OnShareListener listener,int target) {
        boolean result = IntentShareUtil.shareVideo(context, obj.getMediaPath(), pkg, page);
        if (result) {
            listener.onSuccess(target);
        } else {
            listener.onFailure(SocialError.make(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareVideo by intent" + pkg + "  " + page + " failure"));
        }
    }

    /**
     * 兼容 QQ 多平台分享视频
     *
     * @param context  context
     * @param obj      ShareObj
     * @param target   target
     * @param listener lis
     */
    public static void shareQQVideo(Context context, ShareObj obj, int target, OnShareListener listener) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("video/*");
        Uri videoUri = SocialUtil.fromFile(context, new File(obj.getMediaPath()));
        boolean result = activeMultiFilterShare(context, sendIntent, info -> {
            return SocialValues.QQ_FRIENDS_PAGE.equals(info.activityInfo.name);
        }, intent -> {
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, videoUri);
        });
        if (result) {
            listener.onSuccess(target);
        } else {
            listener.onFailure(SocialError.make(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareText by intent failure"));
        }
    }


    /**
     * 兼容 QQ 多平台分享文字
     *
     * @param context  context
     * @param obj      ShareObj
     * @param target   target
     * @param listener lis
     */
    public static void shareQQText(Activity context, ShareObj obj, int target, OnShareListener listener) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
//        boolean result = activeMultiFilterShare(context, sendIntent, info -> {
//            return SocialValues.QQ_FRIENDS_PAGE.equals(info.activityInfo.name);
//        }, intent -> {
//            intent.putExtra(Intent.EXTRA_TEXT, obj.getTitle());
//            intent.putExtra(Intent.EXTRA_TITLE, obj.getSummary());
//            intent.setType("text/plain");
//        });

        boolean result = activeMultiFilterShare(context, sendIntent, info -> {
            return SocialValues.QQ_FRIENDS_PAGE.equals(info.activityInfo.name);
        }, intent -> {
            intent.putExtra(Intent.EXTRA_TEXT, obj.getTitle());
            intent.putExtra(Intent.EXTRA_TITLE, obj.getSummary());
            intent.setType("text/plain");
        });
        if (result) {
            listener.onSuccess(target);
        } else {
            listener.onFailure(SocialError.make(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareText by intent failure"));
        }
    }

}
