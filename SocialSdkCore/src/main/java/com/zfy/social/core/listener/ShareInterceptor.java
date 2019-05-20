package com.zfy.social.core.listener;

import android.content.Context;

import com.zfy.social.core.model.ShareObj;

/**
 * CreateAt : 2019/5/18
 * Describe :
 *
 * @author chendong
 */
public interface ShareInterceptor {

    ShareObj intercept(Context context, ShareObj obj);

}
