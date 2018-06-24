package com.march.socialsdk.platform;

import android.app.Activity;

/**
 * CreateAt : 2018/2/11
 * Describe :
 *
 * @author chendong
 */
public interface PlatformCreator {
    IPlatform create(Activity context, int target);
}
