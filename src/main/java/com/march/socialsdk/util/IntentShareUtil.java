package com.march.socialsdk.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

/**
 * CreateAt : 28/10/2017
 * Describe : 激活本地分享
 *
 * @author chendong
 */
public class IntentShareUtil {

    public static final int SHARE_REQ_CODE = 0x123;

    public static boolean shareText(Activity activity, String title, String text, String pkg, String targetActivity) {
        Intent sendIntent = new Intent();
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.putExtra(Intent.EXTRA_TITLE, title);
        sendIntent.setType("text/plain");
        return activeShare(activity, sendIntent, pkg, targetActivity);
    }

    public static boolean shareImage(Activity activity, String path, String pkg, String targetActivity) {
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        // shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("image/*");
        return activeShare(activity, shareIntent, pkg, targetActivity);
    }


    public static boolean shareVideo(Activity activity, String path, String pkg, String targetActivity) {
        //由文件得到uri
        Uri videoUri = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
        // shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("video/*");
        printActivitySupport(activity, shareIntent);
        return activeShare(activity, shareIntent, pkg, targetActivity);
    }


    private static boolean activeShare(Activity activity, Intent sendIntent, String pkg, String targetActivity) {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(targetActivity))
            sendIntent.setClassName(pkg, targetActivity);
        try {
            Intent chooserIntent = Intent.createChooser(sendIntent, "请选择");
            if (chooserIntent == null) {
                return false;
            }
            activity.startActivityForResult(chooserIntent, SHARE_REQ_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public static void printActivitySupport(Activity activity,Intent intent){
        List<ResolveInfo> resolveInfos = activity.getPackageManager()
                .queryIntentActivities(intent,PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo resolveInfo : resolveInfos) {
            SocialLogUtil.e(resolveInfo.activityInfo.packageName + " - " + resolveInfo.activityInfo.name);
        }
    }
}
