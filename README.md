
# SocialSDK


![](http://cdn1.showjoy.com/shop/images/20180828/MLI1YQGFQLZBRO3VKH6U1535432744013.png)

使用 **微博**、**QQ**、**微信**、**钉钉** 原生 `SDK` 接入，提供这些平台的登录、分享功能支持。针对业务逻辑对各个平台的接口进行封装，对外提供一致的表现，在减轻接入压力的同时，又能获得原生 `SDK` 最大的灵活性。

> 考虑到每个平台的 `SDK` 也在不断的更新，且每个项目的需求差异比较大，如可能只需要支持部分平台，因此没有对类库进行发布，请下载 `GitHub` 上的 `module` 自行依赖，在类库设计的过程中，每个平台都是独立的，如果只需要支持部分平台，只需要删除 `platform` 包下面对应的实现即可，不会对其他平台造成影响。

项目地址 : [GitHub - SocialSdkLibrary](https://github.com/chendongMarch/SocialSdkLibrary)

本文地址 ：[快速接入微信微博QQ钉钉原生登录分享](http://zfyx.coding.me/article/3067853428/)

🎉  2018.9.26 项目获得了第202颗🌟，感谢新同事补星 2 个 😄

🎉  2018.6.7 项目获得了第100颗🌟，最后一颗是我问同事要的🤦‍

🎉  2018.5.12 修复内存问题、功能扩展 [稳定版本 1.1.0](https://github.com/chendongMarch/SocialSdkLibrary/releases/tag/1.1.0)

🎉  2018.2.12 支持钉钉分享

🎉  2017.12.12 对代码进行简单重构并测试  [稳定版本 1.0.0](https://github.com/chendongMarch/SocialSdkLibrary/releases/tag/1.0.0)


<div style="width:100%;display: flex;height:30px;">

<img style="margin-right:20px;" src="https://badge.juejin.im/entry/5a793a405188257a82111092/likes.svg?style=flat-square"/>

<img style="margin-right:20px;"  src="https://img.shields.io/github/stars/chendongMarch/SocialSdkLibrary.svg"/>

<img  style="margin-right:20px;"  src="https://img.shields.io/github/forks/chendongMarch/SocialSdkLibrary.svg"/>

</div>

<!--more-->

## 优点

还在优化中...

🔥 简单：只需要关注几个管理类和相关数据的构造即可实现所需功能，不需要考虑复杂的授权和分享逻辑。

🔥 轻量：除了必须的第三方 `sdk` 之外，本项目只依赖了一个简单的异步任务的框架 `bolts (38k)`，后续会考虑也剔除掉，不引入无用依赖，保证与宿主项目高度统一。

🔥 全面：内部存储授权 `token`，避免多次授权；对 qq、微信、微博 做了完善的支持；

🔥 扩展性：

- 平台独立，项目以平台进行划分，各个平台之间完全独立，如果想仅支持部分平台，只需要删除 `platform` 包下该平台的具体实现即可。
- 请求、JSON 解析等功能可从外部注入代理，对一些功能进行自定义的扩展。
- 可以继承 `AbsPlatform` 接入其他平台分享，自定义扩展。

🔥 功能性：针对实际项目需求进行扩展，例如在分享前统一对分享数据提供一次重新构造的机会。

🔥 兼容性：

- 为多个平台提供外观一致的分享接口，若不支持，使用 `web` 分享兼容。
- 支持直接使用网络图片分享，内置自动下载功能。
- 使用 `Intent` 兼容不支持的数据模式，如支持本地视频分享，`qq` 的纯文字分享等等。

## 主要类文件

使用 **SocialSdk** 只需要关注以下几个文件：

👉️ `SocialSdk.java` 结合 `SocialConfig.java` 用来进行授权信息的配置。

👉️ `Target.java` 类是单独分离出来的常量类，指向了登录和分享的具体目标。

👉️ `LoginManager.java` 用来实现 qq、微信、微博第三方授权登录，内部存储 `accessToken`，无需多次授权，只要调用 `LoginManager.login()` 方法。

👉️️ `ShareManager.java` 用来实现 **8** 种数据类型、**4** 个平台、**8** 个渠道的分享，只要调用 `ShareManager.share()` 方法。


## gradle 配置

针对多方 `SDK` 的要求，对权限、和必要的界面、服务都已经在类库中进行了配置，当依赖该类库时，会自动合并，不过仍然还需要在项目的 `app/build.gradle` 中配置对应的 `qqId` 的 `manifestPlaceholders`，代码如下：

```gradle
defaultConfig {
	manifestPlaceholders = [qq_id: "11049xxxxx"]
}
```
关于 `manifestPlaceholders` 的使用

```bash
当使用 manifestPlaceholders = [qq_id: "11049xxxxx"] 的方式时，之前声明的所有 manifestPlaceholders 都会被替换掉，只保留最后的。

当使用 manifestPlaceholders.qq_id = "11049xxxxx" 的方式时，会在原来的 manifestPlaceholders 中追加新的，同时也保留以前的。

建议的方式是，在 defaultConfig 中使用直接赋值的方式，而在 buildTypes 和 Favors 中使用追加的方式，避免将之前的覆盖掉。
```


## 初始化

你需要在使用 SDK 之前进行初始化操作，建议放在 `Applicaton` 中进行。


```java
String qqAppId = getString(R.string.QQ_APP_ID);
String wxAppId = getString(R.string.WX_APP_ID);
String wxSecretKey = getString(R.string.WX_SECRET_KEY);
String sinaAppId = getString(R.string.SINA_APP_ID);
String ddAppId = getString(R.string.DD_APP_ID);
SocialSdkConfig config = SocialSdkConfig.create(this)
        // 开启调试
        .debug(true)
        // 配置钉钉
        .dd(ddAppId)
        // 配置qq
        .qq(qqAppId)
        // 配置wx
        .wechat(wxAppId, wxSecretKey)
        // 配置sina
        .sina(sinaAppId)
        // 配置Sina的RedirectUrl，有默认值，如果是官网默认的不需要设置
        .sinaRedirectUrl("http://open.manfenmm.com/bbpp/app/weibo/common.php")
        // 配置Sina授权scope,有默认值，默认值 all
        .sinaScope(SocialConstants.SCOPE)
        // 不加载钉钉和微博平台
        .disablePlatform(Target.PLATFORM_DD)
        .disablePlatform(Target.PLATFORM_WB)
        // 当缩略图因为各种原因无法获取时，将会使用默认图，避免分享中断
        .defImageResId(R.mipmap.ic_launcher_new);
// 👮 添加 config 数据，必须
SocialSdk.init(config);
// 👮 添加自定义的 json 解析，必须，参考 temp 文件夹下的实现
SocialSdk.setJsonAdapter(new GsonJsonAdapter());
// 👮 请求处理类，如果使用了微博的 openApi 分享，这个是必须的，参考 temp 文件夹下的实现
SocialSdk.setRequestAdapter(new OkHttpRequestAdapter());
```

## adapter

使用 `adapter` 这种模式主要参照了一些成熟的类库，目的是为了对外提供更好的扩展性，这部分内容可以关注 `SocialSdk.java`.

- `IJsonAdapter`，负责 `Json` 解析，为了保持和宿主项目 `json` 解析框架的统一，是必须自定义添加的（没有内置一个实现是因为使用自带的 `JsonObject` 解析实在麻烦，又不想内置一个三方库进来，采取的这种折衷方案），提供一个 `Gson` 下的实现仅供参考 - [GsonJsonAdapter.java](https://github.com/chendongMarch/SocialSdkLibrary/blob/master/temp/GsonJsonAdapter.java)

- `IRequestAdapter`，负责请求数据，目前微信的 `OAuth2` 授权和图片下载的相关请求都是使用 `IRequestAdapter` 代理，已经使用 `URLConnection` 内置了一个实现，如果你有自己的需求可以重写这部分，可以参考 - [OkHttpRequestAdapter.java](https://github.com/chendongMarch/SocialSdkLibrary/blob/b0b8559ff26136abbaaee9667bfc5c2bf54eedea/temp/OkHttpRequestAdapter.java)

## 登录功能

登陆功能支持三个平台，qq，微信，微博；

```java
// 3个平台
Target.LOGIN_QQ;
Target.LOGIN_WX;
Target.LOGIN_WB;
```

使用 `OnLoginListener` 监听登录返回结果，返回的 `LoginResult` 中主要包括登录类型，基本用户信息，令牌信息 3 部分。

```java
public class LoginResult {
    // 登陆的类型，对应 Target.LOGIN_QQ 等。。。
    private int             type;
    // 返回的基本用户信息
    // 针对登录类型可强转为 WbUser,WxUser,QQUser 来获取更加丰富的信息
    private BaseUser        mBaseUser;
    // 本次登陆的 token 信息，openid,unionid,token,expires_in
    private BaseAccessToken mBaseToken;
}

// 登陆结果监听
mOnLoginListener = new OnLoginListener() {
    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e(TAG, loginResult.toString());
    }
    @Override
    public void onCancel() {
        toast("登录取消");
    }
    @Override
    public void onException(PlatformException e) {
        toast("登录失败 " + e.toString());
    }
};

// 3个平台
Target.LOGIN_QQ;
Target.LOGIN_WX;
Target.LOGIN_WB;

// 唤醒登陆
LoginManager.login(mActivity, Target.LOGIN_QQ, mOnLoginListener);
```

清除授权 `token`，为了避免每次登录都要求用户打开授权界面重新点击授权的不好体验，类库里面对 `token` 进行了持久化的存储，当本地 `token` 没有过期时，直接使用这个 `token` 去请求用户信息，同时提供了清除本地 `token` 的方法。

```java
LoginManager.java

// 清除全部平台的 token
public static void clearAllToken(Context context)
// 清除指定平台的 token
public static void clearToken(Context context, @Target.LoginTarget int loginTarget)
```

## 分享功能


请仔细查看平台和数据类型中间的支持能力

![](http://cdn1.showjoy.com/images/b9/b9ffca33435c40d8b6e33914db0fa6da.png)

- 当 微博 使用 `openApi` 形式去分享时，可能有较长的延时，建议在生命周期中增加进度条显示，避免用户等待很久没有响应。

### 扩展支持

```java
// 发短信
ShareManager.sendSms(mActivity,"13612391817","msg body");
// 发邮件
ShareManager.sendEmail(mActivity,"1101873740@qq.com","subject","msg body");
// 打开渠道对应应用
ShareManager.openApp(mActivity,Target.PLATFORM_QQ);
```

### 8 种数据支持

分享支持 8 种类型的数据；如果某个平台不兼容某种类型的分享，将会使用 `web` 分享的方式代替；比如微信不支持 `app` 分享，分享出去之后时 `web` 分享的模式。支持的 8 种类型分别是：

> 1. 开启渠道对用的 app。
> 2. 分享文字。
> 3. 分享图片( jpg , png , gif )。
> 4. 分享 app。
> 5. 分享 web。
> 6. 分享 music。
> 7. 分享 video。
> 8. 分享本地 video，使用 Intent 方式唤醒。


### 8 个分享渠道

```java
// 支持的分享渠道
Target.SHARE_DD; // 钉钉好友
Target.SHARE_QQ_FRIENDS; // qq好友
Target.SHARE_QQ_ZONE; // qq空间
Target.SHARE_WX_FRIENDS; // 微信好友
Target.SHARE_WX_ZONE; // 微信朋友圈
Target.SHARE_WX_FAVORITE; // 微信收藏
Target.SHARE_WB; // 新浪微博
```

### 创建分享数据

分享时，我们首先要构造分享用的数据，`ShareObj` 对象提供了多种静态方法用来快速创建对应分享的类型的对象。

```java
// 测试用的路径
localImagePath = new File(Environment.getExternalStorageDirectory(), "1.jpg").getAbsolutePath();
localVideoPath = new File(Environment.getExternalStorageDirectory(), "video.mp4").getAbsolutePath();
localGifPath = new File(Environment.getExternalStorageDirectory(), "3.gif").getAbsolutePath();
netVideoPath = "http://7xtjec.com1.z0.glb.clouddn.com/export.mp4";
netImagePath = "http://7xtjec.com1.z0.glb.clouddn.com/token.png";
netMusicPath = "http://7xtjec.com1.z0.glb.clouddn.com/test_music.mp3";
netMusicPath = "http://mp3.haoduoge.com/sSocialSdkConfig/2017-05-19/1495207225.mp3";
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
ShareObj videoLocalObj = ShareObj.buildVideoObj("分享本地视频", "summary", localVideoPath);
// 分享音乐
ShareObj musicObj = ShareObj.buildMusicObj("分享音乐", "summary", localImagePath, targetUrl, netMusicPath, 10);
```

### 分享监听

分享结果，使用 `OnShareListener` 进行检测。`OnShareListener` 提供了丰富的方法来支持分享的各个阶段，关于分享对象重构的操作，在下一部分说明。

```
public class SimpleShareListener implements OnShareListener{
    @Override
    public void onStart(int shareTarget, ShareObj obj) {
        // 分享开始
    }
    @Override
    public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) {
        // 重构分享对象，不需要时返回 null 即可
        return null;
    }
    @Override
    public void onSuccess() {
        // 分享成功
    }
    @Override
    public void onFailure(SocialError e) {
        // 分享失败
    }
    @Override
    public void onCancel() {
        // 分享取消
    }
}
```

### 发起分享

```java
// 唤醒分享
ShareManager.share(mActivity, Target.SHARE_QQ_FRIENDS, imageObj, mOnShareListener);
```

### 重写分享对象

关于重写分享对象，其实提供一种能在分享之前对需要分享的 `ShareObj` 进行统一处理的机会，类似中间插一道自定义工序，比如可以用来解决网络图片无法分享，我们需要将它下载到本地，在进行分享，又比如图片分享出去之前加上 app 水印等操作。

主要是重写 `OnShareListener` 的 `onPrepareInBackground` 方法，这个方法会在分享之前首先执行，如果返回不是 `null`，将会使用新创建的 `ShareObj` 进行分享，另外由于考虑到可能进行耗时操作，这个方法是在子线程执行的。

```java
@Override
public ShareObj onPrepareInBackground(int shareTarget,ShareObj obj) {
    // 重构分享对象，不需要时返回 null 即可
    return null;
}
```

看一个实现，主要功能是在分享之前用来将网络图下载到本地然后更新 `ShareObj` 指向的图片地址，这样就可以支持网络图片的直接分享，当然，`SocialSdk` 目前已经支持网络图片的分享，这只是一个例子。

```java
public class MyShareListener extends SimpleShareListener {

    public static final String TAG = MyShareListener.class.getSimpleName();

    private Context       mContext;
    private LoadingDialog mLoadingDialog;

    public MyShareListener(Context context) {
        mContext = context;
        mLoadingDialog = new LoadingDialog(mContext);
    }

    @Override
    public void onStart(int shareTarget, ShareObj obj) {
        if (mLoadingDialog != null)
            mLoadingDialog.show();
    }

    @Override
    public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Exception{
        // 网络路径，先进行文件下载进行文件下载
        ShareObjHelper.prepareThumbImagePath(obj);
        // 分享照片且不是gif时加水印
        if (obj.getShareObjType() == ShareObj.SHARE_TYPE_IMAGE
                && !FileHelper.isGifFile(obj.getThumbImagePath())) {
            File thumbImageFile = new File(obj.getThumbImagePath());
            File saveFile = new File(Constants.THUMB_IMAGE_PATH, thumbImageFile.getName());
            if (!FileUtil.fileIsExist(saveFile.getAbsolutePath())) {
                ImageUtils.drawWaterMarkSync(mContext, obj.getThumbImagePath(), saveFile.getAbsolutePath(), false, false);
            }
            obj.setThumbImagePath(saveFile.getAbsolutePath());
        }
        return obj;
    }

    @Override
    public void onSuccess() {
        ToastUtil.show("分享成功");
    }

    @Override
    public void onFailure(SocialError e) {
        switch (e.getErrorCode()) {
            case SocialError.CODE_NOT_INSTALL:
                ToastUtil.show("应用未安装");
                break;
        }
        L.e(TAG, "分享失败" + e.toString());
    }

    @Override
    public void onCancel() {
        ToastUtil.show("分享取消");
    }
}
```

## 错误码

为了更好的统一分享失败时返回的异常，返回的所有异常都会有一个 `code`，可以根据不同的 `code` 定位问题和给出更友好的提示。


```java
CODE_COMMON_ERROR         = 101; // 通用错误，未归类
CODE_NOT_INSTALL          = 102; // 没有安装应用
CODE_VERSION_LOW          = 103; // 版本过低，不支持
CODE_SHARE_OBJ_VALID      = 104; // 分享的对象参数有问题
CODE_SHARE_BY_INTENT_FAIL = 105; // 使用 Intent 分享失败
CODE_STORAGE_READ_ERROR   = 106; // 没有读存储的权限，获取分享缩略图将会失败
CODE_STORAGE_WRITE_ERROR  = 107; // 没有写存储的权限，微博分享视频copy操作将会失败
CODE_FILE_NOT_FOUND       = 108; // 文件不存在
CODE_SDK_ERROR            = 109; // sdk 返回错误
CODE_REQUEST_ERROR        = 110; // 网络请求发生错误
CODE_CANNOT_OPEN_ERROR    = 111; // 无法启动 app
CODE_PARSE_ERROR          = 112; // 数据解析错误
CODE_IMAGE_COMPRESS_ERROR = 113; // 图片压缩失败
```

例如你可以这么做：

```java
mOnShareListener = new SimpleShareListener() {
    @Override
    public void onFailure(SocialError e) {
        showMsg("分享失败  " + e.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (e.getErrorCode() == SocialError.CODE_STORAGE_READ_ERROR) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else if (e.getErrorCode() == SocialError.CODE_STORAGE_WRITE_ERROR) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }
};
```
