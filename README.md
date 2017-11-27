# SocialSdk


「`SocialSdk`」 提供微博、微信、QQ的登陆分享功能支持，使用 微博、QQ、微信 原生 SDK 接入，持续优化中...

由于项目中想要接入的平台因人而异，第三方 SDK 更新也比较频繁，因此没有对类库进行发布操作，下载之后直接依赖 `module` 即可，这样也方便问题修复。

项目地址 : [GitHub - SocialSdkLibrary](https://github.com/chendongMarch/SocialSdkLibrary)

<!--more-->

## 介绍

简单：只需要关注几个管理类和相关数据的构造，不需要考虑复杂的授权逻辑。

轻量：除了必须的第三方 sdk 之外，本项目只依赖了一个简单的异步任务的框架-bolts，后续会考虑也剔除掉，不引入无用依赖，保证与宿主项目统一。

全面：内部存储授权 token，避免多次授权；对qq、微信、微博做了完善的支持，同时兼容了各平台分享数据类型的差异，还对本地视频这种原本不支持分享的类型使用本地分享进行了内部扩展。

扩展性：项目以功能进行划分，各个平台之间互相独立，如果想仅支持部分平台，只需要删除某个平台的具体实现即可。

功能性：针对实际项目需求进行扩展，例如在分享前统一对分享数据提供一次重新构造的机会。

使用 **SocialSdk** 只需要关注以下几个文件：

> `SocialSdk` 结合 `SocialConfig` 用来进行授权信息的配置。

>  `Target` 类是单独分离出来的常量类，指向了登录和分享的具体目标。

> `LoginManager` 用来实现 qq、微信、微博第三方授权登录，内部存储 `accessToken`，无需多次授权，只要调用 `LoginManager.login()` 方法。

> `ShareManager` 用来实现 9 种数据类型、3 大平台、7 个渠道的分享，只要调用 `ShareManager.share()` 方法。


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

为了减轻类库的体积，兼容不同的 `json` 解析方案，将 `json` 解析使用接口的方式注入，你需要实现 `IJsonAdapter` 接口，
根据项目中具体使用的 `json` 解析库进行数据的解析。提供一个 `Gson` 的实现作为参考 [GsonJsonAdapter.java](https://github.com/chendongMarch/SocialSdkLibrary/blob/master/sample/GsonJsonAdapter.java)

```java
String qqAppId = getString(R.string.QQ_APPID);
String wxAppId = getString(R.string.WEICHAT_APPID);
String wxSecretKey = getString(R.string.WEICHAT_APPKEY);
String sinaAppId = getString(R.string.SINA_APPKEY);

SocialSdkConfig config = new SocialSdkConfig(this)
        // 配置qq
        .qq(qqAppId)
        // 配置wx
        .wechat(wxAppId, wxSecretKey)
        // 配置sina
        .sina(sinaAppId)
        // 配置Sina的RedirectUrl，有默认值，如果是官网默认的不需要设置
        .sinaRedirectUrl("http://open.manfenmm.cxxxxxxx")
        // 配置Sina授权scope,有默认值，默认值 all
        .sinaScope(SocialConstants.SCOPE);
// 添加自定义的json解析，必须
SocialSdk.addJsonAdapter(new GsonJsonAdapter());

SocialSdk.init(config);
```

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
    public void onLoginSucceed(LoginResult loginResult) {
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


## 分享功能


### 扩展支持

```java
// 发短信
ShareManager.sendSms(mActivity,"13612391817","msg body");
// 发邮件
ShareManager.sendEmail(mActivity,"1101873740@qq.com","subject","msg body");
// 打开渠道对应应用
ShareManager.openApp(mActivity,Target.SHARE_QQ_FRIENDS);
```

### 9 种数据支持

分享支持 9 种类型的数据；如果某个平台不兼容某种类型的分享，将会使用 `web` 分享的方式代替；比如微信不支持 `app` 分享，分享出去之后时 `web` 分享的模式。支持的 9 种类型分别是：

> 1. 开启渠道对用的 app。
> 2. 分享文字。
> 3. 分享图片( jpg , png , gif )。
> 4. 分享 app。
> 5. 分享 web。
> 6. 分享 music。
> 7. 分享 video。
> 8. 分享本地 video，使用 Intent 方式唤醒，支持 qq、微信 好友分享。
> 9. 分享 voice，(sina 专有，其他平台使用 web 分享)


### 7 个分享渠道

```java
// 支持的分享渠道
Target.SHARE_QQ_FRIENDS; // qq好友
Target.SHARE_QQ_ZONE; // qq空间
Target.SHARE_WX_FRIENDS; // 微信好友
Target.SHARE_WX_ZONE; // 微信朋友圈
Target.SHARE_WX_FAVORITE; // 微信收藏
Target.SHARE_WB_NORMAL; // 新浪微博
Target.SHARE_WB_OPENAPI; // 新浪微博openApi分享，使用该方法分享图片时微博后面会带一个小尾巴，可以点击进入官微
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
// 分享本地视频，使用 Intent 方式唤醒，支持 qq、微信 好友分享
ShareObj videoLocalObj = ShareObj.buildVideoObjByLocalPath(localVideoPath);
// 分享音乐
ShareObj musicObj = ShareObj.buildMusicObj("分享音乐", "summary", localImagePath, targetUrl, netMusicPath, 10);
// 分享声音，微博特有，其他平台以web方式分享
ShareObj voiceObj = ShareObj.buildVoiceObj("分享声音", "summary", localImagePath, targetUrl, netMusicPath, 10);
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
    public void onFailure(SocialException e) {
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

主要是重写 `OnShareListener` 的 `onPrepareInBackground` 方法，这个方法会在分享之前首先执行，如果返回不是 null，将会使用新创建的 `ShareObj` 进行分享，另外由于考虑到可能进行耗时操作，这个方法是在子线程执行的。

```java
@Override
public ShareObj onPrepareInBackground(int shareTarget,ShareObj obj) {
    // 重构分享对象，不需要时返回 null 即可
    return null;
}
```

看一个基本的实例，其中 `ShareObjHelper.prepareThumbImagePath(obj);` 是 SDK 内部下载文件的一个方法的封装，用来将网络图下载到本地然后更新 `ShareObj` 指向的图片地址，你可以直接使用它。

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
    public void onFailure(SocialException e) {
        switch (e.getErrorCode()) {
            case SocialException.CODE_NOT_INSTALL:
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
