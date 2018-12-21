package com.zfy.social.core.listener;

import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.model.LoginResult;

/**
 * CreateAt : 2016/12/25
 * Describe : 登陆监听
 *
 * @author chendong
 */
public interface OnLoginListener {

    void onStart();

    void onSuccess(LoginResult loginResult);

    void onCancel();

    void onFailure(SocialError e);
}
