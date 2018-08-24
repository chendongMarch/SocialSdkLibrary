package com.march.socialsdk.exception;

import com.march.socialsdk.util.SocialLogUtil;

/**
 * CreateAt : 2016/12/5
 * Describe : 错误信息
 *
 * @author chendong
 */
public class SocialError extends Exception{

    public static final String TAG = SocialError.class.getSimpleName();

    public static final int CODE_OK = 1; // 成功

    public static final int CODE_COMMON_ERROR         = 101; // 通用错误，未归类
    public static final int CODE_NOT_INSTALL          = 102; // 没有安装应用
    public static final int CODE_VERSION_LOW          = 103; // 版本过低，不支持
    public static final int CODE_SHARE_OBJ_VALID      = 104; // 分享的对象参数有问题
    public static final int CODE_SHARE_BY_INTENT_FAIL = 105; // 使用 Intent 分享失败
    public static final int CODE_STORAGE_READ_ERROR   = 106; // 没有读存储的权限，获取分享缩略图将会失败
    public static final int CODE_STORAGE_WRITE_ERROR  = 107; // 没有写存储的权限，微博分享视频copy操作将会失败
    public static final int CODE_FILE_NOT_FOUND       = 108; // 文件不存在
    public static final int CODE_SDK_ERROR            = 109; // sdk 返回错误
    public static final int CODE_REQUEST_ERROR        = 110; // 网络请求发生错误
    public static final int CODE_CANNOT_OPEN_ERROR    = 111; // 无法启动 app
    public static final int CODE_PARSE_ERROR          = 112; // 数据解析错误
    public static final int CODE_IMAGE_COMPRESS_ERROR = 113; // 图片压缩失败

    private int errorCode = CODE_OK;
    private String errorMsg;
    private Exception mException;


    public SocialError(int errorCode) {
        this.errorCode = errorCode;
        switch (errorCode) {
            case CODE_NOT_INSTALL:
                append("应用未安装");
                break;
            case CODE_VERSION_LOW:
                append("应用版本低,需要更高版本");
                break;
            case CODE_STORAGE_READ_ERROR:
                append("没有获取到读SD卡的权限，这会导致图片缩略图无法获取");
                break;
            case CODE_STORAGE_WRITE_ERROR:
                append("没有获取到写SD卡的权限，这会微博分享本地视频无法使用");
                break;
            case CODE_SDK_ERROR:
                append("SDK 返回的错误信息");
                break;
            case CODE_COMMON_ERROR:
                append("通用其他错误");
                break;
            case CODE_SHARE_OBJ_VALID:
                append("分享的对象数据有问题");
                break;
            case CODE_SHARE_BY_INTENT_FAIL:
                append("使用 intent 分享失败");
                break;
            case CODE_FILE_NOT_FOUND:
                append("没有找到文件");
                break;
            case CODE_REQUEST_ERROR:
                append("网络请求错误");
                break;
            case CODE_CANNOT_OPEN_ERROR:
                append("app 无法唤醒");
                break;
            case CODE_PARSE_ERROR:
                append("数据解析错误");
                break;
            case CODE_IMAGE_COMPRESS_ERROR:
                append("图片压缩错误");
                break;
        }
    }

    public SocialError(int errCode,String message) {
        this.errorMsg = message;
        this.errorCode = errCode;
    }

    public SocialError(int errorCode, Exception exception) {
        this.errorCode = errorCode;
        mException = exception;
    }

    public SocialError exception(Exception ex) {
        this.mException = ex;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void printStackTrace() {
        SocialLogUtil.e(TAG, toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("errCode = ").append(errorCode)
                .append(", errMsg = ").append(errorMsg).append("\n");
        if (mException != null) {
            sb.append("其他错误 : ").append(mException.getMessage());
            mException.printStackTrace();
        }
        return sb.toString();
    }

    public SocialError append(String msg) {
        this.errorMsg = String.valueOf(errorMsg) + " ， " + msg;
        return this;
    }

}
