package com.march.socialsdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * CreateAt : 2016/12/28
 * Describe :  多元化分享的对象，用于app|网页|视频|音频|声音 分享
 *
 * @author chendong
 */

public class ShareObj implements Parcelable {

    public static final String TAG = ShareObj.class.getSimpleName();

    public static final int SHARE_TYPE_TEXT = 0x41; // 分享文字
    public static final int SHARE_TYPE_IMAGE = 0x42; // 分享图片
    public static final int SHARE_TYPE_APP = 0x43; // 分享app
    public static final int SHARE_TYPE_WEB = 0x44; // 分享web
    public static final int SHARE_TYPE_MUSIC = 0x45; // 分享音乐
    public static final int SHARE_TYPE_VIDEO = 0x46; // 分享视频
    public static final int SHARE_OPEN_APP = 0x99; // 打开 app

    // 分享对象的类型
    private int shareObjType;
    // title 标题，如果不设置为app name
    private String title;
    // 概要，描述，desc
    private String summary;
    // 缩略图地址，必传
    private String thumbImagePath;
    private String thumbImagePathNet;
    // 启动url，点击之后指向的url，启动新的网页
    private String targetUrl;
    // 资源url,音视频播放源
    private String mediaPath;
    // 音视频时间
    private int duration = 10;
    // 附加信息
    private Parcelable extraTag;
    // 新浪分享带不带文字
    private boolean isSinaWithSummary = true;
    // 新浪分享带不带图片
    private boolean isSinaWithPicture = false;
    // 使用本地 intent 打开，分享本地视频用
    private boolean isShareByIntent = false;

    // 直接打开对应app
    public static ShareObj buildOpenAppObj() {
        return new ShareObj(SHARE_OPEN_APP);
    }

    // 分享文字，qq 好友原本不支持，使用intent兼容
    public static ShareObj buildTextObj(String title, String summary) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_TEXT);
        shareMediaObj.setTitle(title);
        shareMediaObj.setSummary(summary);
        return shareMediaObj;
    }

    // 分享图片
    public static ShareObj buildImageObj(String path) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_IMAGE);
        shareMediaObj.setThumbImagePath(path);
        return shareMediaObj;
    }

    // 分享图片，带描述，qq微信好友会分为两条消息发送
    public static ShareObj buildImageObj(String path, String summary) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_IMAGE);
        shareMediaObj.setThumbImagePath(path);
        shareMediaObj.setSummary(summary);
        return shareMediaObj;
    }

    // 应用分享，qq支持，其他平台使用 web 分享兼容
    public static ShareObj buildAppObj(String title, String summary
            , String thumbImagePath, String targetUrl) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_APP);
        shareMediaObj.init(title, summary, thumbImagePath, targetUrl);
        return shareMediaObj;
    }

    // 分享web，打开链接
    public static ShareObj buildWebObj(String title, String summary
            , String thumbImagePath, String targetUrl) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_WEB);
        shareMediaObj.init(title, summary, thumbImagePath, targetUrl);
        return shareMediaObj;
    }

    // 分享音乐,qq空间不支持，使用web分享
    public static ShareObj buildMusicObj(String title, String summary
            , String thumbImagePath, String targetUrl, String mediaPath, int duration) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_MUSIC);
        shareMediaObj.init(title, summary, thumbImagePath, targetUrl);
        shareMediaObj.setMediaPath(mediaPath);
        shareMediaObj.setDuration(duration);
        return shareMediaObj;
    }

    // 分享视频，
    // 本地视频使用 intent 兼容，qq 空间本身支持本地视频发布
    // 支持网络视频
    public static ShareObj buildVideoObj(String title, String summary
            , String thumbImagePath, String targetUrl, String mediaPath, int duration) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_VIDEO);
        shareMediaObj.init(title, summary, thumbImagePath, targetUrl);
        shareMediaObj.setMediaPath(mediaPath);
        shareMediaObj.setDuration(duration);
        return shareMediaObj;
    }

    // 本地视频
    public static ShareObj buildVideoObj(String title, String summary, String localVideoPath) {
        ShareObj shareMediaObj = new ShareObj(SHARE_TYPE_VIDEO);
        shareMediaObj.setMediaPath(localVideoPath);
        shareMediaObj.setShareByIntent(true);
        shareMediaObj.setTitle(title);
        shareMediaObj.setSummary(summary);
        return shareMediaObj;
    }

    public ShareObj(int shareObjType) {
        this.shareObjType = shareObjType;
    }

    public void init(String title, String summary, String thumbImagePath, String targetUrl) {
        setTitle(title);
        setSummary(summary);
        setThumbImagePath(thumbImagePath);
        setTargetUrl(targetUrl);
    }

    public <T extends Parcelable> T getExtraTag() {
        return (T) extraTag;
    }

    public void setExtraTag(Parcelable extraTag) {
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
        this.thumbImagePathNet = thumbImagePath;
    }

    public String getTargetUrl() {
        if (TextUtils.isEmpty(targetUrl)) {
            return mediaPath;
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

    public boolean isShareByIntent() {
        return isShareByIntent;
    }

    public void setShareByIntent(boolean shareByIntent) {
        isShareByIntent = shareByIntent;
    }

    public String getMediaPath() {
        if (TextUtils.isEmpty(mediaPath)) {
            return targetUrl;
        }
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
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
        dest.writeString(this.mediaPath);
        dest.writeInt(this.duration);
        dest.writeParcelable(this.extraTag, flags);
        dest.writeByte(this.isSinaWithSummary ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSinaWithPicture ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShareByIntent ? (byte) 1 : (byte) 0);
    }

    protected ShareObj(Parcel in) {
        this.shareObjType = in.readInt();
        this.title = in.readString();
        this.summary = in.readString();
        this.thumbImagePath = in.readString();
        this.targetUrl = in.readString();
        this.mediaPath = in.readString();
        this.duration = in.readInt();
        this.extraTag = in.readParcelable(Parcelable.class.getClassLoader());
        this.isSinaWithSummary = in.readByte() != 0;
        this.isSinaWithPicture = in.readByte() != 0;
        this.isShareByIntent = in.readByte() != 0;
    }

    public static final Creator<ShareObj> CREATOR = new Creator<ShareObj>() {
        @Override
        public ShareObj createFromParcel(Parcel source) {
            return new ShareObj(source);
        }

        @Override
        public ShareObj[] newArray(int size) {
            return new ShareObj[size];
        }
    };

    @Override
    public String toString() {
        return "ShareObj{" +
                "shareObjType=" + shareObjType +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", thumbImagePath='" + thumbImagePath + '\'' +
                ", thumbImagePathNet='" + thumbImagePathNet + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", mediaPath='" + mediaPath + '\'' +
                ", duration=" + duration +
                ", extraTag=" + extraTag +
                ", isSinaWithSummary=" + isSinaWithSummary +
                ", isSinaWithPicture=" + isSinaWithPicture +
                ", isShareByIntent=" + isShareByIntent +
                '}';
    }
}
