package com.zfy.social.core.listener;

import com.zfy.social.core.model.LoginResult;

/**
 * CreateAt : 2019/5/20
 * Describe :
 *
 * @author chendong
 */
public interface OnLoginStateListener {
    void onState(LoginResult result);
}
