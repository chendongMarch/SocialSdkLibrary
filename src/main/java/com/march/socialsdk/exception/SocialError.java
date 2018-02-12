package com.march.socialsdk.exception;

import com.march.socialsdk.utils.LogUtils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.UiError;

/**
 * CreateAt : 2016/12/5
 * Describe : 错误信息
 *
 * @author chendong
 */
public class SocialError {

    public static final String TAG = SocialError.class.getSimpleName();

    public static final int CODE_OK = 1; // 成功

    public static final int CODE_NOT_INSTALL = 0; // 没有安装应用
    public static final int CODE_VERSION_LOW = 1; // 版本低
    public static final int CODE_SHARE_OBJ_VALID = 2; // 分享的对象参数有问题
    public static final int CODE_SHARE_BY_INTENT_FAIL = 3; // 使用 Intent 分享失败

    private int errorCode;
    private String errorMsg;
    private Exception mException;

    public SocialError(int errorCode) {
        this.errorCode = errorCode;
        switch (errorCode) {
            case CODE_NOT_INSTALL:
                errorMsg = "应用未安装";
                break;
            case CODE_VERSION_LOW:
                errorMsg = "应用版本低,需要更高版本";
                break;
            case CODE_SHARE_OBJ_VALID:
                errorMsg = "分享的对象参数有问题，请检查输出的log，会有具体的提示";
                break;
        }
    }

    public SocialError(String message) {
        this.errorMsg = message;
    }

    public SocialError(int errorCode, Exception exception) {
        this.errorCode = errorCode;
        mException = exception;
    }

    public SocialError(String message, Exception exception) {
        this(message);
        mException = exception;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void printStackTrace() {
        LogUtils.e(TAG, toString());
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
        this.errorMsg = String.valueOf(errorMsg) + "   " + msg;
        return this;
    }
}
