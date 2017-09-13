package com.march.socialsdk.listener.impl;

import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareObj;

/**
 * CreateAt : 2017/5/22
 * Describe : 简化版本分享监听
 *
 * @author chendong
 */
public class SimpleShareListener implements OnShareListener {

    @Override
    public void onStart(int shareTarget, ShareObj obj) {
    }

    @Override
    public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Exception {
        return obj;
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(SocialException e) {

    }

    @Override
    public void onCancel() {

    }
}
