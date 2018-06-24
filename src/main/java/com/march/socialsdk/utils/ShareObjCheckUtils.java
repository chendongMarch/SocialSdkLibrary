package com.march.socialsdk.utils;


import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.Target;

/**
 * CreateAt : 2017/5/22
 * Describe : ShareObj 辅助类
 *
 * @author chendong
 */
public class ShareObjCheckUtils {

    public static final String TAG = ShareObjCheckUtils.class.getSimpleName();

    public static boolean checkObjValid(ShareObj obj, int shareTarget) {
        switch (obj.getShareObjType()) {
            // 文字分享，title,summary 必须有
            case ShareObj.SHARE_TYPE_TEXT: //
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
                if (FileUtils.isExist(obj.getMediaPath()) && isAny(shareTarget,Target.SHARE_QQ_ZONE,
                        Target.SHARE_WB,
                        Target.SHARE_QQ_FRIENDS,
                        Target.SHARE_WX_FRIENDS,
                        Target.SHARE_DD)) {
                    return isTitleSummaryValid(obj) && !CommonUtils.isAnyEmpty(obj.getMediaPath());
                }
                // 网络视频
                else if (FileUtils.isHttpPath(obj.getMediaPath())) {
                    return isUrlValid(obj) && isMusicVideoVoiceValid(obj) && isNetMedia(obj);
                } else {
                    SocialLogUtils.e("本地不支持或者，不是本地也不是网络 " + obj.toString());
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
        boolean valid = !CommonUtils.isAnyEmpty(obj.getTitle(), obj.getSummary());
        if (!valid) {
            SocialLogUtils.e("title summary 不能空");
        }
        return valid;
    }

    // 是否是网络视频
    private static boolean isNetMedia(ShareObj obj) {
        boolean httpPath = FileUtils.isHttpPath(obj.getMediaPath());
        if (!httpPath) {
            SocialLogUtils.e("ShareObj mediaPath 需要 网络路径");
        }
        return httpPath;
    }

    // url 合法
    private static boolean isUrlValid(ShareObj obj) {
        String targetUrl = obj.getTargetUrl();
        boolean urlValid = !CommonUtils.isAnyEmpty(targetUrl) && FileUtils.isHttpPath(targetUrl);
        if (!urlValid) {
            SocialLogUtils.e(TAG, "url : " + targetUrl + "  不能为空，且必须带有http协议头");
        }
        return urlValid;
    }

    // 音频视频
    private static boolean isMusicVideoVoiceValid(ShareObj obj) {
        return isTitleSummaryValid(obj) && !CommonUtils.isAnyEmpty(obj.getMediaPath()) && isThumbLocalPathValid(obj);
    }

    // 本地文件存在
    private static boolean isThumbLocalPathValid(ShareObj obj) {
        String thumbImagePath = obj.getThumbImagePath();
        boolean exist = FileUtils.isExist(thumbImagePath);
        boolean picFile = FileUtils.isPicFile(thumbImagePath);
        if (!exist || !picFile) {
            SocialLogUtils.e(TAG, "path : " + thumbImagePath + "  " + (exist ? "" : "文件不存在") + (picFile ? "" : "不是图片文件"));
        }
        return exist && picFile;
    }

}
