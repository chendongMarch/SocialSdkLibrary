package com.zfy.social.core.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnShareListener;
import com.zfy.social.core.model.ShareObj;

import java.io.File;
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


    private static boolean activeShare(Context context, Intent sendIntent, String pkg, String targetActivity) {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(targetActivity))
            sendIntent.setClassName(pkg, targetActivity);
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

    public static void shareVideo(Context context, ShareObj obj, String pkg, String page, OnShareListener listener) {
        boolean result = IntentShareUtil.shareVideo(context, obj.getMediaPath(), pkg, page);
        if (result) {
            listener.onSuccess();
        } else {
            listener.onFailure(SocialError.make(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareVideo by intent" + pkg + "  " + page + " failure"));
        }
    }

    public static void shareText(Context context, ShareObj obj, String pkg, String page, OnShareListener listener) {
        boolean result = IntentShareUtil.shareText(context, obj.getTitle(), obj.getSummary(), pkg, page);
        if (result) {
            listener.onSuccess();
        } else {
            listener.onFailure(SocialError.make(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareText by intent" + pkg + "  " + page + " failure"));
        }
    }

}
