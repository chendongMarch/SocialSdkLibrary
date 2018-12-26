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
    public static final int CODE_SHARE_OBJ_VALID = 104; // 分享的对象参数有问题
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

    private int code = CODE_OK;
    private String msg;
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
                .append(", errMsg = ").append(msg).append("\n");
        if (error != null) {
            sb.append("其他错误 : ").append(error.getMessage());
            error.printStackTrace();
        }
        return sb.toString();
    }

    public SocialError append(String msg) {
        this.msg = String.valueOf(this.msg) + " ， " + msg;
        return this;
    }

}
