package com.march.socialsdk.exception;

import com.march.socialsdk.helper.PlatformLog;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.UiError;

/**
 * CreateAt : 2016/12/5
 * Describe : 错误信息
 *
 * @author chendong
 */
public class SocialException {

    public static final int CODE_OK              = -1; // 成功
    public static final int CODE_NOT_INSTALL     = 0; // 没有安装应用
    public static final int CODE_VERSION_LOW     = 1; // 版本低
    public static final int CODE_SHARE_OBJ_VALID = 2; // 分享的对象参数有问题
    public static final int CODE_SHARE_BY_INTENT_FAIL = 3; // 使用 Intent 分享失败

    private int errorCode = CODE_OK;
    private String errorMsg;

    private WeiboException mWeiboException;
    private UiError        mUiError;
    private Exception      mException;

    public SocialException(int errorCode) {
        this.errorCode = errorCode;
        switch (errorCode) {
            case CODE_NOT_INSTALL:
                errorMsg = "应用未安装";
                break;
            case CODE_VERSION_LOW:
                errorMsg = "应用版本低,需要更高版本";
                break;
            case CODE_SHARE_OBJ_VALID:
                errorMsg = "分享的对象参数有问题";
                break;
        }
    }

    public SocialException(String message) {
        this.errorMsg = message;
    }

    public SocialException(int errorCode, Exception exception) {
        this.errorCode = errorCode;
        mException = exception;
    }

    public SocialException(String message, WeiboException wbException) {
        this(message);
        this.mWeiboException = wbException;
    }

    public SocialException(String message, UiError uiError) {
        this(message);
        this.mUiError = uiError;
    }

    public SocialException(String message, Exception exception) {
        this(message);
        mException = exception;
    }

    public UiError getUiError() {
        return mUiError;
    }

    public Exception getException() {
        return mException;
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

    public WeiboException getWeiboException() {
        return mWeiboException;
    }

    public static final String TAG = SocialException.class.getSimpleName();

    public void printStackTrace() {
        PlatformLog.e(TAG, toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("errCode = ").append(errorCode)
                .append("   ,errMsg = ").append(errorMsg).append("\n");
        if (mException != null) {
            sb.append("其他错误 : ").append(mWeiboException.getMessage());
            mException.printStackTrace();
        }
        if (mWeiboException != null) {
            sb.append("微博分享出现错误 : ").append(mWeiboException.getMessage());
            mWeiboException.printStackTrace();
        }
        if (mUiError != null) {
            sb.append("qq分享出现错误 : ")
                    .append(mUiError.errorCode).append("\n")
                    .append(mUiError.errorMessage).append("\n")
                    .append(mUiError.errorDetail).append("\n");
        }
        return sb.toString();
    }
}
