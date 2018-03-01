package com.march.socialsdk.listener;

import com.march.socialsdk.exception.SocialError;
import com.march.socialsdk.model.LoginResult;

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
