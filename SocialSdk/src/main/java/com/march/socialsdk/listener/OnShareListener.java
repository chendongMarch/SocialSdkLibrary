package com.march.socialsdk.listener;

import android.content.Context;

import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.model.ShareMediaObj;

/**
 * CreateAt : 2016/12/25
 * Describe : 分享监听
 *
 * @author chendong
 */

public interface OnShareListener {

    void onStart(int shareTarget, ShareMediaObj obj);

    /**
     * 准备工作，在子线程执行
     * @param shareTarget 分享目标
     * @param obj shareMediaObj
     */
    ShareMediaObj onPrepareInBackground(int shareTarget, ShareMediaObj obj) throws Exception;

    void onSuccess();

    void onFailure(SocialException e);

    void onCancel();
}
