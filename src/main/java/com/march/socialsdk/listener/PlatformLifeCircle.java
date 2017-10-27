package com.march.socialsdk.listener;

import android.app.Activity;
import android.content.Intent;

/**
 * CreateAt : 2017/5/20
 * Describe : 平台周期方法
 *
 * @author chendong
 */
public interface PlatformLifeCircle {

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onNewIntent(Activity intent);

    void onResponse(Object resp);

    void recycle();
}