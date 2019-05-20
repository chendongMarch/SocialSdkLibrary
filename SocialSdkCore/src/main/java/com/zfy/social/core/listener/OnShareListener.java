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

    void onSuccess(int target);

    void onFailure(SocialError e);

    void onCancel();
}
