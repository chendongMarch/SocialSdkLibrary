package com.march.socialsdk.common;

/**
 * CreateAt : 2017/5/19
 * Describe : 常量类
 *
 * @author chendong
 */
public class SocialConstants {

    public static final String QQ_CREATOR = "com.march.socialsdk.platform.qq.QQPlatform$Creator";
    public static final String WX_CREATOR = "com.march.socialsdk.platform.wechat.WxPlatform$Creator";
    public static final String WB_CREATOR = "com.march.socialsdk.platform.weibo.WbPlatform$Creator";
    public static final String DD_CREATOR = "com.march.socialsdk.platform.ding.DDPlatform$Creator";

    public static final String QQ_PKG = "com.tencent.mobileqq";
    public static final String WECHAT_PKG = "com.tencent.mm";
    public static final String SINA_PKG = "com.sina.weibo";
    public static final String DD_PKG = "com.alibaba.android.rimet";

    // 微信收藏
    public static final String WX_FAVORITE_PAGE = "com.tencent.mm.ui.tools.AddFavoriteUI";
    // 微信选择好友
    public static final String WX_FRIEND_PAGE = "com.tencent.mm.ui.tools.ShareImgUI";
    // 微信主界面
    public static final String WX_LAUNCH_PAGE = "com.tencent.mm.ui.LauncherUI";
    // 钉钉分享界面
    public static final String DD_FRIEND_PAGE = "com.alibaba.android.rimet.biz.BokuiActivity";


    public static final String QQ_QZONE_PAGE = "com.qzonex.module.maxvideo.activity.QzonePublishVideoActivity";// qq空间app
    public static final String QQ_BROWSER_FAST_TRANS_PAGE = "com.tencent.mtt.browser.share.inhost.FastSpreadEntryActivity";//qq浏览器跨屏穿越
    public static final String QQ_FRIENDS_PAGE = "com.tencent.mobileqq.activity.JumpActivity";//qq选择好友、群、我的电脑
    public static final String QQ_COMPUTER_FILE_PAGE = "com.tencent.mobileqq.activity.qfileJumpActivity";// 发送到我的电脑
    public static final String QQ_TRANSLATE_FACE_2_FACE_PAGE = "cooperation.qlink.QlinkShareJumpActivity";//qq面对面快传
    public static final String QQ_FAVORITE_PAGE = "cooperation.qqfav.widget.QfavJumpActivity";// 保存到qq收藏

    // 发送微博界面
    public static final String WB_COMPOSER_PAGE = "com.sina.weibo.composerinde.ComposerDispatchActivity";
    // 微博故事
    public static final String WB_STORY_PAGE = "com.sina.weibo.story.publisher.StoryDispatcher";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
//    public static final String REDIRECT_URL = "http://open.manfenmm.com/bbpp/app/weibo/common.php";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = "all";
//            "email,direct_messages_read,direct_messages_write,"
//                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
//                    + "follow_app_official_microblog," + "invitation_write";

}
