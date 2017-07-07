package com.march.socialsdk.listener.impl;

import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.model.ShareMediaObj;

/**
 * CreateAt : 2017/5/22
 * Describe :
 *
 * @author chendong
 */
public class SimpleShareListener implements OnShareListener {

    @Override
    public void onStart(int shareTarget, ShareMediaObj obj) {
    }

    @Override
    public ShareMediaObj onPrepareInBackground(int shareTarget, ShareMediaObj obj) throws Exception{
        return null;
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
