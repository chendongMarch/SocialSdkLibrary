package com.babypat;

import android.app.Application;
import android.graphics.Color;
import android.widget.Toast;

import com.babypat.adapter.GsonJsonAdapter;
import com.babypat.adapter.OkHttpRequestAdapter;
import com.babypat.platform.HuaweiPlatform;
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
                // 调试模式，开启 log 输出
                .debug(true)
                // 加载缩略图失败时，降级使用资源图
                .failImgRes(R.mipmap.ic_launcher_new)
                // token 保留时间，但是小时，默认不保留
                .tokenExpiresHours(24)
                // 分享如果停留在第三放将会返回成功，默认返回失败
                .shareSuccessIfStay(true)
                // 微博 loading 窗颜色
                .wbProgressColor(Color.YELLOW)
                // 添加自定义的 json 解析
                .jsonAdapter(new GsonJsonAdapter())
                // 请求处理类，如果使用了微博的 openApi 分享，这个是必须的
                .requestAdapter(new OkHttpRequestAdapter())
                // 添加分享拦截器
                .addShareInterceptor((context, r, obj) -> {
                    obj.setSummary("被重新组装" + obj.getSummary());
                    return null;
                })
                .addPlatform(new HuaweiPlatform.Factory())
                // 构建
                .build();
        // 初始化
        SocialSdk.init(application, options);
        // 添加一个自定义平台
        Toast.makeText(this, "初始化成功", Toast.LENGTH_SHORT).show();



    }
}
