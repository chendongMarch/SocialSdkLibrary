
# SocialSDK

![](http://cdn1.showjoy.com/shop/images/20180828/MLI1YQGFQLZBRO3VKH6U1535432744013.png)

> 最新版本(0.1.1)   Easier 、Lighter 、 More Business-Oriented

以更简单、更轻量、更加面向业务需求为设计目标，提供 **微博**、**微信**、**QQ**、**Tim**、**QQ 轻聊版**、**钉钉** 的登陆分享功能支持；

项目地址 : [GitHub - SocialSdkLibrary](https://github.com/chendongMarch/SocialSdkLibrary)

博客地址 ：[快速接入微信微博QQ钉钉原生登录分享](http://zfyx.coding.me/article/3067853428/)

<div style="width:100%;display: flex;align-items:center;height:40px;margin-bottom:20px;">

<img  style="margin-right:20px;height:20px"  src="https://img.shields.io/circleci/project/github/badges/shields/master.svg"/>

<img  style="margin-right:20px;height:20px"  src="https://img.shields.io/badge/version-0.1.1-blue.svg?maxAge=2592000"/>

<img style="margin-right:20px;height:20px"  src="https://img.shields.io/github/stars/chendongMarch/SocialSdkLibrary.svg"/>

<img  style="margin-right:20px;height:20px"  src="https://img.shields.io/github/forks/chendongMarch/SocialSdkLibrary.svg"/>

<img style="margin-right:20px;height:20px" src="https://badge.juejin.im/entry/5a793a405188257a82111092/likes.svg?style=flat-square"/>

</div>

<br/>

🎉  2019.3.28 使用 `gradle plugin` 自动管理依赖，重新设计版本号 [稳定版本 0.1.1](https://github.com/chendongMarch/SocialSdkLibrary/releases/tag/0.1.1) ❤️

🎉  2018.12.27 完成 `gradle` 插件，拆分平台，自动化依赖，一个新台阶 🐶

🎉  2018.12.21 已经225颗 🌟，着手准备拆分成不同平台库，方便灵活接入 ⛽️

🎉  2018.9.26 项目获得了第202颗 🌟，感谢新同事补星 2 个 😄

🎉  2018.6.7 项目获得了第100颗 🌟，最后一颗是我问同事要的 🤦‍

🎉  2018.5.12 修复内存问题、功能扩展 [稳定版本 1.1.0](https://github.com/chendongMarch/SocialSdkLibrary/releases/tag/1.1.0) ❤️

🎉  2018.2.12 支持钉钉分享 🆕

🎉  2017.12.12 对代码进行简单重构并测试  [稳定版本 1.0.0](https://github.com/chendongMarch/SocialSdkLibrary/releases/tag/1.0.0) ❤️


<!--more-->

## 优势

🔥 开源：没有彩蛋，没有彩蛋，没有彩蛋；

🔥 简单：只需要关注登录、分享管理类和一个数据结构对象即可，不需要再关注平台之间的差异；

🔥 轻量：仅包含三方 `SDK` 和一个简单的异步框架(38k)，网络请求、`JSON` 解析从外部注入，减少多余的依赖，保证与宿主项目高度统一；

🔥 面向需求设计：

- Q：微信登录本地只获取 `code`，服务端获取 `token`？
	- A：配置 `wxOnlyAuthCode` 参数;
- Q：又有新平台？华为、OV 联运？
    - A：支持扩展新平台实现，例如华为联运登录接入等；
- Q：每个平台支持的类型不一致，你支持音乐，我支持视频，接入迁移困难？
    - A：封装各平台内部实现，对外暴露统一的接口，若不支持，使用 `web` 分享兼容；
- Q：本地视频无法分享？QQ 不支持纯文字分享？需要兼容方案？
    - A：无法支持的数据类型，使用 `Intent` 唤醒分享，如支持本地视频分享，`qq` 的纯文字分享等等；
- Q：网络图片还得先下载？每次分享前还需要执行下载逻辑？
    - A：支持直接使用网络图片分享，内置自动下载和图片缓存功能；
- Q：万一图片下载不下来怎么办？分享直接失败？
    - A：图片 下载/加载 失败时降级使用资源图，避免分享被中断；
- Q：分享过程中需要完成统一切面操作，打点？加水印？还有啥？
    - A：在分享前可统一重写数据对象，用来做分享的切面，支持图片加水印等类似需求；
- Q：分享增加了复制链接到粘贴板的类型，单独重新写这部分逻辑，那不是不统一了？
    - A：支持短信、邮件、粘贴板等系统分享平台；
- Q：每次都需要打开三方 App 授权登录，能直接登录进去吗？
    - A：持久化存储 `token` 避免多次授权，可选择有效时长；
- Q：我遇到的这些问题，你是不是也遇到了？
    - A：试试 `SocialSdk` 吧；


## 开始接入

**STEP1**: 添加插件依赖路径

> project / build.gradle

```js
buildscript {
    repositories {
        maven { url "https://dl.bintray.com/zfy/maven" }
    }
    dependencies {
        // 请查看文初最新版本，这边可能忘记更新！！！
        classpath 'com.zfy.social:social-sdk-plugin:0.1.1'
    }
}

allprojects {
    repositories {
        maven { url "https://dl.bintray.com/zfy/maven" }
    }
}
```

**STEP2**: 配置参数，注意与 `android` 同级

> project / local.properties

配置你的 `appId` 和 `appKey`;

```
wxAppId = wx4b8xxxb195c3
wxAppSecret = 0a3cxxxxxx654f499171
wxOnlyAuthCode = false

qqAppId = 110xxx0200
qqAppSecret = A6Aqxxx9yQ4N


wbAppId = 2xxx5998
wbUrl = http://open.manfenmm.com/bxx/common.php

ddAppId = dingoxxxefwjeumuof
```

> app / build.gralde

```js
// 引用插件
apply plugin: 'socialsdk'

// android 配置模块
android {
	...
}

// socialsdk 配置模块
Properties prop = getLocalProperties()
socialsdk {
    wx {
        appId = prop.get('wxAppId')
        appSecret = prop.get('wxAppSecret')
        onlyAuthCode = Boolean.parseBoolean(prop.get('wxOnlyAuthCode'))
    }
    qq {
        appId = prop.get('qqAppId')
        appSecret = prop.get('qqAppSecret')
    }
    wb {
        appId = prop.get('wbAppId')
        url = prop.get('wbUrl')
    }
    dd {
        appId = prop.get('ddAppId')
    }
}
```

**STEP3**：初始化

```java
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
        // 添加分享拦截器，重新处理分享的数据
        .addShareInterceptor((context, obj) -> {
            obj.setSummary("被重新组装" + obj.getSummary());
            return obj;
        })
        // 构建
        .build();
// 初始化
SocialSdk.init(options);
// 添加一个自定义平台
SocialSdk.addPlatform(new HuaweiPlatform.Factory());
```

说一下 `Adapter`，项目内使用了 `JSON` 解析，网络请求等功能，但是又不想引入多余的框架，所以才用了宿主项目注入的方式，保证和宿主项目统一。

- `IJsonAdapter`，必须 ！负责完成 `Json` 解析和序列化，提供一个 `Gson` 下的实现仅供参考 - [GsonJsonAdapter.java](https://github.com/chendongMarch/SocialSdkLibrary/blob/master/temp/GsonJsonAdapter.java)；

- `IRequestAdapter`，非必须！内部使用 `UrlConnection` 做了一个默认的实现，负责完成网络请求，也可以使用 `OkHttp` 重新实现，可以参考 - [OkHttpRequestAdapter.java](https://github.com/chendongMarch/SocialSdkLibrary/blob/b0b8559ff26136abbaaee9667bfc5c2bf54eedea/temp/OkHttpRequestAdapter.java)，目前微信的 `OAuth2` 授权和图片下载的相关请求都是使用 `IRequestAdapter` 代理；

## 登录功能

登陆功能支持三个平台，qq，微信，微博；

```java
// 3个平台
Target.LOGIN_QQ;
Target.LOGIN_WX;
Target.LOGIN_WB;
```

登录将会返回 `LoginResult`， 其中主要包括登录类型，基本用户信息，令牌信息 3 部分；

```java
LoginResult {
    // 状态，成功，失败，取消等
    public int state;
    // 目标平台
    public int target;
    // 发生错误时使用
    public SocialError error;
    // 针对登录类型可强转为 WbUser,WxUser,QQUser 来获取更加丰富的信息
    public SocialUser socialUser;
    // 本次登陆的 token 信息，openId, unionId,token,expires_in
    public AccessToken accessToken;
    // 授权码，如果 onlyAuthCode 为 true, 将会返回它
    public String wxAuthCode;
}
```
登录时需要设置登录回调：

```java
new OnLoginStateListener() {
    @Override
    public void onState(LoginResult result) {
        switch (result.state) {
            case LoginResult.STATE_SUCCESS:
                // 登录成功
                break;
            case LoginResult.STATE_FAIL:
                // 登录失败
                break;
            case LoginResult.STATE_CANCEL:
                // 登录取消
                break;
        }
    }
};
```

获取更多用户信息：

```java
SocialUser socialUser = loginResult.socialUser;
// 基本信息可以从 SocialUser 在获取到
String userNickName = socialUser.getUserNickName();
// 获取 openId
String openId = socialUser.getOpenId();
// 微信获取 unionId，其他平台仍旧返回 openId
String unionId = socialUser.getUnionId();
// 获取 userId，微信返回 unionId, 其他平台返回 openId
String userId = socialUser.getUserId();
// 强转为平台用户，可以拿到更多信息
int target = result.getTarget();
switch (target) {
    case Target.LOGIN_QQ:
        QQUser qqUser = (QQUser) socialUser;
        break;
    case Target.LOGIN_WB:
        WbUser wbUser = (WbUser) socialUser;
        break;
    case Target.LOGIN_WX:
        WxUser wxUser = (WxUser) socialUser;
        break;
}
```

发起登录：

```java
LoginManager.login(mActivity, Target.LOGIN_QQ, listener);
```

关于 `token` 时效，可以在初始化时设置 `tokenExpiresHours` 来控制，也同样提供清除授权 `token` 的方法。


```java
// 清除全部平台的 token
LoginManager.clearAllToken(context);
// 清除指定平台的 token
LoginManager.clearToken(context, Target.LOGIN_QQ);
```

## 分享功能


重要：请仔细查看平台和数据类型中间的支持能力

![](http://cdn1.showjoy.com/images/b9/b9ffca33435c40d8b6e33914db0fa6da.png)

当 微博 使用 `openApi` 形式去分享时，可能有较长的延时，建议在生命周期中增加进度条显示，避免用户等待很久没有响应。


### 划分分享数据类型

针对业务逻辑和 `SDK` 设计，将分享数据类型划分为 7 种类型，他们能涵盖大多数业务场景，分别是：

```bash
开启 App 类型，打开渠道应用；
文字类型，纯文本分享；
图片类型(jpg, png, gif(要求能动))；
App 推广类型；
网页链接类型；
音频分享类型；
视频分享类型；
```

为了保证每个平台都有封闭且统一的外观，如果某个平台不兼容某种类型的分享，将会使用 `web` 分享的方式代替；比如微信不支持 `app` 分享，分享出去之后时 `web` 分享的模式。

### 划分分享渠道

```java
// 支持的分享渠道
Target.SHARE_DD; // 钉钉好友
Target.SHARE_QQ_FRIENDS; // qq好友
Target.SHARE_QQ_ZONE; // qq空间
Target.SHARE_WX_FRIENDS; // 微信好友
Target.SHARE_WX_ZONE; // 微信朋友圈
Target.SHARE_WX_FAVORITE; // 微信收藏
Target.SHARE_WB; // 新浪微博
Target.SHARE_SMS; // 短信分享
Target.SHARE_EMAIL; // 邮件分享
Target.SHARE_CLIPBOARD; // 粘贴板分享
```

### 创建分享数据

分享时，我们首先要构造分享用的数据，`ShareObj` 对象提供了多种静态方法用来快速创建对应分享的类型的对象;

```java
// 测试用的路径
localImagePath = new File(Environment.getExternalStorageDirectory(), "1.jpg").getAbsolutePath();
localVideoPath = new File(Environment.getExternalStorageDirectory(), "video.mp4").getAbsolutePath();
localGifPath = new File(Environment.getExternalStorageDirectory(), "3.gif").getAbsolutePath();
netVideoPath = "http://7xtjec.com1.z0.glb.clouddn.com/export.mp4";
netImagePath = "http://7xtjec.com1.z0.glb.clouddn.com/token.png";
netMusicPath = "http://7xtjec.com1.z0.glb.clouddn.com/test_music.mp3";
netMusicPath = "http://mp3.haoduoge.com/test/2017-05-19/1495207225.mp3";
targetUrl = "http://bbs.csdn.net/topics/391545021";


// 打开渠道对应app
ShareObj shareMediaObj = ShareObj.buildOpenAppObj();
// 分享文字
ShareObj textObj = ShareObj.buildTextObj("分享文字", "summary");
// 分享图片
ShareObj imageObj = ShareObj.buildImageObj("分享图片", "summary", localImagePath);
// 分享gif
ShareObj imageGifObj = ShareObj.buildImageObj("分享图片", "summary", localGifPath);
// 分享app
ShareObj appObj = ShareObj.buildAppObj("分享app", "summary", localImagePath, targetUrl);
// 分享web
ShareObj webObj = ShareObj.buildWebObj("分享web", "summary", localImagePath, targetUrl);
// 分享视频
ShareObj videoObj = ShareObj.buildVideoObj("分享视频", "summary", localImagePath, targetUrl, localVideoPath, 10);
// 本地视频分享、部分平台支持
ShareObj videoLocalObj = ShareObj.buildVideoObj("分享本地视频", "summary", localImagePath, targetUrl, localVideoPath, 0);
// 分享音乐
ShareObj musicObj = ShareObj.buildMusicObj("分享音乐", "summary", localImagePath, targetUrl, netMusicPath, 10);
```

针对一些不能被统一的参数使用扩展的参数支持：

```java
// 使 ShareObj 支持短信分享
webObj.setSmsParams("13611301719", "说啥呢");
// 使 ShareObj 支持粘贴板分享
webObj.setClipboardParams("复制的内容");
// 使 ShareObj 支持邮件分享
webObj.setEMailParams("1101873740@qq.com", "主题", "内容");
// 使 ShareObj 在微信平台优先使用小程序分享
webObj.setWxMiniParams("51299u9**q31",SocialValues.WX_MINI_TYPE_RELEASE,"/page/path");
```

### 分享监听

使用 `OnShareStateListener ` 作为监听分享回调；

```java
new OnShareStateListener() {
    @Override
    public void onState(ShareResult result) {
        switch (result.state) {
            case ShareResult.STATE_SUCCESS:
                showMsg("分享成功");
                break;
            case ShareResult.STATE_FAIL:
                SocialError e = result.error;
                showMsg("分享失败  " + e.toString());
                // 如下因为没有存储权限导致失败，请求权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (e.getCode() == SocialError.CODE_STORAGE_READ_ERROR) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else if (e.getCode() == SocialError.CODE_STORAGE_WRITE_ERROR) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    }
                }
                break;
            case ShareResult.STATE_CANCEL:
                showMsg("分享取消");
                break;
        }
    }
};
```

### 发起分享

```java
// 唤醒分享
ShareManager.share(mActivity, Target.SHARE_QQ_FRIENDS, imageObj, mOnShareListener);
```

### 重写分享对象

关于重写分享对象，其实提供一种能在分享之前对需要分享的 `ShareObj` 进行统一处理的机会，类似分享功能的一个切面，比如如下场景：

- 自动下载网络图片到本地，再进行分享，支持网络图分享（已内置）；
- 图片分享出去之前需要加上 `app` 水印；
- 分享的 `url` 带上公共参数 `shareId` 等，在 `H5` 做访问统计；

重写分享对象，我们使用拦截器来实现，拦截器在 `SDK` 初始化时注入，支持多个，可以将不同业务分为不同的拦截器，所有拦截器会被顺序调用；

⚠️ 拦截器会在子线程执行，也就意味着你可以做耗时操作（图片处理，网络请求），需要更改 `UI` 需要到主线程执行；


```java
SocialOptions options = new SocialOptions.Builder(this)
        // ... 其他初始化代码
        // 添加分享拦截器
        .addShareInterceptor((context, target, obj) -> {
            obj.setSummary("描述加前缀" + obj.getSummary());
            return obj;
        })
        .addShareInterceptor((context, target, obj) -> {
            obj.setTargetUrl(obj.getTargetUrl()+"?id=100");
            return obj;
        })
        // 构建
        .build();
// 初始化
SocialSdk.init(options);
```

### 系统平台

我们在做分享时通常会遇到需求，复制到粘贴板/支持短信分享/支持邮件分享等等，`SocialSdk` 内置了这些功能，需要在创建 `ShareObj` 之后添加额外参数来实现；

- 短信分享

```java
shareObj.setSmsParams("13611301719", "说啥呢");
```

- 邮件分享

```java
shareObj.setEMailParams("1101873740@qq.com", "主题", "内容");
```
- 复制链接

```java
shareObj.setClipboardParams("复制的内容");
```

### 微信小程序分享

支持微信小程序分享，也同样使用额外参数的形式

```java
shareObj.setWxMiniParams("51299u9**q31",SocialValues.WX_MINI_TYPE_RELEASE,"/page/path");
```
## 错误码

为了更好的统一分享失败时返回的异常，返回的所有异常都会有一个 `code`，可以根据不同的 `code` 定位问题和给出更友好的提示。


```java
int CODE_COMMON_ERROR = 101; // 通用错误，未归类
int CODE_NOT_INSTALL = 102; // 没有安装应用
int CODE_VERSION_LOW = 103; // 版本过低，不支持
int CODE_SHARE_BY_INTENT_FAIL = 105; // 使用 Intent 分享失败
int CODE_STORAGE_READ_ERROR = 106; // 没有读存储的权限，获取分享缩略图将会失败
int CODE_STORAGE_WRITE_ERROR = 107; // 没有写存储的权限，微博分享视频copy操作将会失败
int CODE_FILE_NOT_FOUND = 108; // 文件不存在
int CODE_SDK_ERROR = 109; // sdk 返回错误
int CODE_REQUEST_ERROR = 110; // 网络请求发生错误
int CODE_CANNOT_OPEN_ERROR = 111; // 无法启动 app
int CODE_PARSE_ERROR = 112; // 数据解析错误
int CODE_IMAGE_COMPRESS_ERROR = 113; // 图片压缩失败
int CODE_PARAM_ERROR = 114; // 参数错误
int CODE_SDK_INIT_ERROR = 115; // SocialSdk 初始化错误
int CODE_PREPARE_BG_ERROR = 116; // 执行 prepareOnBackground 时错误
int CODE_NOT_SUPPORT = 117; // 不支持
```

例如你可以这么做：

```java
listener = new OnShareListener() {
	...
    @Override
    public void onFailure(SocialError e) {
        showMsg("分享失败  " + e.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (e.getCode() == SocialError.CODE_STORAGE_READ_ERROR) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else if (e.getCode() == SocialError.CODE_STORAGE_WRITE_ERROR) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }
};
```

## 扩展新的平台？

参考这里 [HuaweiPlatform.java](https://github.com/chendongMarch/SocialSdkLibrary/blob/master/app/src/main/java/com/babypat/platform/HuaweiPlatform.java)

向 `SocialSdk` 注册构建工厂：

```java
SocialSdk.addPlatform(new HuaweiPlatform.Factory());
```


## 其他

- 生命周期绑定避免内存泄漏

`SocialSdk` 内部对生命周期有自动的管理，每次登录分享结束了都会回收掉所有的资源；
发起登录分享的 `Activity` 建议实现 `LifecycleOwner` 接口，可以直接使用 `AppCompatActivity`，内部会做生命周期的绑定，避免内存泄漏的发生；




## 相关文档

QQ：

- `libs/open_sdk_r6019_lite.jar`
- [QQ 登录分享文档](http://wiki.open.qq.com/wiki/QQ%E7%94%A8%E6%88%B7%E8%83%BD%E5%8A%9B)
- [QQ SDK 下载](http://wiki.open.qq.com/wiki/mobile/SDK%E4%B8%8B%E8%BD%BD)
- [QQ 设计资源](http://wiki.connect.qq.com/%E8%A7%86%E8%A7%89%E7%B4%A0%E6%9D%90%E4%B8%8B%E8%BD%BD)


微信：

- `com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.3.1`
- [分享与收藏文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317340&token=&lang=zh_CN)
- [微信登录文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN)
- [微信SDK](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419319167&token=&lang=zh_CN)
- [微信设计资源](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419319171&token=&lang=zh_CN)
- [分享与收藏文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317340&token=&lang=zh_CN)
- [微信登录文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN)
- [公告-取消 cancel 事件](https://open.weixin.qq.com/cgi-bin/announce?spm=a311a.9588098.0.0&action=getannouncement&key=11534138374cE6li&version=)


微博：

- `com.sina.weibo.sdk:core:4.3.7:openDefaultRelease@aar`
- [微博开放平台](http://open.weibo.com/wiki/%E7%A7%BB%E5%8A%A8%E5%BA%94%E7%94%A8%E4%BB%8B%E7%BB%8D)
- [GitHub(大多数资源还是在 git 上)](https://github.com/sinaweibosdk/weibo_android_sdk)
- [openApi 文档](http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI)
- [微博设计资源](http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9A%E6%A0%87%E8%AF%86%E4%B8%8B%E8%BD%BD)


钉钉：

- `com.alibaba.android:ddsharesdk:1.1.0`
- [钉钉分享文档](https://open-doc.dingtalk.com/docs/doc.htm?spm=a219a.7629140.0.0.15nVTL&treeId=178&articleId=104986&docType=1)
- [钉钉设计资源](https://open-doc.dingtalk.com/microapp/kn6zg7/tnrhmb)


