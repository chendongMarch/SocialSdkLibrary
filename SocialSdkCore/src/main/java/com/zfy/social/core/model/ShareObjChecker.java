package com.zfy.social.core.model;


import android.Manifest;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.util.FileUtil;
import com.zfy.social.core.util.SocialUtil;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * CreateAt : 2017/5/22
 * Describe : ShareObj 辅助类
 *
 * @author chendong
 */
public class ShareObjChecker {

    public static final String TAG = ShareObjChecker.class.getSimpleName();

    static class ErrMsgRef extends WeakReference<String> {
        ErrMsgRef(String msg, ShareObj obj) {
            super(msg + " data = " + (obj == null ? "no data" : obj.toString()));
        }
    }

    private static ErrMsgRef sErrMsgRef = new ErrMsgRef("", null);


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
                    if (isAny(shareTarget, Target.SHARE_QQ_ZONE, Target.SHARE_WB)) {
                        checkTitleSummary(obj);
                        checkHttpUrl(obj.getTargetUrl(), obj);
                        checkThumbImage(context, obj);
                        // 微博、本地、视频 需要写存储的权限
                        if (shareTarget == Target.SHARE_WB && !isAppCachePath(context, obj.getMediaPath())) {

                        }
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

    public static boolean checkObjValid(ShareObj obj, int shareTarget) {
        switch (obj.getType()) {
            // 文字分享，title,summary 必须有
            case ShareObj.SHARE_TYPE_TEXT:
            {
                return isTitleSummaryValid(obj);
            }
            // 图片分享，如果是微博的 open api 必须有summary
            case ShareObj.SHARE_TYPE_IMAGE: //
            {
                return isThumbLocalPathValid(obj);
            }
            // app 和 web
            case ShareObj.SHARE_TYPE_APP:
            case ShareObj.SHARE_TYPE_WEB: //
            {
                return isUrlValid(obj)
                        && isTitleSummaryValid(obj)
                        && isThumbLocalPathValid(obj);
            }
            //  music voice
            case ShareObj.SHARE_TYPE_MUSIC: {
                return isMusicVideoVoiceValid(obj) && isNetMedia(obj);
            }
            // video
            case ShareObj.SHARE_TYPE_VIDEO:
            {
                // 本地视频分享，qq空间、微博自己支持，qq好友、微信好友、钉钉 使用 intent 支持
                if (FileUtil.isExist(obj.getMediaPath()) && isAny(shareTarget,Target.SHARE_QQ_ZONE,
                        Target.SHARE_WB,
                        Target.SHARE_QQ_FRIENDS,
                        Target.SHARE_WX_FRIENDS,
                        Target.SHARE_DD)) {
                    return isTitleSummaryValid(obj) && !SocialUtil.isAnyEmpty(obj.getMediaPath());
                }
                // 网络视频
                else if (FileUtil.isHttpPath(obj.getMediaPath())) {
                    return isUrlValid(obj) && isMusicVideoVoiceValid(obj) && isNetMedia(obj);
                } else {
                    sErrMsgRef = new ErrMsgRef("本地不支持或者，不是本地也不是网络 ", obj);
                    return false;
                }
            }
            default:
                return false;
        }
    }

    private static boolean isAny(int shareTarget, int... targets) {
        for (int target : targets) {
            if (target == shareTarget) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTitleSummaryValid(ShareObj obj) {
        boolean valid = !SocialUtil.isAnyEmpty(obj.getTitle(), obj.getSummary());

        return valid;
    }

    // 是否是网络视频
    private static boolean isNetMedia(ShareObj obj) {
        boolean httpPath = FileUtil.isHttpPath(obj.getMediaPath());
        if (!httpPath) {
            sErrMsgRef = new ErrMsgRef("ShareObj mediaPath 需要 网络路径", obj);
        }
        return httpPath;
    }

    // url 合法
    private static boolean isUrlValid(ShareObj obj) {
        String targetUrl = obj.getTargetUrl();
        boolean urlValid = !SocialUtil.isAnyEmpty(targetUrl) && FileUtil.isHttpPath(targetUrl);
        if (!urlValid) {
            sErrMsgRef = new ErrMsgRef("url : " + targetUrl + "  不能为空，且必须带有http协议头", obj);
        }
        return urlValid;
    }

    // 音频视频
    private static boolean isMusicVideoVoiceValid(ShareObj obj) {
        return isTitleSummaryValid(obj) && !SocialUtil.isAnyEmpty(obj.getMediaPath()) && isThumbLocalPathValid(obj);
    }

    // 本地文件存在
    private static boolean isThumbLocalPathValid(ShareObj obj) {
        String thumbImagePath = obj.getThumbImagePath();
        boolean exist = FileUtil.isExist(thumbImagePath);
        boolean picFile = FileUtil.isPicFile(thumbImagePath);
        if (!exist || !picFile) {
            sErrMsgRef = new ErrMsgRef("path : " + thumbImagePath + "  " + (exist ? "" : "文件不存在") + (picFile ? "" : "不是图片文件"), obj);
        }
        return exist && picFile;
    }
}
