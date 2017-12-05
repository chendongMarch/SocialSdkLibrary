package com.march.socialsdk.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.march.socialsdk.common.SocialConstants;

import java.io.File;
import java.util.List;

/**
 * CreateAt : 28/10/2017
 * Describe : 激活本地分享
 *
 * @author chendong
 */
public class IntentShareHelper {

    public static final int SHARE_REQ_CODE = 0x123;

    public static void shareText(Activity activity, String title, String text, String pkg, String targetActivity) throws Exception {
        Intent sendIntent = new Intent();
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.putExtra(Intent.EXTRA_TITLE, title);
        sendIntent.setType("text/plain");
        activeShare(activity, sendIntent, pkg, targetActivity);
    }

    public static void shareImage(Activity activity, String path, String pkg, String targetActivity) throws Exception {
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        // shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("image/*");
        activeShare(activity, shareIntent, pkg, targetActivity);
    }


    public static void shareVideo(Activity activity, String path, String pkg, String targetActivity) throws Exception {
        //由文件得到uri
        Uri videoUri = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
        // shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("video/*");
        printActivitySupport(activity,shareIntent);
        activeShare(activity, shareIntent, pkg, targetActivity);
    }


    public static void activeShare(Activity activity, Intent sendIntent, String pkg, String targetActivity) throws Exception {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(targetActivity))
            sendIntent.setClassName(pkg, targetActivity);
        try {
            Intent chooserIntent = Intent.createChooser(sendIntent, "请选择");
            if (chooserIntent == null) {
                return;
            }

            activity.startActivityForResult(chooserIntent, SHARE_REQ_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    public static void printActivitySupport(Activity activity,Intent intent){
        List<ResolveInfo> resolveInfos = activity.getPackageManager()
                .queryIntentActivities(intent,PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo resolveInfo : resolveInfos) {
            PlatformLog.e(resolveInfo.activityInfo.packageName + " - " + resolveInfo.activityInfo.name);
        }
    }
}
