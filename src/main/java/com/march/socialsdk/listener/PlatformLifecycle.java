package com.march.socialsdk.listener;

import android.app.Activity;
import android.content.Intent;

/**
 * CreateAt : 2017/5/20
 * Describe : 平台周期方法
 *
 * @author chendong
 */
public interface PlatformLifecycle extends Recyclable{

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void handleIntent(Activity intent);

    void onResponse(Object resp);

}