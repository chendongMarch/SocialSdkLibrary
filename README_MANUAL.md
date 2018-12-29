# SocialSDK

相信你已经看了[使用插件接入的文档](https://github.com/chendongMarch/SocialSdkLibrary)，我们更推荐使用插件的方式接入，那样你不用自己去管理复杂的依赖，只需要更新升级插件即可；

这里也同样提供一种手动接入的方案，从某些方面来说，它更容易理解一些；


> app / build.gradle

```gradle
// 依赖核心库
implementation "com.zfy:social-sdk-core:0.0.5"
// 依赖不同的平台库
implementation "com.zfy:social-sdk-wx:0.0.5"
implementation "com.zfy:social-sdk-dd:0.0.5"
implementation "com.zfy:social-sdk-qq:0.0.5"
implementation "com.zfy:social-sdk-weibo:0.0.5"
```

在代码中初始化：

```java
String qqAppId = getString(R.string.QQ_APP_ID);
String wxAppId = getString(R.string.WX_APP_ID);
String wxSecretKey = getString(R.string.WX_SECRET_KEY);
String wbAppId = getString(R.string.SINA_APP_ID);
String ddAppId = getString(R.string.DD_APP_ID);
SocialOptions options = new SocialOptions.Builder(this)
        // 开启调试
        .debug(true)
        // 添加自定义的 json 解析
        .jsonAdapter(new GsonJsonAdapter())
        // 请求处理类，如果使用了微博的 openApi 分享，这个是必须的
        .requestAdapter(new OkHttpRequestAdapter())
        // 加载缩略图失败时，降级使用资源图
        .failImgRes(R.mipmap.ic_launcher_new)
        // 设置 token 有效期，单位小时，默认 24
        .tokenExpiresHours(12)
        // 分享如果停留在第三放将会返回成功，默认返回失败
        .shareSuccessIfStay(true)
        // 配置钉钉
        .dd(ddAppId)
        // 配置qq
        .qq(qqAppId)
        // 配置wx, 第三个参数是是否只返回 code
        .wx(wxAppId, wxSecretKey, false)
        // 配置wb
        .wb(wbAppId, "http://open.manfenmm.com/bbpp/app/weibo/common.php")
        .build();
// 👮 添加 config 数据，必须
SocialSdk.init(options);
```

其他的内容和主文档一致，只是接入方式和初始化方式有些小差别；