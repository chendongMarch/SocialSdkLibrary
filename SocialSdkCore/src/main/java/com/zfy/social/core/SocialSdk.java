package com.zfy.social.core;

import android.app.Application;

/**
 * CreateAt : 2017/5/19
 * Describe : SocialSdk
 *
 * @author chendong
 */
public class SocialSdk {

    public static void init(Application application, SocialOptions opts) {
        _SocialSdk inst = _SocialSdk.getInst();
        inst.init(application, opts);
    }


}
