package com.zfy.social.core.model;

import android.os.Bundle;
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
    public static final int SHARE_TYPE_OPEN_APP = 0x47; // 打开 app


    // 分享对象的类型
    private int type;
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
    // 新浪分享带不带文字
    private boolean isSinaWithSummary = true;
    // 新浪分享带不带图片
    private boolean isSinaWithPicture = false;
    // 使用本地 intent 打开，分享本地视频用
    private boolean isShareByIntent = false;
    // 附加信息
    private Bundle extra;


    // 小程序专属参数
    private String wxMiniOriginId;
    private int wxMiniType;
    private String wxMiniPagePath;
    private boolean isWxMini;
    // 短信专属参数
    private String smsPhone;
    private String smsBody;
    private boolean isSms;
    // 邮件专属参数
    private String eMailAddress;
    private String eMailSubject;
    private String eMailBody;
    private boolean isEMail;
    // 复制内容
    private String copyContent;
    private boolean isClipboard;

    /**
     * 直接打开对应app
     *
     * @return ShareObj
     */
    public static ShareObj buildOpenAppObj() {
        return new ShareObj(SHARE_TYPE_OPEN_APP);
    }

    /**
     * 分享文字，qq 好友原本不支持，使用intent兼容
     *
     * @param title   标题
     * @param summary 描述
     * @return ShareObj
     */
    public static ShareObj buildTextObj(String title, String summary) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_TEXT);
        shareObj.setTitle(title);
        shareObj.setSummary(summary);
        return shareObj;
    }

    /**
     * 分享图片
     *
     * @param path 图片路径
     * @return ShareObj
     */
    public static ShareObj buildImageObj(String path) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_IMAGE);
        shareObj.setThumbImagePath(path);
        return shareObj;
    }

    /**
     * 分享图片，带描述，qq微信好友会分为两条消息发送
     * @param path 图片路径
     * @param summary 描述
     * @return ShareObj
     */
    public static ShareObj buildImageObj(String path, String summary) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_IMAGE);
        shareObj.setThumbImagePath(path);
        shareObj.setSummary(summary);
        return shareObj;
    }

    /**
     * 应用分享，qq支持，其他平台使用 web 分享兼容
     *
     * @param title          标题
     * @param summary        描述
     * @param thumbImagePath 缩略图
     * @param targetUrl      url
     * @return ShareObj
     */
    public static ShareObj buildAppObj(String title, String summary
            , String thumbImagePath, String targetUrl) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_APP);
        shareObj.init(title, summary, thumbImagePath, targetUrl);
        return shareObj;
    }

    /**
     * 分享web，打开链接
     *
     * @param title          标题
     * @param summary        描述
     * @param thumbImagePath 缩略图
     * @param targetUrl      url
     * @return ShareObj
     */
    public static ShareObj buildWebObj(String title, String summary
            , String thumbImagePath, String targetUrl) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_WEB);
        shareObj.init(title, summary, thumbImagePath, targetUrl);
        return shareObj;
    }


    /**
     * 分享音乐,qq空间不支持，使用web分享
     * @param title          标题
     * @param summary        描述
     * @param thumbImagePath 缩略图
     * @param targetUrl      url
     * @param mediaPath      多媒体地址
     * @param duration       时长
     * @return ShareObj
     */
    public static ShareObj buildMusicObj(String title, String summary
            , String thumbImagePath, String targetUrl, String mediaPath, int duration) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_MUSIC);
        shareObj.init(title, summary, thumbImagePath, targetUrl);
        shareObj.setMediaPath(mediaPath);
        shareObj.setDuration(duration);
        return shareObj;
    }


    /**
     * 分享视频，
     * 本地视频使用 intent 兼容，qq 空间本身支持本地视频发布
     * 支持网络视频
     *
     * @param title          标题
     * @param summary        描述
     * @param thumbImagePath 缩略图
     * @param targetUrl      url
     * @param mediaPath      多媒体地址
     * @param duration       时长
     * @return ShareObj
     */
    public static ShareObj buildVideoObj(String title, String summary
            , String thumbImagePath, String targetUrl, String mediaPath, int duration) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_VIDEO);
        shareObj.init(title, summary, thumbImagePath, targetUrl);
        shareObj.setMediaPath(mediaPath);
        shareObj.setDuration(duration);
        return shareObj;
    }

    /**
     * 本地视频
     *
     * @param title          标题
     * @param summary        描述
     * @param localVideoPath 本地视频地址
     * @return ShareObj
     */
    public static ShareObj buildVideoObj(String title, String summary, String localVideoPath) {
        ShareObj shareObj = new ShareObj(SHARE_TYPE_VIDEO);
        shareObj.setMediaPath(localVideoPath);
        shareObj.setShareByIntent(true);
        shareObj.setTitle(title);
        shareObj.setSummary(summary);
        return shareObj;
    }


    /**
     * 设置小程序分享参数
     *
     * @param wxMiniOriginId // 小程序原始 id
     * @param wxMiniType     // 类型 {@link com.zfy.social.core.common.SocialValues#WX_MINI_TYPE_RELEASE}
     * @param wxMiniPagePath // 页面路径
     */
    public void setWxMiniParams(String wxMiniOriginId, int wxMiniType, String wxMiniPagePath) {
        this.wxMiniOriginId = wxMiniOriginId;
        this.wxMiniType = wxMiniType;
        this.wxMiniPagePath = wxMiniPagePath;
        this.isWxMini = true;
    }

    /**
     * 设置短信分享参数
     *
     * @param smsPhone 手机号
     * @param smsBody  信息内容
     */
    public void setSmsParams(String smsPhone, String smsBody) {
        this.smsBody = smsBody;
        this.smsPhone = smsPhone;
        this.isSms = true;
    }


    /**
     * 设置邮件参数
     *
     * @param eMailAddress 邮件地址
     * @param eMailSubject 设置邮件主题
     * @param eMailBody    邮件内容
     */
    public void setEMailParams(String eMailAddress, String eMailSubject, String eMailBody) {
        this.eMailAddress = eMailAddress;
        this.eMailSubject = eMailSubject;
        this.eMailBody = eMailBody;
        this.isEMail = true;
    }


    /**
     * 设置粘贴板复制
     *
     * @param copyContent 复制
     */
    public void setClipboardParams(String copyContent) {
        this.copyContent = copyContent;
        this.isClipboard = true;
    }


    private ShareObj(int type) {
        this.type = type;
    }

    private void init(String title, String summary, String thumbImagePath, String targetUrl) {
        setTitle(title);
        setSummary(summary);
        setThumbImagePath(thumbImagePath);
        setTargetUrl(targetUrl);
    }


    public boolean hasImg() {
        return type != SHARE_TYPE_OPEN_APP && type != SHARE_TYPE_TEXT;
    }


    public Bundle getExtra() {
        return extra;
    }

    public void setExtra(Bundle extra) {
        this.extra = extra;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getWxMiniOriginId() {
        return wxMiniOriginId;
    }


    public int getWxMiniType() {
        return wxMiniType;
    }


    public String getWxMiniPagePath() {
        return wxMiniPagePath;
    }


    public boolean isWxMini() {
        return isWxMini;
    }


    public String getSmsPhone() {
        return smsPhone;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public boolean isSms() {
        return isSms;
    }

    public String geteMailAddress() {
        return eMailAddress;
    }

    public String geteMailSubject() {
        return eMailSubject;
    }

    public String geteMailBody() {
        return eMailBody;
    }

    public boolean isEMail() {
        return isEMail;
    }

    public String getCopyContent() {
        return copyContent;
    }

    public boolean isClipboard() {
        return isClipboard;
    }

    @Override
    public String toString() {
        return "ShareObj{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", thumbImagePath='" + thumbImagePath + '\'' +
                ", thumbImagePathNet='" + thumbImagePathNet + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", mediaPath='" + mediaPath + '\'' +
                ", duration=" + duration +
                ", isSinaWithSummary=" + isSinaWithSummary +
                ", isSinaWithPicture=" + isSinaWithPicture +
                ", isShareByIntent=" + isShareByIntent +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.thumbImagePath);
        dest.writeString(this.thumbImagePathNet);
        dest.writeString(this.targetUrl);
        dest.writeString(this.mediaPath);
        dest.writeInt(this.duration);
        dest.writeByte(this.isSinaWithSummary ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSinaWithPicture ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShareByIntent ? (byte) 1 : (byte) 0);
        dest.writeBundle(this.extra);
        dest.writeString(this.wxMiniOriginId);
        dest.writeInt(this.wxMiniType);
        dest.writeString(this.wxMiniPagePath);
        dest.writeByte(this.isWxMini ? (byte) 1 : (byte) 0);
        dest.writeString(this.smsPhone);
        dest.writeString(this.smsBody);
        dest.writeByte(this.isSms ? (byte) 1 : (byte) 0);
        dest.writeString(this.eMailAddress);
        dest.writeString(this.eMailSubject);
        dest.writeString(this.eMailBody);
        dest.writeByte(this.isEMail ? (byte) 1 : (byte) 0);
        dest.writeString(this.copyContent);
        dest.writeByte(this.isClipboard ? (byte) 1 : (byte) 0);
    }

    protected ShareObj(Parcel in) {
        this.type = in.readInt();
        this.title = in.readString();
        this.summary = in.readString();
        this.thumbImagePath = in.readString();
        this.thumbImagePathNet = in.readString();
        this.targetUrl = in.readString();
        this.mediaPath = in.readString();
        this.duration = in.readInt();
        this.isSinaWithSummary = in.readByte() != 0;
        this.isSinaWithPicture = in.readByte() != 0;
        this.isShareByIntent = in.readByte() != 0;
        this.extra = in.readBundle();
        this.wxMiniOriginId = in.readString();
        this.wxMiniType = in.readInt();
        this.wxMiniPagePath = in.readString();
        this.isWxMini = in.readByte() != 0;
        this.smsPhone = in.readString();
        this.smsBody = in.readString();
        this.isSms = in.readByte() != 0;
        this.eMailAddress = in.readString();
        this.eMailSubject = in.readString();
        this.eMailBody = in.readString();
        this.isEMail = in.readByte() != 0;
        this.copyContent = in.readString();
        this.isClipboard = in.readByte() != 0;
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
}
