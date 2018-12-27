package com.zfy.social.core.util;


import android.Manifest;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.model.ShareObj;

import java.io.File;

/**
 * CreateAt : 2017/5/22
 * Describe : ShareObj 辅助类
 *
 * @author chendong
 */
public class ShareObjCheckUtil {

    public static final String TAG = ShareObjCheckUtil.class.getSimpleName();

    // 标题和描述是否合法
    private static void checkTitleSummary(ShareObj obj) {
        if (SocialUtil.isAnyEmpty(obj.getTitle(), obj.getSummary())) {
            throw SocialError.make(SocialError.CODE_PARAM_ERROR, "title or summary is empty ," + obj.toString());
        }
    }

    // 本地图片是否合法
    private static void checkThumbImage(Context context, ShareObj obj) {
        String thumbImagePath = obj.getThumbImagePath();
        if (TextUtils.isEmpty(thumbImagePath)) {
            throw SocialError.make(SocialError.CODE_PARAM_ERROR, "thumbImg is empty ," + obj.toString());
        }
        if (!FileUtil.isExist(thumbImagePath)) {
            throw SocialError.make(SocialError.CODE_PARAM_ERROR, "thumbImg file not exist ," + obj.toString());
        }
        if (!isAppCachePath(context, thumbImagePath)
                && !SocialUtil.hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            throw SocialError.make(SocialError.CODE_STORAGE_READ_ERROR, "permission denied, " + obj.toString());
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(thumbImagePath, options);
        if (options.outWidth <= 0) {
            throw SocialError.make(SocialError.CODE_PARAM_ERROR, "thumbImg file error ," + obj.toString());
        }
    }

    // 是否是 App 内部路径
    private static boolean isAppCachePath(Context context, String fullPath) {
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            if (fullPath.contains(externalCacheDir.getAbsolutePath())) {
                return true;
            }
        }
        File cacheDir = context.getCacheDir();
        if (fullPath.contains(cacheDir.getAbsolutePath())) {
            return true;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            File codeCacheDir = context.getCodeCacheDir();
            if (fullPath.contains(codeCacheDir.getAbsolutePath())) {
                return true;
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            File dataDir = context.getDataDir();
            if (fullPath.contains(dataDir.getAbsolutePath())) {
                return true;
            }
        }
        File filesDir = context.getFilesDir();
        if (fullPath.contains(filesDir.getAbsolutePath())) {
            return true;
        }
        return false;
    }

    // 检查 url 是否合法
    private static void checkHttpUrl(String url, ShareObj obj) {
        if (TextUtils.isEmpty(url)) {
            throw SocialError.make(SocialError.CODE_PARAM_ERROR, "targetUrl is empty ," + obj.toString());
        }
        if (!url.toLowerCase().startsWith("http")) {
            throw SocialError.make(SocialError.CODE_PARAM_ERROR, "targetUrl no http schema ," + obj.toString());
        }
    }

    public static void checkShareObjParams(Context context, @Target.ShareTarget int shareTarget, ShareObj obj) {
        switch (obj.getType()) {
            case ShareObj.SHARE_TYPE_OPEN_APP:
                break;
            case ShareObj.SHARE_TYPE_TEXT:
                checkTitleSummary(obj);
                break;
            case ShareObj.SHARE_TYPE_IMAGE:
                checkThumbImage(context, obj);
                break;
            case ShareObj.SHARE_TYPE_APP:
            case ShareObj.SHARE_TYPE_WEB:
                checkTitleSummary(obj);
                checkHttpUrl(obj.getTargetUrl(), obj);
                checkThumbImage(context, obj);
                break;
            case ShareObj.SHARE_TYPE_MUSIC:
                checkTitleSummary(obj);
                checkHttpUrl(obj.getTargetUrl(), obj);
                checkHttpUrl(obj.getMediaPath(), obj);
                checkThumbImage(context, obj);
                break;
            case ShareObj.SHARE_TYPE_VIDEO:
                // 本地视频，qq空间、微博自己支持
                // qq好友、微信好友、钉钉 使用 intent 支持
                if (FileUtil.isExist(obj.getMediaPath())) {
                    if (SocialUtil.isAnyEq(shareTarget, Target.SHARE_QQ_ZONE, Target.SHARE_WB)) {
                        checkTitleSummary(obj);
                        checkHttpUrl(obj.getTargetUrl(), obj);
                        checkThumbImage(context, obj);
                    }
                } else {
                    checkTitleSummary(obj);
                    checkHttpUrl(obj.getTargetUrl(), obj);
                    checkHttpUrl(obj.getMediaPath(), obj);
                    checkThumbImage(context, obj);
                }
            default:
        }
    }

}
