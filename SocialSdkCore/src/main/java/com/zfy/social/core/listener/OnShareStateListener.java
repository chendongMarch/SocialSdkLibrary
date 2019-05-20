package com.zfy.social.core.listener;

import android.app.Activity;

import com.zfy.social.core.model.ShareResult;

/**
 * CreateAt : 2019/5/18
 * Describe :
 *
 * @author chendong
 */
public interface OnShareStateListener {
    void onState(Activity activity, ShareResult result);
}
