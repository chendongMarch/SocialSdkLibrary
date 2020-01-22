package com.babypat;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.zfy.social.core.SocialOptions;
import com.zfy.social.core.SocialSdk;


/**
 * CreateAt : 8/12/17
 * Describe :
 *
 * @author march
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        initSocialSDKSample(this);
    }

    private void initSocialSDKSample(Application application) {
        SocialOptions options = new SocialOptions.Builder(this)
                .debug(true)  // 调试模式，开启 log 输出
                .failImgRes(R.mipmap.ic_launcher_new) // 加载缩略图失败时，降级使用资源图
                // 添加分享拦截器
                .addShareInterceptor((context, r, obj) -> {
                    obj.setSummary("被重新组装" + obj.getSummary());
                    return null;
                })
                // 构建
                .build();
        // 初始化
        SocialSdk.init(application, options);

    }
}
