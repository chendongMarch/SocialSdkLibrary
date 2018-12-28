package com.zfy.social.core.exception;

import com.zfy.social.core.util.SocialUtil;

/**
 * CreateAt : 2016/12/5
 * Describe : 错误信息
 *
 * @author chendong
 */
public class SocialError extends RuntimeException {

    public static final String TAG = SocialError.class.getSimpleName();

    public static final int CODE_OK = 1; // 成功

    public static final int CODE_COMMON_ERROR = 101; // 通用错误，未归类
    public static final int CODE_NOT_INSTALL = 102; // 没有安装应用
    public static final int CODE_VERSION_LOW = 103; // 版本过低，不支持
    public static final int CODE_SHARE_BY_INTENT_FAIL = 105; // 使用 Intent 分享失败
    public static final int CODE_STORAGE_READ_ERROR = 106; // 没有读存储的权限，获取分享缩略图将会失败
    public static final int CODE_STORAGE_WRITE_ERROR = 107; // 没有写存储的权限，微博分享视频copy操作将会失败
    public static final int CODE_FILE_NOT_FOUND = 108; // 文件不存在
    public static final int CODE_SDK_ERROR = 109; // sdk 返回错误
    public static final int CODE_REQUEST_ERROR = 110; // 网络请求发生错误
    public static final int CODE_CANNOT_OPEN_ERROR = 111; // 无法启动 app
    public static final int CODE_PARSE_ERROR = 112; // 数据解析错误
    public static final int CODE_IMAGE_COMPRESS_ERROR = 113; // 图片压缩失败
    public static final int CODE_PARAM_ERROR = 114; // 参数错误
    public static final int CODE_SDK_INIT_ERROR = 115; // SocialSdk 初始化错误
    public static final int CODE_PREPARE_BG_ERROR = 116; // 执行 prepareOnBackground 时错误
    public static final int CODE_NOT_SUPPORT = 117; // 不支持
    public static final int CODE_STAY_OTHER_APP = 118; // 留在了第三方应用里面

    private int code = CODE_OK;
    private String msg = "";
    private Exception error;


    public static SocialError make(String msg) {
        SocialError error = new SocialError();
        error.code = CODE_COMMON_ERROR;
        error.msg = msg;
        return error;
    }

    public static SocialError make(int code) {
        SocialError error = new SocialError();
        error.code = code;
        return error;
    }

    public static SocialError make(int code, String msg) {
        SocialError error = new SocialError();
        error.code = code;
        error.msg = msg;
        return error;
    }

    public static SocialError make(int code, String msg, Exception exception) {
        SocialError error = new SocialError();
        error.code = code;
        error.msg = msg;
        error.error = exception;
        return error;
    }

    private SocialError() {
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void printStackTrace() {
        SocialUtil.e(TAG, toString());
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("errCode = ").append(code)
                .append(", errMsg = ").append(getMsgByCode()).append("\n");
        if (error != null) {
            sb.append("其他错误 : ").append(error.getMessage());
            error.printStackTrace();
        }
        return sb.toString();
    }

    private String getMsgByCode() {
        StringBuilder sb = new StringBuilder(this.msg).append(", ");
        switch (code) {
            case CODE_COMMON_ERROR:
                sb.append("通用错误，未归类");
                break; //
            case CODE_NOT_INSTALL:
                sb.append("没有安装应用");
                break;
            case CODE_VERSION_LOW:
                sb.append("版本过低，不支持");
                break;
            case CODE_SHARE_BY_INTENT_FAIL:
                sb.append("使用 Intent 分享失败");
                break;
            case CODE_STORAGE_READ_ERROR:
                sb.append("没有读存储的权限，获取分享缩略图将会失败");
                break;
            case CODE_STORAGE_WRITE_ERROR:
                sb.append("没有写存储的权限，微博分享视频copy操作将会失败");
                break;
            case CODE_FILE_NOT_FOUND:
                sb.append("文件不存在");
                break;
            case CODE_SDK_ERROR:
                sb.append("第三方 sdk 返回错误");
                break;
            case CODE_REQUEST_ERROR:
                sb.append("网络请求发生错误");
                break;
            case CODE_CANNOT_OPEN_ERROR:
                sb.append("无法启动 app");
                break;
            case CODE_PARSE_ERROR:
                sb.append("数据解析错误");
                break;
            case CODE_IMAGE_COMPRESS_ERROR:
                sb.append("图片压缩失败");
                break;
            case CODE_PARAM_ERROR:
                sb.append(" 参数错误");
                break;
            case CODE_SDK_INIT_ERROR:
                sb.append("SocialSdk 初始化错误");
                break;
            case CODE_PREPARE_BG_ERROR:
                sb.append("执行 prepareOnBackground 时错误");
                break;
            case CODE_NOT_SUPPORT:
                sb.append("不支持的操作");
                break;
            case CODE_STAY_OTHER_APP:
                sb.append("留在了第三方应用里面");
                break;
        }
        return sb.toString();
    }

    public SocialError append(String msg) {
        this.msg = String.valueOf(this.msg) + " ， " + msg;
        return this;
    }

}
