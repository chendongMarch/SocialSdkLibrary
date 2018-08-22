package com.march.socialsdk.model;


import com.march.socialsdk.platform.Target;
import com.march.socialsdk.util.FileUtil;
import com.march.socialsdk.util.Util;

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

    public static String getErrMsg() {
        return sErrMsgRef.get();
    }

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
                if (FileUtil.isExist(obj.getMediaPath()) && isAny(shareTarget,Target.SHARE_QQ_ZONE,
                        Target.SHARE_WB,
                        Target.SHARE_QQ_FRIENDS,
                        Target.SHARE_WX_FRIENDS,
                        Target.SHARE_DD)) {
                    return isTitleSummaryValid(obj) && !Util.isAnyEmpty(obj.getMediaPath());
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
        boolean valid = !Util.isAnyEmpty(obj.getTitle(), obj.getSummary());
        if (!valid) {
            sErrMsgRef = new ErrMsgRef("title summary 不能空", obj);
        }
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
        boolean urlValid = !Util.isAnyEmpty(targetUrl) && FileUtil.isHttpPath(targetUrl);
        if (!urlValid) {
            sErrMsgRef = new ErrMsgRef("url : " + targetUrl + "  不能为空，且必须带有http协议头", obj);
        }
        return urlValid;
    }

    // 音频视频
    private static boolean isMusicVideoVoiceValid(ShareObj obj) {
        return isTitleSummaryValid(obj) && !Util.isAnyEmpty(obj.getMediaPath()) && isThumbLocalPathValid(obj);
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
