package com.zfy.social.core.listener;

import android.app.Activity;

import com.zfy.social.core.model.LoginResult;

/**
 * CreateAt : 2019/5/20
 * Describe :
 *
 * @author chendong
 */
public interface OnLoginStateListener {
    void onState(Activity activity, LoginResult result);
}

