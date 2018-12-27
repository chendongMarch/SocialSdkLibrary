package com.babypat;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.zfy.social.config.SocialBuildConfig;
import com.zfy.social.core.SocialOptions;
import com.zfy.social.core.SocialSdk;
import com.zfy.social.qq.QQPlatform;
import com.zfy.social.wb.WbPlatform;
import com.zfy.social.wx.WxPlatform;


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
        // initSocialSDK();
    }

//    private void initSocialSDK() {
//
//        String qqAppId = getString(R.string.QQ_APP_ID);
//        String wxAppId = getString(R.string.WX_APP_ID);
//        String wxSecretKey = getString(R.string.WX_SECRET_KEY);
//        String wbAppId = getString(R.string.SINA_APP_ID);
//        String ddAppId = getString(R.string.DD_APP_ID);
//
//        SocialOptions options = SocialOptions.with(this)
//                // 开启调试
//                .debug(true)
//                // 配置钉钉
//                .dd(ddAppId)
//                // 配置qq
//                .qq(qqAppId)
//                // 配置wx
//                .wx(wxAppId, wxSecretKey)
//                // 配置wb
//                .wb(wbAppId, "http://open.manfenmm.com/bbpp/app/weibo/common.php")
//                // 当缩略图因为各种原因无法获取时，将会使用默认图，避免分享中断
//                .failImgRes(R.mipmap.ic_launcher_new)
//                // 设置 token 有效期，有效期内不会重新获取 token
//                // 默认一天，如下设置为 12 小时
//                // 设置为0，将不会做持久化存储，每次获取最新的
//                .tokenExpiresHours(12 * 60 * 60 * 1000)
//                // 注册平台创建工厂
//                .addPlatform(new QQPlatform.Factory())
//                // .addPlatform(new DDPlatform.Factory())
//                .addPlatform(new WbPlatform.Factory())
//                .addPlatform(new WxPlatform.Factory());
//        // 👮 添加 config 数据，必须
//        SocialSdk.init(options);
//        // 👮 添加自定义的 json 解析，必须，参考 temp 文件夹下的实现
//        SocialSdk.setJsonAdapter(new GsonJsonAdapter());
//        // 👮 请求处理类，如果使用了微博的 openApi 分享，这个是必须的，参考 temp 文件夹下的实现
//        SocialSdk.setRequestAdapter(new OkHttpRequestAdapter());
//    }
}
