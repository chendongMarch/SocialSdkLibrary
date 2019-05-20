package com.zfy.social.core.model;

import com.zfy.social.core.exception.SocialError;

/**
 * CreateAt : 2019/5/17
 * Describe :
 *
 * @author chendong
 */
public class Result {

    public static final int STATE_START = 0;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_FAIL = 3;
    public static final int STATE_CANCEL = 4;

    public int state;
    public int target;
    public SocialError error;

    public Result(int state, int target) {
        this.state = state;
        this.target = target;
    }

}
