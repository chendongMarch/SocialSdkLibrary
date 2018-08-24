package com.march.socialsdk.common;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.util.SocialLogUtil;

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
            SocialLogUtil.e(tag, "图片压缩失败 -> " + msg);
            onShareListener.onFailure(new SocialError(SocialError.CODE_IMAGE_COMPRESS_ERROR, msg).exception(task.getError()));
        } else {
            onSuccess(task.getResult());
        }
        return null;
    }

    public abstract void onSuccess(byte[] thumbData);
}