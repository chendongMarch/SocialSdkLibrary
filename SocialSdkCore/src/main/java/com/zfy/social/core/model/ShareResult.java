package com.zfy.social.core.model;

import com.zfy.social.core.exception.SocialError;

/**
 * CreateAt : 2019/5/17
 * Describe :
 *
 * @author chendong
 */
public class ShareResult extends Result {

    public ShareObj shareObj;

    private ShareResult(int state, ShareObj shareObj, int target) {
        super(state, target);
        this.shareObj = shareObj;
    }

    public ShareResult(int state) {
        super(state);
    }

    public static ShareResult startOf(int target, ShareObj obj) {
        return new ShareResult(STATE_START, obj, target);
    }

    public static ShareResult successOf(int target, ShareObj obj) {
        return new ShareResult(STATE_SUCCESS, obj, target);
    }

    public static ShareResult failOf(int target, ShareObj obj, SocialError error) {
        ShareResult result = new ShareResult(STATE_FAIL, obj, target);
        result.error = error;
        return result;
    }

    public static ShareResult failOf(SocialError error) {
        ShareResult result = new ShareResult(STATE_FAIL);
        result.error = error;
        return result;
    }

    public static ShareResult cancelOf(int target, ShareObj obj) {
        return new ShareResult(STATE_CANCEL, obj, target);
    }


    public static ShareResult completeOf(int target, ShareObj obj) {
        return new ShareResult(STATE_COMPLETE, obj, target);
    }

    public static ShareResult stateOf(int state, int target, ShareObj obj) {
        return new ShareResult(state, obj, target);
    }

    public static ShareResult stateOf(int state) {
        return new ShareResult(state);
    }

}
