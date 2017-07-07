## SocialSdk



`SocialSdk` 提供微博、微信、QQ的登陆分享功能支持，使用 微博、QQ、微信 原生 SDK 接入，持续优化中...

由于项目中想要接入的平台因人而异，第三方 SDK 更新也比较频繁，因此没有对类库进行发布操作，下载之后直接依赖 `module` 即可，开放源码，这样也方便问题修复。

**SocialSdk** 主要对外开放三个类文件 `SocialSdk`，`ShareManager`，`LoginManager`。`SocialSdk` 用来完成基本配置的初始化操作，`ShareManager` 用来进行分享操作，`LoginManager` 用来进行登录操作。

```java
LoginManager.login(mActivity, LoginManager.TARGET_QQ, mOnLoginListener);

ShareManager.share(mActivity, ShareManager.TARGET_QQ_FRIENDS, imageObj, mOnShareListener);
```

更多请查看 **[详细说明 - Blog](http://cdevlab.top/article/3067853428/)**