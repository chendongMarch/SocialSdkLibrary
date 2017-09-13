package com.march.socialsdk.common;

import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.helper.PlatformLog;
import com.march.socialsdk.listener.OnShareListener;

import bolts.Continuation;
import bolts.Task;

/**
 * CreateAt : 2017/5/20
 * Describe : 压缩图片之后的返回结果
 *
 * @author chendong
 */
public abstract class ThumbDataContinuation implements Continuation<byte[], Object> {

    private String          tag;
    private String          msg;
    private OnShareListener onShareListener;

    protected ThumbDataContinuation(String tag, String msg, OnShareListener onShareListener) {
        this.tag = tag;
        this.msg = msg;
        this.onShareListener = onShareListener;
    }

    @Override
    public Object then(Task<byte[]> task) throws Exception {
        if (task.isFaulted() || task.getResult() == null) {
            PlatformLog.e(tag, "图片压缩失败 -> " + msg);
            onShareListener.onFailure(new SocialException(msg, task.getError()));
        } else {
            onSuccess(task.getResult());
        }
        return null;
    }

    public abstract void onSuccess(byte[] thumbData);
}