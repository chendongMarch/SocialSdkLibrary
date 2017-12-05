package com.march.socialsdk.helper;


import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.Target;

/**
 * CreateAt : 2017/5/22
 * Describe : ShareObj 辅助类
 *
 * @author chendong
 */
public class ShareObjHelper {

    public static final String TAG = ShareObjHelper.class.getSimpleName();

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
                if (shareTarget == Target.SHARE_WB_OPENAPI) {
                    boolean isSummaryValid = !CommonHelper.isAnyEmpty(obj.getSummary());
                    if (!isSummaryValid)
                        PlatformLog.e(TAG, "Sina openApi分享必须有summary");
                    return isThumbLocalPathValid(obj) && isSummaryValid;
                } else {
                    return isThumbLocalPathValid(obj);
                }
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
            case ShareObj.SHARE_TYPE_MUSIC:
            case ShareObj.SHARE_TYPE_VOICE: //
            {
                return isMusicVideoVoiceValid(obj) && isNetMedia(obj);
            }
            // video
            case ShareObj.SHARE_TYPE_VIDEO: //
            {
                if (shareTarget == Target.SHARE_QQ_ZONE) {
                    // qq 空间仅支持本地视频
                    // 网络视频使用 web 兼容,因此本地网络文件都可以
                    // 不需要缩略图文件
                    return isTitleSummaryValid(obj) && !CommonHelper.isAnyEmpty(obj.getMediaPath());
                } else if (obj.isShareByIntent() && (shareTarget == Target.SHARE_WB_NORMAL || shareTarget == Target.SHARE_QQ_FRIENDS || shareTarget == Target.SHARE_WX_FRIENDS)) {
                    // 本地视频分享，支持微博、qq好友、微信好友 intent
                    return FileHelper.isExist(obj.getMediaPath());
                } else {
                    // 必须是网络路径
                    return isUrlValid(obj) && isMusicVideoVoiceValid(obj) && isNetMedia(obj);
                }
            }
            default:
                return false;
        }
    }


    private static boolean isTitleSummaryValid(ShareObj obj) {
        boolean valid = !CommonHelper.isAnyEmpty(obj.getTitle(), obj.getSummary());
        if (!valid) {
            PlatformLog.e("title summary 不能空");
        }
        return valid;
    }

    // 是否是网络视频
    private static boolean isNetMedia(ShareObj obj) {
        boolean httpPath = FileHelper.isHttpPath(obj.getMediaPath());
        if (!httpPath) {
            PlatformLog.e("ShareObj mediaPath 需要 网络路机构");
        }
        return httpPath;
    }

    // url 合法
    private static boolean isUrlValid(ShareObj obj) {
        String targetUrl = obj.getTargetUrl();
        boolean urlValid = !CommonHelper.isAnyEmpty(targetUrl) && FileHelper.isHttpPath(targetUrl);
        if (!urlValid) {
            PlatformLog.e(TAG, "url : " + targetUrl + "  不能为空，且必须带有http协议头");
        }
        return urlValid;
    }

    // 音频视频
    private static boolean isMusicVideoVoiceValid(ShareObj obj) {
        return isTitleSummaryValid(obj) && !CommonHelper.isAnyEmpty(obj.getMediaPath()) && isThumbLocalPathValid(obj);
    }

    // 本地文件存在
    private static boolean isThumbLocalPathValid(ShareObj obj) {
        String thumbImagePath = obj.getThumbImagePath();
        boolean exist = FileHelper.isExist(thumbImagePath);
        boolean picFile = FileHelper.isPicFile(thumbImagePath);
        if (!exist || !picFile) {
            PlatformLog.e(TAG, "path : " + thumbImagePath + "  " + (exist ? "" : "文件不存在") + (picFile ? "" : "不是图片文件"));
        }
        return exist && picFile;
    }

}
