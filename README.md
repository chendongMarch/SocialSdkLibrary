
# SocialSDK

![](http://cdn1.showjoy.com/shop/images/20180828/MLI1YQGFQLZBRO3VKH6U1535432744013.png)



使用 **微博**、**QQ**、**微信**、**钉钉** 原生 `SDK` 接入，提供这些平台的登录、分享功能支持。针对业务逻辑对各个平台的接口进行封装，对外提供一致的表现，在减轻接入压力的同时，又能获得原生 `SDK` 最大的灵活性。

项目地址 : [GitHub - SocialSdkLibrary](https://github.com/chendongMarch/SocialSdkLibrary)

博客地址 ：[快速接入微信微博QQ钉钉原生登录分享](http://zfyx.coding.me/article/3067853428/)

<div style="width:100%;display: flex;height:50px;margin-bottom:20px;">

<img  style="margin-right:50px;"  src="https://img.shields.io/circleci/project/github/badges/shields/master.svg"/>

<img  style="margin-right:50px;"  src="https://img.shields.io/badge/version-0.0.4-blue.svg?maxAge=2592000"/>

<img style="margin-right:50px;"  src="https://img.shields.io/github/stars/chendongMarch/SocialSdkLibrary.svg"/>

<img  style="margin-right:50px;"  src="https://img.shields.io/github/forks/chendongMarch/SocialSdkLibrary.svg"/>

<img style="margin-right:50px;" src="https://badge.juejin.im/entry/5a793a405188257a82111092/likes.svg?style=flat-square"/>

</div>


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

🔥 功能性：面向常见的需求场景设计：

- 良好的扩展性，容易增加新平台实现，例如华为联运登录接入等；
- 一致的外观，封装各平台内部实现，对外暴露统一的接口，若不支持，使用 `web` 分享兼容；
- 无法支持的数据类型，使用 `Intent` 唤醒分享，如支持本地视频分享，`qq` 的纯文字分享等等；
- 支持直接使用网络图片分享，内置自动下载和图片缓存功能；
- 图片 下载/加载 失败时降级使用资源图，避免分享被中断；
- 在分享前可统一重写数据对象，用来做分享的切面，支持图片加水印等类似需求；
- 支持短信、邮件、粘贴板等系统分享平台；
- 持久化存储 `token` 避免多次授权，可选择有效时长；
- 针对选择留在三方应用的业务场景，可将其回调为成功或失败。

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
        classpath 'com.zfy.social:social-sdk-plugin:0.0.4'
    }
}

allprojects {
    repositories {
        maven { url "https://dl.bintray.com/zfy/maven" }
    }
}
```

**STEP2**: 配置参数，注意与 `android` 同级

> app / build.gralde

```js
// 引用插件
apply plugin: 'socialsdk'

// android 配置模块
android {
	...
}

// socialsdk 配置模块
socialsdk {
    wx {
        appId = 'wx4b8db***5b195c3'
        appSecret = '0a3cb007291d0e5***3654f499171'
        onlyAuthCode = false // 微信授权仅返回 code
    }
    qq {
        appId = '1104***200'
        appSecret = 'A6AqtY***g59yQ4N'
    }
    wb {
        appId = '218***998'
        url = 'http://open.manfenmm.com/***/***'
    }
    dd {
        appId = 'dingo***wrefwjeumuof'
    }
    local false // 使用本地依赖
}
```

**STEP3**：初始化

```java
SocialOptions options = new SocialOptions.Builder(this)
        // 调试模式，开启 log 输出
        .debug(true)
        // 加载缩略图失败时，降级使用资源图
        .failImgRes(R.mipmap.ic_launcher_new)
        // token 保留时间，单位小时，默认不保留
        .tokenExpiresHours(24)
        // 分享如果停留在第三放将会返回成功，默认返回失败
        .shareSuccessIfStay(true)
        // 添加自定义的 json 解析
        .jsonAdapter(new GsonJsonAdapter())
        // 请求处理类，如果使用了微博的 openApi 分享，这个是必须的
        .requestAdapter(new OkHttpRequestAdapter())
        // 构建
        .build();
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
public class LoginResult {
    // 登陆的类型，对应 Target.LOGIN_QQ 等。。。
    private int target;
    // 返回的基本用户信息
    // 针对登录类型可强转为 WbUser,WxUser,QQUser 来获取更加丰富的信息
    private SocialUser socialUser;
    // 本次登陆的 token 信息，openId, unionId,token,expires_in
    private AccessToken accessToken;
    // 授权码，如果 onlyAuthCode 为 true, 将会返回它
    private String wxAuthCode;
}
```
登录时需要设置登录回调：

```java
OnLoginListener listener = new OnLoginListener() {
    @Override
    public void onStart() {
        // 当登录开始时触发
    }
    @Override
    public void onSuccess(LoginResult result) {
        // 登录成功，获取用户信息
        SocialUser socialUser = result.getSocialUser();
    }
    @Override
    public void onCancel() {
        // 登录取消
    }
    @Override
    public void onFailure(SocialError e) {
        // 登录失败
    }
};
```

获取更多用户信息：

```java
SocialUser socialUser = loginResult.getSocialUser();
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

使用 `OnShareListener` 作为监听分享回调；

```
OnShareListener listener = new OnShareListener() {
    @Override
    public void onStart(int shareTarget, ShareObj obj) {
        // 分享开始
    }
    @Override
    public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Except
        // 重写分享对象，例如给分享出去的图片加水印等
        return null;
    }
    @Override
    public void onSuccess(int target) {
        // 分享成功
    }
    @Override
    public void onFailure(SocialError e) {
        // 分享失败
    }
    @Override
    public void onCancel() {
        // 分享被取消
    }
};
```

### 发起分享

```java
// 唤醒分享
ShareManager.share(mActivity, Target.SHARE_QQ_FRIENDS, imageObj, mOnShareListener);
```

### 重写分享对象

关于重写分享对象，其实提供一种能在分享之前对需要分享的 `ShareObj` 进行统一处理的机会，类似分享功能的一个切面，比如可以用来解决网络图片无法分享，我们需要将它下载到本地，在进行分享，又比如图片分享出去之前加上 app 水印等操作。

主要是重写 `OnShareListener` 的 `onPrepareInBackground` 方法，这个方法会在分享之前首先执行，如果返回不是 `null`，将会使用新创建的 `ShareObj` 进行分享，另外由于考虑到可能进行耗时操作，这个方法是在子线程执行的。

```java
@Override
public ShareObj onPrepareInBackground(int shareTarget,ShareObj obj) {
    // 重构分享对象，不需要时返回 null 即可
    obj.setTitle(obj.getTitle() + "/哈哈哈");
    return obj;
}
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
