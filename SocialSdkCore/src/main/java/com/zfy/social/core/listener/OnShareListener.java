package com.zfy.social.core.listener;

import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.model.ShareObj;

/**
 * CreateAt : 2016/12/25
 * Describe : 分享监听
 *
 * @author chendong
 */

public interface OnShareListener {

    void onStart(int shareTarget, ShareObj obj);

    /**
     * 准备工作，在子线程执行
     *
     * @param shareTarget 分享目标
     * @param obj         shareMediaObj
     * @return 处理后的 Obj
     * @throws Exception e
     */
    ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Exception;

    void onSuccess(int target);

    void onFailure(SocialError e);

    void onCancel();
}
