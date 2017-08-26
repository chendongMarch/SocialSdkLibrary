package com.march.socialsdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.march.socialsdk.helper.OtherHelper;
import com.march.socialsdk.helper.FileHelper;
import com.march.socialsdk.helper.PlatformLog;
import com.march.socialsdk.manager.ShareManager;

/**
 * CreateAt : 2016/12/28
 * Describe :  多元化分享的对象，用于app|网页|视频|音频|声音 分享
 *
 * @author chendong
 */

public class ShareMediaObj implements Parcelable {

    public static final int SHARE_TYPE_TEXT  = 0x41;
    public static final int SHARE_TYPE_IMAGE = 0x42;
    public static final int SHARE_TYPE_APP   = 0x43;
    public static final int SHARE_TYPE_WEB   = 0x44;
    public static final int SHARE_TYPE_MUSIC = 0x45;
    public static final int SHARE_TYPE_VIDEO = 0x46;
    public static final int SHARE_TYPE_VOICE = 0x47;
    public static final int SHARE_OPEN_APP   = 0x99;

    // 分享对象的类型
    private int    shareObjType;
    // title 标题，如果不设置为app name
    private String title;
    // 概要，描述，desc
    private String summary;
    // 缩略图地址，必传
    private String thumbImagePath;
    // 启动url，点击之后指向的url，启动新的网页
    private String targetUrl;
    // 资源url,音视频播放源
    private String mediaUrl;
    // 音视频时间
    private int duration = 10;
    // 附加信息
    private Object extraTag;
    // 新浪分享带不带文字
    private boolean isSinaWithSummary = true;
    // 新浪分享带不带图片
    private boolean isSinaWithPicture = false;

    public static ShareMediaObj buildOpenAppObj() {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_OPEN_APP);
        return shareMediaObj;
    }

    public static ShareMediaObj buildTextObj(String title, String summary) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_TEXT);
        shareMediaObj.setTitle(title);
        shareMediaObj.setSummary(summary);
        return shareMediaObj;
    }

    public static ShareMediaObj buildImageObj(String path) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_IMAGE);
        shareMediaObj.setThumbImagePath(path);
        return shareMediaObj;
    }
    public static ShareMediaObj buildImageObj(String path,String summary) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_IMAGE);
        shareMediaObj.setThumbImagePath(path);
        shareMediaObj.setSummary(summary);
        return shareMediaObj;
    }

    public static ShareMediaObj buildAppObj(String title, String summary
            , String thumbImagePath, String targetUrl) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_APP);
        shareMediaObj.initMediaObj(title, summary, thumbImagePath, targetUrl);
        return shareMediaObj;
    }

    public static ShareMediaObj buildWebObj(String title, String summary
            , String thumbImagePath, String targetUrl) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_WEB);
        shareMediaObj.initMediaObj(title, summary, thumbImagePath, targetUrl);
        return shareMediaObj;
    }

    public static ShareMediaObj buildMusicObj(String title, String summary
            , String thumbImagePath, String targetUrl, String mediaUrl, int duration) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_MUSIC);
        shareMediaObj.initMediaObj(title, summary, thumbImagePath, targetUrl);
        shareMediaObj.setMediaUrl(mediaUrl);
        shareMediaObj.setDuration(duration);
        return shareMediaObj;
    }

    public static ShareMediaObj buildVideoObj(String title, String summary
            , String thumbImagePath, String targetUrl, String mediaUrl, int duration) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_VIDEO);
        shareMediaObj.initMediaObj(title, summary, thumbImagePath, targetUrl);
        shareMediaObj.setMediaUrl(mediaUrl);
        shareMediaObj.setDuration(duration);
        return shareMediaObj;
    }

    public static ShareMediaObj buildVoiceObj(String title, String summary
            , String thumbImagePath, String targetUrl, String mediaUrl, int duration) {
        ShareMediaObj shareMediaObj = new ShareMediaObj(SHARE_TYPE_VOICE);
        shareMediaObj.initMediaObj(title, summary, thumbImagePath, targetUrl);
        shareMediaObj.setMediaUrl(mediaUrl);
        shareMediaObj.setDuration(duration);
        return shareMediaObj;
    }


    public ShareMediaObj(int shareObjType) {
        this.shareObjType = shareObjType;
    }


    public void initMediaObj(String title, String summary, String thumbImagePath, String targetUrl) {
        setTitle(title);
        setSummary(summary);
        setThumbImagePath(thumbImagePath);
        setTargetUrl(targetUrl);
    }

    public boolean isAppOrWebObjValid() {
        return !OtherHelper.isEmpty(title, summary, targetUrl) && isThumbLocalPathValid();
    }

    public boolean isMusicVideoVoiceValid() {
        return !OtherHelper.isEmpty(title, summary, targetUrl, mediaUrl) && isThumbLocalPathValid();
    }

    public static final String TAG = ShareMediaObj.class.getSimpleName();

    public boolean isThumbLocalPathValid() {
        boolean exist = FileHelper.isExist(thumbImagePath);
        boolean picFile = FileHelper.isPicFile(thumbImagePath);
        if (!exist || !picFile)
            PlatformLog.e(TAG, "path : " + thumbImagePath + "  " + (exist ? "" : "文件不存在") + (picFile ? "" : "不是图片文件"));
        return exist && picFile;
    }

    public Object getExtraTag() {
        return extraTag;
    }

    public void setExtraTag(Object extraTag) {
        this.extraTag = extraTag;
    }

    public boolean isSinaWithSummary() {
        return isSinaWithSummary;
    }

    public void setSinaWithSummary(boolean sinaWithSummary) {
        isSinaWithSummary = sinaWithSummary;
    }

    public boolean isSinaWithPicture() {
        return isSinaWithPicture;
    }

    public void setSinaWithPicture(boolean sinaWithPicture) {
        isSinaWithPicture = sinaWithPicture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getThumbImagePath() {
        return thumbImagePath;
    }

    public void setThumbImagePath(String thumbImagePath) {
        this.thumbImagePath = thumbImagePath;
    }

    public String getTargetUrl() {
        if (TextUtils.isEmpty(targetUrl)) {
            return mediaUrl;
        }
        return targetUrl;
    }

    public int getShareObjType() {
        return shareObjType;
    }

    public void setShareObjType(int shareObjType) {
        this.shareObjType = shareObjType;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMediaUrl() {
        if (TextUtils.isEmpty(mediaUrl)) {
            return targetUrl;
        }
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }


    public boolean isValid(int shareTarget) {
        switch (shareObjType) {
            case ShareMediaObj.SHARE_TYPE_TEXT:
                return !OtherHelper.isEmpty(title, summary);
            case ShareMediaObj.SHARE_TYPE_IMAGE:
                if (shareTarget == ShareManager.TARGET_SINA_OPENAPI) {
                    boolean isSummaryValid = !OtherHelper.isEmpty(summary);
                    if (!isSummaryValid)
                        PlatformLog.e(TAG, "Sina openApi分享必须有summary");
                    return isThumbLocalPathValid() && isSummaryValid;
                } else
                    return isThumbLocalPathValid();
            case ShareMediaObj.SHARE_TYPE_APP:
            case ShareMediaObj.SHARE_TYPE_WEB:
                return isAppOrWebObjValid();
            case ShareMediaObj.SHARE_TYPE_MUSIC:
            case ShareMediaObj.SHARE_TYPE_VIDEO:
            case ShareMediaObj.SHARE_TYPE_VOICE:
                boolean musicVideoVoiceValid = isMusicVideoVoiceValid();
                if (shareTarget == ShareManager.TARGET_QQ_ZONE) {
                    return musicVideoVoiceValid;
                } else
                    return musicVideoVoiceValid && FileHelper.isHttpPath(mediaUrl);
            default:
                return true;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.shareObjType);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.thumbImagePath);
        dest.writeString(this.targetUrl);
        dest.writeString(this.mediaUrl);
        dest.writeInt(this.duration);
        dest.writeByte(this.isSinaWithSummary ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSinaWithPicture ? (byte) 1 : (byte) 0);
    }

    protected ShareMediaObj(Parcel in) {
        this.shareObjType = in.readInt();
        this.title = in.readString();
        this.summary = in.readString();
        this.thumbImagePath = in.readString();
        this.targetUrl = in.readString();
        this.mediaUrl = in.readString();
        this.duration = in.readInt();
        this.isSinaWithSummary = in.readByte() != 0;
        this.isSinaWithPicture = in.readByte() != 0;
    }

    public static final Creator<ShareMediaObj> CREATOR = new Creator<ShareMediaObj>() {
        @Override
        public ShareMediaObj createFromParcel(Parcel source) {
            return new ShareMediaObj(source);
        }

        @Override
        public ShareMediaObj[] newArray(int size) {
            return new ShareMediaObj[size];
        }
    };
}
